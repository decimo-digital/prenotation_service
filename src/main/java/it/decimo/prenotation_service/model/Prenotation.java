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

    @Column(name = "date")
    private Date dateOfPrenotation;

    @Column(name = "amount")
    private int amount;

    @JsonIgnore
    @Transient
    private boolean isValid;
}
