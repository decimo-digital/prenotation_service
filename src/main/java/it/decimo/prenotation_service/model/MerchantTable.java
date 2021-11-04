package it.decimo.prenotation_service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "merchant_table")
@NoArgsConstructor
@AllArgsConstructor
public class MerchantTable {
    @Id
    @Column(name = "merchant_id")
    private int merchantId;

    @Id
    @Column(name = "table_number")
    private int tableNumber;

    @Column(name = "seats")
    private int seats;

    @Column(name = "status")
    private int status;
}
