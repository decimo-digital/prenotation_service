package it.decimo.prenotation_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "prenotation")
public class Prenotation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prenotation_seq")
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "owner", nullable = false)
    private Integer owner;

    @Column(name = "merchant", nullable = false)
    private Integer merchantId;

    /**
     * Contiene la data di effettuata prenotazione (comprensiva di tempo)
     */
    @Column(name = "date_millis", nullable = false)
    private Long dateOfPrenotation;

    /**
     * Contiene giorno-mese-anno della prenotazione
     * <p>
     * Utilizzato solo per scopi di query
     */
    @Column(name = "date", nullable = false)
    @JsonIgnore
    private Date date;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    /**
     * Di default vale {@code true} perchè una prenotazione è sempre abilitata,
     * se vale {@code false} significa che è stata cancellata
     */
    @Column(name = "prenotation_enabled", nullable = false)
    private Boolean enabled;

    /**
     * Vale {@literal true} se la prenotazione è ancora all'interno del range per il quale si
     * considerano attive le prenotazioni (di base mezz'ora)
     */
    @Transient
    private boolean isValid;

    public java.util.Date getDateOfPrenotation() {
        if (dateOfPrenotation == null || dateOfPrenotation == 0L) {
            return null;
        }
        return new java.util.Date(dateOfPrenotation);
    }
}
