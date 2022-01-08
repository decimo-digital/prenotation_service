package it.decimo.prenotation_service.repository;

import it.decimo.prenotation_service.model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AuthUser, Integer> {

}
