package it.decimo.prenotation_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.decimo.prenotation_service.model.Prenotation;

@Repository
public interface PrenotationRepository extends JpaRepository<Prenotation, Integer> {

    /**
     * Ritorna tutte le prenotazioni effettuate per una deterimanta data, da
     * controllare se siano ancora in validità o no
     */
    @Query(value = "SELECT * FROM prenotation WHERE merchant=:merchantId AND date_part('year', date) = :year AND date_part('month',date) = :month AND date_part('day', date) = :day", nativeQuery = true)
    List<Prenotation> findByPrenotationDateAndMerchantId(@Param("year") int year, @Param("month") int month,
            @Param("day") int day, @Param(value = "merchantId") int merchantId);

    List<Prenotation> findAllByMerchantId(int merchantId);
}
