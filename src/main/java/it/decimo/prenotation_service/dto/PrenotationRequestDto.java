package it.decimo.prenotation_service.dto;

import java.sql.Date;

import it.decimo.prenotation_service.model.Prenotation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrenotationRequestDto {
    /**
     * Il locale presso il quale bisogna prenotare
     */
    private int merchantId;

    /**
     * Il numero di posti da allocare
     */
    private int seatsAmount;

    /**
     * La data di prenotazione
     */
    private Date date;

    /**
     * L'id dell'utente che ha mandato la richiesta di prenotazione
     */
    private int requesterId;

    /**
     * Genera una {@link Prenotation} a partire dal DTO
     * 
     */
    public Prenotation toPrenotation() {
        return Prenotation.builder().owner(requesterId).merchantId(merchantId).amount(seatsAmount)
                .dateOfPrenotation(date).build();
    }
}
