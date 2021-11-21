package it.decimo.prenotation_service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "merchant_data")
@NoArgsConstructor
@AllArgsConstructor
public class MerchantData {
    @Id
    @Column(name = "merchant_id")
    private int merchantId;

    @Column(name = "free_seats")
    private int freeSeats;

    @Column(name = "total_seats")
    private int totalSeats;
}
