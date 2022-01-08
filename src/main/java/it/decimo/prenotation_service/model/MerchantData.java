package it.decimo.prenotation_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "merchant_data")
@NoArgsConstructor
@AllArgsConstructor
public class MerchantData {
    @Id
    @Column(name = "merchant_id")
    private int merchantId;

    @Column(name = "total_seats")
    private int totalSeats;
}
