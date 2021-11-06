package it.decimo.prenotation_service.connector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class MerchantBaseConnector {

    @Value("${app.connector.merchantConnector}")
    private String baseUrl;

}