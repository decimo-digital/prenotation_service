package it.decimo.prenotation_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.decimo.prenotation_service.dto.BasicResponse;
import it.decimo.prenotation_service.dto.PrenotationRequestDto;
import it.decimo.prenotation_service.exception.MissingTableException;
import it.decimo.prenotation_service.exception.NotEnoughSeatsException;
import it.decimo.prenotation_service.model.Prenotation;
import it.decimo.prenotation_service.service.PrenotationService;

@RestController
@RequestMapping(value = "/api/prenotation")
public class PrenotationController {

    @Autowired
    private PrenotationService prenotationService;

    @PostMapping()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'istanza della prenotazione effettuata", content = @Content(schema = @Schema(implementation = Prenotation.class))),
            @ApiResponse(responseCode = "404", description = "Non è stato trovato nessun tavolo disponibile", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
            @ApiResponse(responseCode = "422", description = "Non ci sono abbastanza posti a sedere", content = @Content(schema = @Schema(implementation = BasicResponse.class))) })
    public ResponseEntity<Object> makePrenotation(@RequestBody PrenotationRequestDto prenotationRequest) {
        try {
            final var prenotation = prenotationService.makePrenotation(prenotationRequest);
            return ResponseEntity.ok().body(prenotation);
        } catch (MissingTableException e) {
            return ResponseEntity.status(404)
                    .body(new BasicResponse("Couldn't find any table available", "NO_TABLE_FOUND"));
        } catch (NotEnoughSeatsException e) {
            return ResponseEntity.status(422)
                    .body(new BasicResponse("Merchant doesn't have enough seats", "NOT_ENOUGH_SEATS"));
        }
    }
}
