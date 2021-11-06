package it.decimo.prenotation_service.model;

import java.util.Date;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.format.datetime.DateFormatter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "prenotation")
public class Prenotation {

    public Prenotation(int merchantId, int tableNumber, Date dateOfPrenotation) {
        this.merchantId = merchantId;
        this.tableNumber = tableNumber;
        setDateOfPrenotation(dateOfPrenotation);
    }

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "merchant_id")
    private int merchantId;

    @Column(name = "table_number")
    private int tableNumber;

    @Column(name = "date_of_prenotation")
    private String dateOfPrenotation;

    @Column(name = "prenotation_slot")
    private int prenotationSlot;

    /**
     * Imposta la data di prenotazione
     * 
     * @param date La data in cui bisogna inserire la prenotazione
     */
    public void setDateOfPrenotation(Date date) {
        var formatter = new DateFormatter("dd-mm-yyyy");
        this.dateOfPrenotation = formatter.print(date, Locale.ITALIAN);
        formatter = new DateFormatter("HH");
        final var hours = Integer.parseInt(formatter.print(date, Locale.ITALIAN));
        this.prenotationSlot = hours - 1;
    }

    /**
     * Recupera la data di prenotazione come oggetto date
     */
    @JsonIgnore
    @SneakyThrows
    public Date getDateOfPrenotation() {
        final var formatter = new DateFormatter("dd-mm-yyyy");
        return formatter.parse(dateOfPrenotation, Locale.ITALIAN);
    }
}
