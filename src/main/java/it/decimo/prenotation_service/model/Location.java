package it.decimo.prenotation_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private Double x;
    private Double y;

    @JsonIgnore
    public Point toPoint() {
        return new Point(x, y);
    }
}