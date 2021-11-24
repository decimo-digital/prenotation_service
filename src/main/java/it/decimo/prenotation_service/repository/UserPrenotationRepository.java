package it.decimo.prenotation_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.decimo.prenotation_service.model.UserPrenotation;

@Repository
public interface UserPrenotationRepository extends JpaRepository<UserPrenotation, Integer> {

    List<UserPrenotation> findAllByUser(int userId);
}
