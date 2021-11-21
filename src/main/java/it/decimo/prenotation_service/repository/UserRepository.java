package it.decimo.prenotation_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.decimo.prenotation_service.model.AuthUser;

@Repository
public interface UserRepository extends JpaRepository<AuthUser, Integer> {
    
}
