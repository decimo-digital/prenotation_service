package it.decimo.prenotation_service.controller;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.decimo.prenotation_service.dto.BasicResponse;
import it.decimo.prenotation_service.dto.PrenotationRequestDto;
import it.decimo.prenotation_service.exception.NotAuthorizedException;
import it.decimo.prenotation_service.exception.NotEnoughSeatsException;
import it.decimo.prenotation_service.exception.NotFoundException;
import it.decimo.prenotation_service.model.Prenotation;
import it.decimo.prenotation_service.model.UserPrenotation;
import it.decimo.prenotation_service.service.PrenotationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping(value = "/api/prenotation")
@Slf4j
public class PrenotationController {

    @Autowired
    private PrenotationService prenotationService;

    @PostMapping()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'istanza della prenotazione effettuata", content = @Content(schema = @Schema(implementation = Prenotation.class))),
            @ApiResponse(responseCode = "404", description = "Non è stato trovato il locale richiesto", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
            @ApiResponse(responseCode = "422", description = "Non ci sono abbastanza posti a sedere", content = @Content(schema = @Schema(implementation = BasicResponse.class)))})
    public ResponseEntity<Object> makePrenotation(@RequestBody PrenotationRequestDto prenotationRequest) {
        try {
            final var prenotation = prenotationService.makePrenotation(prenotationRequest);
            return ResponseEntity.ok().body(prenotation);
        } catch (NotEnoughSeatsException e) {
            return ResponseEntity.status(422)
                    .body(new BasicResponse("Merchant doesn't have enough seats", "NOT_ENOUGH_SEATS"));
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(new BasicResponse("Table not found", "TABLE_NOT_FOUND"));
        }
    }

    @GetMapping("/{userId}")
    @ApiResponse(responseCode = "200", description = "Lista delle prenotazioni effettuate", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserPrenotation.class), minItems = 0, uniqueItems = true)))
    public ResponseEntity<Object> getUserPrenotations(@PathVariable(name = "userId") int requesterId) {
        final var prenotations = prenotationService.getPrenotationsForUser(requesterId);
        return ResponseEntity.ok().body(prenotations);
    }

    @PostMapping("/update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La prenotazione è stata modificata con successo", content = @Content(schema = @Schema(implementation = Prenotation.class))),
            @ApiResponse(responseCode = "404", description = "La prenotazione non esiste", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
            @ApiResponse(responseCode = "401", description = "L'utente non può modificare la prenotazione", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
    })
    public ResponseEntity<Object> editPrenotation(@PathParam("userId") int userId,
                                                  @RequestBody Prenotation prenotation) {
        try {
            final var newPrenotation = prenotationService.patchPrenotation(prenotation, userId);
            return ResponseEntity.ok().body(newPrenotation);
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(new BasicResponse("Prenotation not found", "PRENOTATION_NOT_FOUND"));
        } catch (NotAuthorizedException e) {
            return ResponseEntity.status(401).body(new BasicResponse(e.getMessage(), "NOT_AUTHORIZED"));
        }
    }

    @DeleteMapping("/{prenotationId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La prenotazione è stata cancellata con successo", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
            @ApiResponse(responseCode = "404", description = "La prenotazione non esiste", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
            @ApiResponse(responseCode = "401", description = "L'utente non può cancellare la prenotazione", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
    })
    public ResponseEntity<Object> deletePrenotation(@PathVariable(name = "prenotationId") int prenotationId, @PathParam("userId") int userId) {
        try {
            prenotationService.deletePrenotation(prenotationId, userId);
            return ResponseEntity.ok().body(new BasicResponse("Prenotation deleted", "PRENOTATION_DELETED"));
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(new BasicResponse("Prenotation not found", "PRENOTATION_NOT_FOUND"));
        } catch (NotAuthorizedException e) {
            return ResponseEntity.status(401).body(new BasicResponse(e.getMessage(), "NOT_AUTHORIZED"));
        }

    }

    @GetMapping("/{merchantId}/prenotations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista delle prenotazioni effettuate", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Prenotation.class), minItems = 0, uniqueItems = true))),
            @ApiResponse(responseCode = "404", description = "Non è stato trovato il locale richiesto", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
            @ApiResponse(responseCode = "401", description = "L'utente richiedente non ha i permessi necessari per la risorsa", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
    })
    public ResponseEntity<Object> getPrenotations(@PathVariable(name = "merchantId") int merchantId,
                                                  @PathParam("userId") int userId) {
        try {
            final var prenotations = prenotationService.getPrenotationsForMerchant(merchantId, userId);
            return ResponseEntity.ok().body(prenotations);
        } catch (NotAuthorizedException e) {
            return ResponseEntity.status(401).body(new BasicResponse(e.getMessage(), "NOT_AUTHORIZED"));
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(new BasicResponse(e.getMessage(), "PRENOTATION_NOT_FOUND"));
        }

    }

    @PostMapping("/{prenotationId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'utente specificato è stato aggiunto alla prenotazione", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
            @ApiResponse(responseCode = "401", description = "L'utente richiedente non è l'owner della prenotazione e non può aggiungere gente", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
            @ApiResponse(responseCode = "404", description = "Non è stata trovata nessuna prenotazione esistente", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
            @ApiResponse(responseCode = "422", description = "L'utente era già stato registrato nella prenotazione", content = @Content(schema = @Schema(implementation = BasicResponse.class)))})
    public ResponseEntity<Object> addUserToPrenotation(@PathVariable(value = "prenotationId") int prenotationId,
                                                       @PathParam(value = "userId") int userId, @PathParam("requesterId") int requesterId) {
        try {
            prenotationService.addUserToPrenotation(requesterId, prenotationId, userId);
            return ResponseEntity.ok().body(new BasicResponse("User inserted successfully", "OK"));
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(new BasicResponse(e.getMessage(), "NOT_FOUND"));
        } catch (NotAuthorizedException e) {
            return ResponseEntity.status(401).body(new BasicResponse(e.getMessage(), "NOT_AUTHORIZED"));
        } catch (Exception e) {
            log.error("Got error while adding new user to prenotation ({} -> {}): {}", userId, prenotationId,
                    e.getMessage());
            return ResponseEntity.status(422).body(new BasicResponse(e.getMessage(), "ALREADY_EXISTS"));
        }
    }
}
