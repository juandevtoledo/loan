package com.lulobank.credits.starter.v3.adapters.in;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ExtraAmountResponse;
import com.lulobank.credits.starter.v3.handler.ExtraAmountHandler;
import com.lulobank.credits.starter.v3.util.Messages;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.CustomLog;
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

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@RestController
@CrossOrigin(origins = "*")
@CustomLog
@RequestMapping("/api/v1/client/{idClient}/")
public class ExtraAmountAdapter {

    private final ExtraAmountHandler extraAmountHandler;

    public ExtraAmountAdapter (ExtraAmountHandler extraAmountHandler){
        this.extraAmountHandler = extraAmountHandler;
    }


    @GetMapping(value = "loan/{idCredit}/extra-amount/installment", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Extra amount installment", notes = "Profile an extra amount installment. Calculate which " +
            "part corresponds to the minimum payment and which one the extra amount payment.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Profile extra amount installment successfully",
                    response = ExtraAmountResponse.class),
            @ApiResponse(code = 404, message = "Error getting info from database"),
            @ApiResponse(code = 500, message = "Error processing extra payment"),
            @ApiResponse(code = 502, message = "Error getting info from coreBanking")})
    public ResponseEntity<AdapterResponse> getPaymentPlanV4(@RequestHeader final HttpHeaders headers,
                                                            @PathVariable("idCredit") @NotBlank(message = Messages.ERROR_CLIENT_ID_MESSAGE) String idCredit,
                                                            @Valid @RequestParam("amount") BigDecimal amount) {

        return extraAmountHandler.executeUseCase(idCredit,amount);
    }
}
