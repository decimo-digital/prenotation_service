package it.decimo.prenotation_service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "prenotation_to_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrenotation {
    @Id
    @Column(name = "prenotation_id")
    private int prenotation;

    @Id
    @Column(name = "user")
    private int userId;
}
