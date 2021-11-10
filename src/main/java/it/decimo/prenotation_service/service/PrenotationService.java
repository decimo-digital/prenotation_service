package it.decimo.prenotation_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.decimo.prenotation_service.dto.PrenotationRequestDto;
import it.decimo.prenotation_service.exception.MissingTableException;
import it.decimo.prenotation_service.exception.NotEnoughSeatsException;
import it.decimo.prenotation_service.model.Prenotation;
import it.decimo.prenotation_service.model.UserPrenotation;
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

    /**
     * Effettua un controllo sul numero di posti liberi di un dato locale
     * 
     * @param merchantId  Il locale di cui ci interessa il controllo
     * @param toPrenotate Il numero di posti da prenotare
     * @return {@literal true} se il locale ha abbastanza posti per accettare la
     *         prenotazione, {@literal false} altrimenti
     */
    private boolean hasEnoughFreeSeats(int merchantId, int toPrenotate) {
        // int capacity = tableRepository.getFreeSeats(merchantId);
        // TODO bisogna ritornare la capacità attuale del merchant
        int capacity = 1000;
        return capacity >= toPrenotate;
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
     */
    public Prenotation makePrenotation(PrenotationRequestDto dto)
            throws MissingTableException, NotEnoughSeatsException {

        if (!hasEnoughFreeSeats(dto.getMerchantId(), dto.getSeatsAmount())) {
            throw new NotEnoughSeatsException();
        }

        log.info("User {} is prenotation {} seats to {}", dto.getRequesterId(), dto.getSeatsAmount(),
                dto.getMerchantId());

        Prenotation prenotation = Prenotation.builder().merchantId(dto.getMerchantId()).amount(dto.getSeatsAmount())
                .dateOfPrenotation(dto.getDate()).build();

        var savedPrenotation = prenotationRepository.save(prenotation);
        log.info("Saved prenotation of id {}", savedPrenotation.getId());

        userPrenotationRepository.addPrenotation(dto.getRequesterId(), savedPrenotation.getId());
        log.info("Added prenotation to user {}", dto.getRequesterId());

        updateMerchantStatus(dto.getMerchantId());

        log.info("Updated merchant status");

        return savedPrenotation;
    }

    /**
     * Aggiorna lo stato dei posti occupati del {@link Merchant} dato il suo id
     * 
     * @param merchantId L'id del merchant che bisogna aggiornare
     */
    public void updateMerchantStatus(int merchantId) {
        // TODO Bisogna aggiornare lo status del merchant
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
}
