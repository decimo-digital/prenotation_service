package it.decimo.prenotation_service.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

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

    @Column(name = "owner")
    private int owner;

    @Column(name = "merchant")
    private int merchantId;

    /**
     * Contiene la data di effettuata prenotazione (comprensiva di tempo)
     */
    @Column(name = "date_millis")
    private long dateOfPrenotation;

    /**
     * Contiene giorno-mese-anno della prenotazione
     * 
     * Utilizzato solo per scopi di query
     */
    @Column(name = "date")
    @JsonIgnore
    private Date date;

    @Column(name = "amount")
    private int amount;

    @Column(name = "prenotation_enabled")
    private boolean enabled;

    @Transient
    private boolean isValid;

    public java.util.Date getDateOfPrenotation() {
        return new java.util.Date(dateOfPrenotation);
    }
}
