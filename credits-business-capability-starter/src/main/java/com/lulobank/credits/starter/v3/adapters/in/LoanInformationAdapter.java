package com.lulobank.credits.starter.v3.adapters.in;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.loan.LoanDetailResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.movement.Movement;
import com.lulobank.credits.starter.v3.adapters.in.dto.nextinstallment.NextInstallmentResponse;
import com.lulobank.credits.starter.v3.handler.LoanDetailHandler;
import com.lulobank.credits.starter.v3.handler.LoanMovementsHandler;
import com.lulobank.credits.starter.v3.handler.LoanNextInstallmentHandler;
import com.lulobank.credits.starter.v3.util.Messages;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@CustomLog
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/loan/client/{idClient}")
@RequiredArgsConstructor
public class LoanInformationAdapter {

    private final LoanDetailHandler loanDetailHandler;
    private final LoanNextInstallmentHandler loanNextInstallmentHandler;
    private final LoanMovementsHandler loanMovementsHandler;

    @GetMapping(value = "/detail", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get loan detail", notes = "Get loan detail for the active loan of the given " +
            "client.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get loan detail successfully", response = LoanDetailResponse.class),
            @ApiResponse(code = 404, message = "Credit not found")})
    public ResponseEntity<AdapterResponse> loanDetail(@RequestHeader final HttpHeaders headers,
                                                      @PathVariable("idClient") @NotBlank(message = Messages.ERROR_CLIENT_ID_MESSAGE) String idClient
    ) {
        return loanDetailHandler.get(idClient);
    }

    @GetMapping(value = "/next-installment", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get next installment", notes = "Get next installment for the active loan of the given " +
            "client.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get next installment successfully", response = NextInstallmentResponse.class),
            @ApiResponse(code = 404, message = "Credit not found")})
    public ResponseEntity<AdapterResponse> nextInstallments(@RequestHeader final HttpHeaders headers,
                                                            @PathVariable("idClient") @NotBlank(message = Messages.ERROR_CLIENT_ID_MESSAGE) String idClient
    ) {
        return loanNextInstallmentHandler.get(idClient);
    }

    @GetMapping(value = "/movements", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get loan movements", notes = "Get movements for the active loan of the given " +
            "client. By default get last five movements, this behavior can be modified using offset and limit " +
            "parameters.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get loan movements successfully", response = Movement.class,
                    responseContainer = "List"),
            @ApiResponse(code = 404, message = "Credit not found")})
    public ResponseEntity<AdapterResponse> getLoanMovements(@RequestHeader final HttpHeaders headers,
                                                            @PathVariable("idClient") @NotBlank(message = Messages.ERROR_CLIENT_ID_MESSAGE) String idClient,
                                                            @RequestParam(value ="offset", defaultValue = "0") Integer offset,
                                                            @RequestParam(value ="limit", defaultValue = "5") Integer limit) {
        return loanMovementsHandler.getLoanMovements(idClient, offset, limit);
    }
}
