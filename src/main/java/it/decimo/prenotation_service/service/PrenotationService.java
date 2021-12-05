package it.decimo.prenotation_service.service;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.decimo.prenotation_service.dto.PrenotationRequestDto;
import it.decimo.prenotation_service.exception.AlreadyPrenotedException;
import it.decimo.prenotation_service.exception.NotAuthorizedException;
import it.decimo.prenotation_service.exception.NotEnoughSeatsException;
import it.decimo.prenotation_service.exception.NotFoundException;
import it.decimo.prenotation_service.exception.PrenotationExpiredException;
import it.decimo.prenotation_service.model.Prenotation;
import it.decimo.prenotation_service.model.UserPrenotation;
import it.decimo.prenotation_service.repository.CustomRepository;
import it.decimo.prenotation_service.repository.MerchantDataRepository;
import it.decimo.prenotation_service.repository.PrenotationRepository;
import it.decimo.prenotation_service.repository.UserPrenotationRepository;
import it.decimo.prenotation_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PrenotationService {

        @Autowired
        private PrenotationRepository prenotationRepository;
        @Autowired
        private UserPrenotationRepository userPrenotationRepository;
        @Autowired
        private MerchantDataRepository merchantDataRepository;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private CustomRepository customRepository;

        /**
         * Effettua un controllo sul numero di posti liberi di un dato locale
         * 
         * @param dto I dati della prenotazione da effettuare
         * @return {@literal true} se il locale ha abbastanza posti per accettare la
         *         prenotazione, {@literal false} altrimenti
         * 
         * @throws NotFoundException Se il locale non è stato trovato
         */
        private boolean hasEnoughFreeSeats(PrenotationRequestDto dto) throws NotFoundException {

                final var merchantData = merchantDataRepository.findById(dto.getMerchantId()).orElseThrow(
                                () -> new NotFoundException("Merchant " + dto.getMerchantId() + " doesn't exists"));

                final var data = customRepository.getMerchantData(dto.getMerchantId());

                return merchantData.getTotalSeats() <= data.getTotalSeats();
        }

        /**
         * Effettua una prenotazione presso un certo locale
         * 
         * @param merchantId  Il locale verso il quale bisogna prenotare
         * @param toPrenotate Quanti posti sono da allocare alla prenotazione
         * @param userId      L'id dell'utente che sta richiedendo la prenotazione
         * 
         * @return Se viene effettuata correttamente, l'istanza della prenotazione
         * 
         * @throws MissingTableException   Se non vi è nessun tavolo che permette di
         *                                 effettuare la prenotazione
         * @throws NotEnoughSeatsException Se il locale scelto non ha abbastanza posti
         *                                 liberi per ospitare la prenotazione
         * @throws NotFoundException       Se non esiste nessun locale con l'id
         *                                 specificato
         */

        public Prenotation makePrenotation(PrenotationRequestDto dto)
                        throws NotEnoughSeatsException, NotFoundException {

                if (!hasEnoughFreeSeats(dto)) {
                        throw new NotEnoughSeatsException();
                }

                log.info("User {} is prenotation {} seats to {}", dto.getRequesterId(), dto.getSeatsAmount(),
                                dto.getMerchantId());

                Prenotation prenotation = Prenotation.builder().merchantId(dto.getMerchantId())
                                .amount(dto.getSeatsAmount()).dateOfPrenotation(dto.getDate())
                                .owner(dto.getRequesterId()).build();

                // Imposta la data in formato sql per filtrare le query
                prenotation.setDate(new java.sql.Date(dto.getDate()));

                var savedPrenotation = prenotationRepository.save(prenotation);
                log.info("Saved prenotation of id {}", savedPrenotation.getId());

                UserPrenotation userPrenotation = new UserPrenotation(savedPrenotation.getId(), dto.getRequesterId());

                log.info("Adding user {} to prenotation {}", dto.getRequesterId(), savedPrenotation.getId());
                userPrenotationRepository.save(userPrenotation);
                log.info("Added prenotation to user {}", dto.getRequesterId());

                return savedPrenotation;
        }

        /**
         * Modifica la prenotazione con il body nuovo
         * 
         * @param prenotation Le modifiche da apportare alla prenotazione
         * @param requesterId L'utente che ha richiesto la modifica
         * 
         * @throws NotFoundException      Se non esiste nessuna prenotazione con l'id
         *                                specificato
         * @throws NotAuthorizedException Se l'utente non è il proprietario della
         *                                prenotazione
         * 
         * @return la prenotazione modificata
         */
        public Prenotation patchPrenotation(Prenotation prenotation, int requesterId)
                        throws NotFoundException, NotAuthorizedException {
                final var saved = prenotationRepository.findById(prenotation.getId());
                if (saved.isPresent()) {
                        if (saved.get().getOwner() != requesterId) {
                                throw new NotAuthorizedException("L'utente non può modificare la prenotazione");
                        }

                        return prenotationRepository.save(prenotation);

                } else {
                        throw new NotFoundException("La prenotazione non esiste");
                }

        }

        /**
         * Ritorna le prenotazioni effettuate da un certo utente
         * 
         * @param userId L'id dell'utente che ha effettuato le prenotazioni
         * @return La lista delle prenotazioni effettuate
         */
        public Collection<Prenotation> getPrenotations(int userId) {
                return userPrenotationRepository.findAllByUser(userId).stream()
                                .map(up -> prenotationRepository.findById(up.getPrenotation()).orElse(null))
                                .filter(p -> p != null).map((prenotation) -> {
                                        prenotation.setValid(isPrenotationValid(prenotation));
                                        return prenotation;
                                }).collect(Collectors.toSet());
        }

        /**
         * Aggiunge l'utente specificato alla lista di prenotazioni di una certa
         * prenotazione
         * 
         * @param prenotationId L'id della prenotazione a cui aggiungere l'utente
         * @param userId        L'id dell'utente da aggiungere
         * 
         * @throws NotFoundException           Se non esiste nessuna prenotazione con
         *                                     l'id
         * @throws NotAuthorizedException      Se l'utente non è autorizzato ad
         *                                     aggiungere utenti alla prenotazione
         * @throws AlreadyPrenotedException    Se l'utente è già stato aggiunto alla
         *                                     prenotazione
         * @throws PrenotationExpiredException Se la prenotazione è scaduta
         */
        public void addUserToPrenotation(int requesterId, int prenotationId, int userId) throws NotFoundException,
                        NotAuthorizedException, PrenotationExpiredException, AlreadyPrenotedException {

                userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User to add not found"));

                final var prenotation = prenotationRepository.findById(prenotationId)
                                .orElseThrow(() -> new NotFoundException("Missing prenotation of id " + prenotationId));

                if (prenotation.getOwner() != requesterId)
                        throw new NotAuthorizedException("The requester user is not the prenotation's owner");

                if (!isPrenotationValid(prenotation))
                        throw new PrenotationExpiredException("The prenotation is not expired");

                if (userPrenotationRepository.findAllByUser(userId).stream()
                                .anyMatch(p -> p.getPrenotation() == prenotationId))
                        throw new AlreadyPrenotedException("User already prenotated");

                userPrenotationRepository.save(new UserPrenotation(prenotationId, userId));
        }

        /**
         * Controlla se la prenotazione passata per parametro è una prenotazione valida.
         * Per essere valida non dev'essere trascorsa più di mezz'ora dalla data di
         * creazione.
         * 
         * @param prenotation La prenotazione da controllare
         */
        public boolean isPrenotationValid(Prenotation prenotation) {
                final var endDate = prenotation.getDateOfPrenotation().toInstant().plus(30, ChronoUnit.MINUTES);
                return endDate.isAfter(new Date().toInstant());
        }
}
