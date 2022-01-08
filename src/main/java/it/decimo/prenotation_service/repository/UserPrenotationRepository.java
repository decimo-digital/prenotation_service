package it.decimo.prenotation_service.repository;

import it.decimo.prenotation_service.model.UserPrenotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPrenotationRepository extends JpaRepository<UserPrenotation, Integer> {

    List<UserPrenotation> findAllByUser(int userId);
}
