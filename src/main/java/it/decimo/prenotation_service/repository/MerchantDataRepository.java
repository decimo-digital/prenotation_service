package it.decimo.prenotation_service.repository;

import it.decimo.prenotation_service.model.MerchantData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantDataRepository extends JpaRepository<MerchantData, Integer> {

}
