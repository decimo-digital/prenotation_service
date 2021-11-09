package it.decimo.prenotation_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.decimo.prenotation_service.model.UserPrenotation;

@Repository
public interface UserPrenotationRepository extends JpaRepository<UserPrenotation, Integer> {

    /**
     * Collega una prenotazione ad un determinato utente
     * 
     * @param userId        L'utente a cui dev'essere collegata la prenotazione
     * @param prenotationId La prenotazione da collegare
     */
    @Query(value = "INSERT INTO user_prenotations (user_id, prenotation_id) VALUES (:user_id, :prenotation_id)", nativeQuery = true)
    void addPrenotation(@Param("user_id") int userId, @Param("prenotation_id") int prenotationId);

    List<UserPrenotation> findAllByUserId(int userId);
}
