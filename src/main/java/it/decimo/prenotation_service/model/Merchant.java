package it.decimo.prenotation_service.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "merchant")
public class Merchant {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "store_name")
    @JsonAlias(value = "store_name")
    private String storeName;
    @JsonAlias(value = "owner")
    @Column(name = "owner")
    private Integer owner;
    @Transient
    @JsonAlias(value = "free_seats")
    private Integer freeSeats;
    @Column(name = "description")
    private String description;
    @Transient
    @JsonAlias(value = "occupancy_rate")
    private float occupancyRate;
    @Column(name = "total_seats")
    @JsonAlias(value = "total_seats")
    private Integer totalSeats;


}
