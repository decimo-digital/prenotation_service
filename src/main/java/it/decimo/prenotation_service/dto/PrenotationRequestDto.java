package it.decimo.prenotation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
     * Ritorna la data di creazione della prenotazione in millisecondi dall'epoch
     */
    public long getDate() {
        return date.toInstant().toEpochMilli();
    }
}
