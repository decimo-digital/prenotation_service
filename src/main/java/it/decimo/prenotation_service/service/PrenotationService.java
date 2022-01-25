package it.decimo.prenotation_service.service;

import it.decimo.prenotation_service.connectors.MerchantServiceConnector;
import it.decimo.prenotation_service.dto.Merchant;
import it.decimo.prenotation_service.dto.PrenotationRequestDto;
import it.decimo.prenotation_service.exception.*;
import it.decimo.prenotation_service.model.Prenotation;
import it.decimo.prenotation_service.model.UserPrenotation;
import it.decimo.prenotation_service.repository.PrenotationRepository;
import it.decimo.prenotation_service.repository.UserPrenotationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PrenotationService {
    private final PrenotationRepository prenotationRepository;
    private final UserPrenotationRepository userPrenotationRepository;
    private final MerchantServiceConnector merchantServiceConnector;

    public PrenotationService(PrenotationRepository prenotationRepository, UserPrenotationRepository userPrenotationRepository, MerchantServiceConnector merchantServiceConnector) {
        this.prenotationRepository = prenotationRepository;
        this.userPrenotationRepository = userPrenotationRepository;
        this.merchantServiceConnector = merchantServiceConnector;
    }

    /**
     * Effettua un controllo sul numero di posti liberi di un dato locale
     *
     * @param merchantId Il merchant a cui si sta prenotando
     * @param seats      Il numero di posti da prenotare
     * @return {@literal true} se il locale ha abbastanza posti per accettare la
     * prenotazione, {@literal false} altrimenti
     */
    private boolean hasEnoughFreeSeats(int merchantId, int seats) {
        try {
            final var data = merchantServiceConnector.getMerchant(merchantId);
            log.info("Merchant {} has {} free seats, {} seats requested", merchantId, data.getFreeSeats(), seats);
            return data.getFreeSeats() >= seats;
        } catch (Exception e) {
            log.error("Failed to retrieve merchant {}", merchantId, e);
            return false;
        }
    }

    /**
     * Effettua una prenotazione presso un certo locale
     *
     * @return Se viene effettuata correttamente, l'istanza della prenotazione
     * @throws NotEnoughSeatsException Se il locale scelto non ha abbastanza posti
     *                                 liberi per ospitare la prenotazione
     * @throws NotFoundException       Se non esiste nessun locale con l'id
     *                                 specificato
     */

    public Prenotation makePrenotation(PrenotationRequestDto dto)
            throws NotEnoughSeatsException, NotFoundException {

        if (!hasEnoughFreeSeats(dto.getMerchantId(), dto.getSeatsAmount())) {
            throw new NotEnoughSeatsException();
        }

        log.info("User {} is prenotating {} seats to {}", dto.getRequesterId(), dto.getSeatsAmount(),
                dto.getMerchantId());

        Prenotation prenotation = Prenotation.builder()
                .merchantId(dto.getMerchantId())
                .amount(dto.getSeatsAmount()).dateOfPrenotation(dto.getDate())
                .enabled(true)
                .owner(dto.getRequesterId())
                .build();

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
     * @return la prenotazione modificata
     * @throws NotFoundException      Se non esiste nessuna prenotazione con l'id
     *                                specificato
     * @throws NotAuthorizedException Se l'utente non è il proprietario della
     *                                prenotazione
     */
    public Prenotation patchPrenotation(Prenotation prenotation, int requesterId)
            throws NotFoundException, NotAuthorizedException {
        final var saved = prenotationRepository.findById(prenotation.getId())
                .orElseThrow(() -> new NotFoundException("La prenotazione non esiste"));


        log.info("Updating prenotation {}", prenotation.getId());

        if (prenotation.getAmount() != null) {
            if (!hasEnoughFreeSeats(prenotation.getMerchantId(), prenotation.getAmount())) {
                log.info("User {} tried to increment seats to {} but merchant {} hasn't enough free space", requesterId, prenotation.getMerchantId(), prenotation.getAmount());
            } else {
                log.info("User {} is updating prenotation {} with {} seats", requesterId, prenotation.getId(), prenotation.getAmount());
                saved.setAmount(prenotation.getAmount());
            }
        }

        if (prenotation.getDate() != null) {
            saved.setDate(prenotation.getDate());
        }

        if (prenotation.getDateOfPrenotation() != null) {
            saved.setDateOfPrenotation(prenotation.getDateOfPrenotation().getTime());
        }

        if (!prenotation.getEnabled()) {
            saved.setEnabled(false);
            log.info("User {} deleted prenotation {}", requesterId, prenotation.getId());
        }

        return prenotationRepository.save(saved);
    }

    /**
     * Elimina una prenotazione dato il suo io
     *
     * @param prenotationId l'id della prenotazione da eliminare
     * @param requesterId   L'utente che ha richiesto la cancellazione (deve esserne il proprietario)
     * @throws NotFoundException      Se non esiste nessuna prenotazione con l'id specificato
     * @throws NotAuthorizedException Se l'utente non è il proprietario della prenotazione
     */
    public void deletePrenotation(int prenotationId, int requesterId) throws NotFoundException, NotAuthorizedException {
        log.info("User {} is trying to delete prenotation {}", requesterId, prenotationId);
        final var prenotation = prenotationRepository.findById(prenotationId);
        if (prenotation.isEmpty()) {
            log.warn("Prenotation {} was not found", prenotationId);
            throw new NotFoundException("La prenotazione non esiste");
        }

        if (prenotation.get().getOwner() != requesterId) {
            log.warn("User {} tried to delete prenotation {} without permissions", requesterId, prenotationId);
            throw new NotAuthorizedException("L'utente non può cancellare la prenotazione");
        }

        var modified = prenotation.get();
        modified.setEnabled(false);
        prenotationRepository.save(modified);
    }

    /**
     * Ritorna le prenotazioni effettuate da un certo utente
     *
     * @param userId L'id dell'utente che ha effettuato le prenotazioni
     * @return La lista delle prenotazioni effettuate
     */
    public Collection<Prenotation> getPrenotationsForUser(int userId) {
        log.info("Getting prenotations for user {}", userId);
        return userPrenotationRepository.findAllByUser(userId).stream()
                .map(up -> prenotationRepository.findById(up.getPrenotation()).orElse(null))
                .filter(Objects::nonNull)
                .peek((prenotation) -> prenotation.setValid(isPrenotationValid(prenotation)))
                .collect(Collectors.toSet());
    }

    /**
     * Recupera le prenotazioni che sono state effettuate ad un determinato
     *
     * @param merchantId L'id del locale di cui ci interessano le prenotazioni
     * @throws NotFoundException se non esiste nessun locale con l'id
     *                           specificato
     */
    public Collection<Prenotation> getPrenotationsForMerchant(int merchantId)
            throws NotFoundException {
        log.info("Requesting prenotations for merchant {}", merchantId);
        try {
            Merchant merchant = merchantServiceConnector.getMerchant(merchantId);

            log.info("Collecting prenotations for merchant {}", merchantId);

            return prenotationRepository.findAllByMerchantId(merchantId).stream()
                    .peek(prenotation -> prenotation.setValid(isPrenotationValid(prenotation)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to retrieve prenotations for merchant {}", merchantId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Aggiunge l'utente specificato alla lista di prenotazioni di una certa
     * prenotazione
     *
     * @param prenotationId L'id della prenotazione a cui aggiungere l'utente
     * @param userId        L'id dell'utente da aggiungere
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
        log.info("Adding user {} to prenotation {}", userId, prenotationId);

        final var prenotation = prenotationRepository.findById(prenotationId)
                .orElseThrow(() -> new NotFoundException("Missing prenotation of id " + prenotationId));

        if (prenotation.getOwner() != requesterId) {
            log.error("User {} is not the owner of prenotation {}", requesterId, prenotationId);
            throw new NotAuthorizedException("The requester user is not the prenotation's owner");
        }

        if (!isPrenotationValid(prenotation)) {
            log.error("Prenotation {} is expired", prenotationId);
            throw new PrenotationExpiredException("The prenotation is not expired");
        }

        if (userPrenotationRepository.findAllByUser(userId).stream()
                .anyMatch(p -> p.getPrenotation() == prenotationId)) {
            log.error("User {} is already prenotated for prenotation {}", userId, prenotationId);
            throw new AlreadyPrenotedException("User already prenotated");
        }

        userPrenotationRepository.save(new UserPrenotation(prenotationId, userId));
    }

    /**
     * Controlla se l'utente richiesto ha i permessi per rimuovere una prenotazione
     * <p>
     * Per avere i permessi, l'utente deve essere il proprietario della prenotazione oppure dev'essere il
     * proprietario del merchant presso il quale è stata effettuata la prenotazione
     *
     * @param userId        L'utente che sta richiedendo la rimozione
     * @param prenotationId L'id della prenotazione da rimuovere
     * @return true se l'utente ha i permessi, false altrimenti
     * @throws NotFoundException Se non esiste nessuna prenotazione con l'id specificato
     */
    private boolean canRemovePrenotation(int userId, int prenotationId) throws NotFoundException {
        final var prenotation = prenotationRepository.findById(prenotationId);
        if (prenotation.isEmpty()) {
            throw new NotFoundException("Prenotation " + prenotationId + " not found");
        }

        if (prenotation.get().getOwner() == userId) {
            return true;
        } else {
            final var merchant = merchantServiceConnector.getMerchant(prenotation.get().getMerchantId());
            if (merchant.getOwner() == userId) {
                log.info("User {} is the owner of merchant {}", userId, merchant.getOwner());
                return true;
            }
        }

        log.info("User {} is not the owner of prenotation {} nor the owner of the merchant {}", userId, prenotationId, prenotation.get().getMerchantId());
        return false;
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
