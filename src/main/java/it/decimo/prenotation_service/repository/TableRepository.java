package it.decimo.prenotation_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import it.decimo.prenotation_service.model.TableStatus;

import it.decimo.prenotation_service.model.MerchantTable;

@Repository
public interface TableRepository extends JpaRepository<MerchantTable, Integer> {
    /**
     * Cerca i tavoli che possono andare bene per ospitare il numero di posti a
     * sedere che si vogliono prenotare
     * 
     * @param merchantId Il locale presso il quale effettuare la prenotazione
     * @param seats      Il numero di posti necessari
     * @return Il primo tavolo prenotabile che rispetta i criteri
     */
    @Query(value = "SELECT * FROM merchant_table WHERE merchant_id = :merch_id AND seats >= :seats AND status = 0 ORDER BY seats ASC LIMIT 1;", nativeQuery = true)
    MerchantTable findAvailableTable(@Param("merch_id") int merchantId, @Param("seats") int seats);

    /**
     * Aggiorna lo stato di un tavolo
     * 
     * @param merchantId  Il locale proprietario del tavolo
     * @param tableNumber Il numero del tavolo da aggiornare
     * @param status      Lo stato (il cui valore è preso da {@link TableStatus})
     */
    @Query(value = "UPDATE merchant_table SET status = :status WHERE merchant_id = :merch_id AND table_number = :table;", nativeQuery = true)
    void updateTableStatus(@Param("merch_id") int merchantId, @Param("table") int tableNumber,
            @Param("status") int status);

    /**
     * Recupera la capacità totale del locale
     * 
     * @param merchantId Il locale di cui ci interessa la capacità totale
     */
    @Query(value = "SELECT sum(seats) FROM merchant_table WHERE merchant_id = :merchant_id", nativeQuery = true)
    int getTotalCapacity(@Param("merchant_id") int merchantId);

    /**
     * Recupera il numero di posti liberi che ha attualmente il locale
     * 
     * @param merchantId Il locale di cui ci interessa il dato
     */
    @Query(value = "SELECT sum(seats) FROM merchant_table WHERE merchant_id = :merchant_id and status = 0", nativeQuery = true)
    int getFreeSeats(@Param("merchant_id") int merchantId);
}
