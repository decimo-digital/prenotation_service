package it.decimo.prenotation_service.model;

import java.time.Instant;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "prenotation")
public class Prenotation {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "merchant_id")
    private int merchantId;

    @Column(name = "table_number")
    private int tableNumber;

    @Column(name = "date_of_prenotation")
    private long dateOfPrenotation;

    /**
     * Recupera la data di prenotazione come oggetto date
     */
    @JsonIgnore
    public Date getDateOfPrenotation() {
        return Date.from(Instant.ofEpochMilli(dateOfPrenotation));
    }
}
