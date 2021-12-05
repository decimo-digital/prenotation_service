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
    private Integer id;

    @Column(name = "owner")
    private Integer owner;

    @Column(name = "merchant")
    private Integer merchantId;

    /**
     * Contiene la data di effettuata prenotazione (comprensiva di tempo)
     */
    @Column(name = "date_millis")
    private Long dateOfPrenotation;

    /**
     * Contiene giorno-mese-anno della prenotazione
     * 
     * Utilizzato solo per scopi di query
     */
    @Column(name = "date")
    @JsonIgnore
    private Date date;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "prenotation_enabled")
    /**
     * Di default vale {@code true} perchè una prenotazione è sempre abilitata,
     * se vale {@code false} significa che è stata cancellata
     */
    private Boolean enabled;

    @Transient
    private boolean isValid;

    public java.util.Date getDateOfPrenotation() {
        if (dateOfPrenotation == null || dateOfPrenotation == 0l) {
            return null;
        }
        return new java.util.Date(dateOfPrenotation);
    }
}
