package it.decimo.prenotation_service.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.decimo.prenotation_service.exception.MissingTableException;
import it.decimo.prenotation_service.exception.NotEnoughSeatsException;
import it.decimo.prenotation_service.model.Prenotation;
import it.decimo.prenotation_service.model.TableStatus;
import it.decimo.prenotation_service.repository.PrenotationRepository;
import it.decimo.prenotation_service.repository.TableRepository;
import it.decimo.prenotation_service.repository.UserPrenotationRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PrenotationService {
    @Autowired
    private TableRepository tableRepository;
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
        int capacity = tableRepository.getFreeSeats(merchantId);
        return capacity >= toPrenotate;
    }

    /**
     * Effettua una prenotazione presso un certo locale
     * 
     * @param merchantId  Il locale verso il quale bisogna prenotare
     * @param toPrenotate Quanti posti sono da allocare alla prenotazione
     * @param userId      L'id dell'utente che sta richiedendo la prenotazione
     * 
     * @throws MissingTableException   Se non vi è nessun tavolo che permette di
     *                                 effettuare la prenotazione
     * @throws NotEnoughSeatsException Se il locale scelto non ha abbastanza posti
     *                                 liberi per ospitare la prenotazione
     */
    public void makePrenotation(int merchantId, int toPrenotate, int userId)
            throws MissingTableException, NotEnoughSeatsException {

        if (!hasEnoughFreeSeats(merchantId, toPrenotate)) {
            throw new NotEnoughSeatsException();
        }

        log.info("User {} is prenotation {} seats to {}", userId, toPrenotate, merchantId);
        var table = tableRepository.findAvailableTable(merchantId, toPrenotate);

        if (table == null) {
            throw new MissingTableException();
        }

        var prenotation = Prenotation.builder().merchantId(merchantId).tableNumber(table.getTableNumber())
                .dateOfPrenotation(new Date().toInstant().toEpochMilli()).build();

        var savedPrenotation = prenotationRepository.save(prenotation);
        log.info("Saved prenotation of id {}", savedPrenotation.getId());

        userPrenotationRepository.addPrenotation(userId, savedPrenotation.getId());
        log.info("Added prenotation to user {}", userId);

        tableRepository.updateTableStatus(merchantId, table.getTableNumber(), TableStatus.prenotated.getValue());
        log.info("Updated status of table {}-{}", merchantId, table.getTableNumber());

        // TODO aggiornare il dato ridondato nel merchant_service

        // TODO ritornare l'ok sulla prenotazione
    }

}
