package it.decimo.prenotation_service.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "user_prenotations")
@IdClass(UserPrenotationId.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPrenotation {

    @Id
    @Column(name = "user_id")
    private int userId;

    @Id
    @Column(name = "prenotation_id")
    private int prenotationId;

    @Column(name = "active")
    private boolean active;

    @Column(name = "date_of_deletion")
    private Integer dateOfDeletion;

    @JsonIgnore
    public Date getDateOfDeletion() {
        if (this.dateOfDeletion == null) {
            return null;
        }
        return Date.from(Instant.ofEpochMilli(dateOfDeletion));
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class UserPrenotationId implements Serializable {
    @Id
    @Column(name = "user_id")
    private int userId;

    @Id
    @Column(name = "prenotation_id")
    private int prenotationId;
}