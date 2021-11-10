package it.decimo.prenotation_service.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "merchant_table")
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MerchantTableId.class)
public class MerchantTable {
    @Id
    @Column(name = "merchant_id")
    private int merchantId;

    @Id
    @Column(name = "number")
    private int tableNumber;

    @Column(name = "seats")
    private int seats;

    @Column(name = "status")
    private int status;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class MerchantTableId implements Serializable {
    @Id
    @Column(name = "merchant_id")
    private int merchantId;

    @Id
    @Column(name = "number")
    private int tableNumber;
}