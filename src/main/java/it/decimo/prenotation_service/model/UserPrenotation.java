package it.decimo.prenotation_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@Entity(name = "prenotation_to_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PrenotationId.class)
public class UserPrenotation {
    @Id
    @Column(name = "prenotation_id")
    private int prenotation;

    @Id
    @Column(name = "user_id")
    private int user;


}

@Data
class PrenotationId implements Serializable {
    @Column(name = "prenotation_id")
    private int prenotation;

    @Column(name = "user_id")
    private int user;
}