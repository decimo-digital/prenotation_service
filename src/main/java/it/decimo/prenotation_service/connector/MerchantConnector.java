package it.decimo.prenotation_service.connector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import it.decimo.prenotation_service.dto.MerchantStatusDto;

@Component
public class MerchantConnector {

    @Value("${app.connector.merchantConnector}")
    private String baseUrl;

    private String path = "/api/merchant";

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Invia un {@link MerchantStatusDto} per aggiornare lo stato ridondato di un
     * Merchant
     * 
     * @param dto L'oggetto contenente il nuovo stato di posti a sedere del locale
     */
    public void sendUpdate(MerchantStatusDto dto) {
        restTemplate.patchForObject(baseUrl + path + "/{id}", dto, ResponseEntity.class, dto.getId());
    }

}
