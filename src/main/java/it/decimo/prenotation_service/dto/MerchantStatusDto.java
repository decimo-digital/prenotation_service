package it.decimo.prenotation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchantStatusDto {
    private int id;
    private int totalSeats;
    private int freeSeats;
}
