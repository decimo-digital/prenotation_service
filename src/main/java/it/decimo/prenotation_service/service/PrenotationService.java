package it.decimo.prenotation_service.service;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.decimo.prenotation_service.dto.PrenotationRequestDto;
import it.decimo.prenotation_service.exception.NotAuthorizedException;
import it.decimo.prenotation_service.exception.NotEnoughSeatsException;
import it.decimo.prenotation_service.exception.NotFoundException;
import it.decimo.prenotation_service.model.Prenotation;
import it.decimo.prenotation_service.model.UserPrenotation;
import it.decimo.prenotation_service.repository.MerchantDataRepository;
import it.decimo.prenotation_service.repository.PrenotationRepository;
import it.decimo.prenotation_service.repository.UserPrenotationRepository;
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

        final var data = merchantDataRepository.findById(dto.getMerchantId())
                .orElseThrow(() -> new NotFoundException("Merchant " + dto.getMerchantId() + " doesn't exists"));

        final var calendar = Calendar.getInstance();
        calendar.setTime(dto.getDate());

        final var year = calendar.get(Calendar.YEAR);
        final var month = calendar.get(Calendar.MONTH);
        final var day = calendar.get(Calendar.DAY_OF_MONTH);

        // Recupera le prenotazioni effettaute per il giorno specificato
        final var prenotations = prenotationRepository.findByPrenotationDateAndMerchantId(year, month, day,
                dto.getMerchantId());

        log.info("Retrieved {} prenotation for merchant {} on date {}", prenotations.size(), dto.getMerchantId(),
                dto.getDate());

        final var totalAmount = prenotations.stream().map(p -> p.getAmount()).reduce((p1, p2) -> p1 + p2).orElse(0);

        return totalAmount <= data.getTotalSeats();
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

    public Prenotation makePrenotation(PrenotationRequestDto dto) throws NotEnoughSeatsException, NotFoundException {

        if (!hasEnoughFreeSeats(dto)) {
            throw new NotEnoughSeatsException();
        }

        log.info("User {} is prenotation {} seats to {}", dto.getRequesterId(), dto.getSeatsAmount(),
                dto.getMerchantId());

        Prenotation prenotation = Prenotation.builder().merchantId(dto.getMerchantId()).amount(dto.getSeatsAmount())
                .dateOfPrenotation(dto.getDate()).build();

        var savedPrenotation = prenotationRepository.save(prenotation);
        log.info("Saved prenotation of id {}", savedPrenotation.getId());

        userPrenotationRepository.save(new UserPrenotation(savedPrenotation.getId(), dto.getRequesterId()));
        log.info("Added prenotation to user {}", dto.getRequesterId());

        return savedPrenotation;
    }

    /**
     * Ritorna le prenotazioni effettuate da un certo utente
     * 
     * @param userId L'id dell'utente che ha effettuato le prenotazioni
     * @return La lista delle prenotazioni effettuate
     */
    public List<UserPrenotation> getPrenotations(int userId) {
        return userPrenotationRepository.findAllByUserId(userId);
    }

    /**
     * Aggiunge l'utente specificato alla lista di prenotazioni di una certa
     * prenotazione
     * 
     * @param prenotationId L'id della prenotazione a cui aggiungere l'utente
     * @param userId        L'id dell'utente da aggiungere
     * 
     * @throws NotFoundException      Se non esiste nessuna prenotazione con l'id
     * @throws NotAuthorizedException Se l'utente non è autorizzato ad aggiungere utenti alla prenotazione
     */
    public void addUserToPrenotation(int requesterId, int prenotationId, int userId)
            throws NotFoundException, NotAuthorizedException {

        final var prenotation = prenotationRepository.findById(prenotationId)
                .orElseThrow(() -> new NotFoundException("Missing prenotation of id " + prenotationId));

        if (prenotation.getOwner() != requesterId)
            throw new NotAuthorizedException("The requester user is not the prenotation's owner");

        userPrenotationRepository.save(new UserPrenotation(prenotationId, userId));
    }
}
