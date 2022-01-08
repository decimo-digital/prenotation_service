package it.decimo.prenotation_service.repository;

import it.decimo.prenotation_service.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Integer> {

}
