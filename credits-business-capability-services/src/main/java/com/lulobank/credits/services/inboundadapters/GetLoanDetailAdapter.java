package com.lulobank.credits.services.inboundadapters;

import com.lulobank.core.Response;
import com.lulobank.core.crosscuttingconcerns.ValidatorDecoratorHandler;
import com.lulobank.credits.sdk.dto.CreditErrorResult;
import com.lulobank.credits.sdk.dto.CreditSuccessResult;
import com.lulobank.credits.sdk.dto.clientloandetail.GetClientLoan;
import com.lulobank.credits.sdk.dto.clientloandetail.Loan;
import com.lulobank.credits.sdk.dto.loandetails.LoanDetail;
import com.lulobank.credits.services.features.getloandetail.GetLoanDetail;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.lulobank.core.utils.ValidatorUtils.getHttpStatusByCode;

@RefreshScope
@RestController
@CrossOrigin(origins = "*")
@RequestMapping()
public class GetLoanDetailAdapter {
    @Autowired
    @Qualifier("creditProductsDecoratorHandler")
    private ValidatorDecoratorHandler creditProductsDecoratorHandler;
    @Autowired
    @Qualifier("clientLoanDecoratorHandler")
    private ValidatorDecoratorHandler clientLoanDecoratorHandler;

    @GetMapping(value = "loan/client/{idClient}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get client loan", notes = "Get client loan for the given client. Includes loan detail, " +
            "next installment and payment list.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get client loan successfully",
                    response = CreditSuccessResult.class),
            @ApiResponse(code = 406, message = "Unexpected errors")})
    public ResponseEntity getClientLoan(@RequestHeader final HttpHeaders headers, @PathVariable("idClient") final String idClient) {

        Response<List<Loan>> response = clientLoanDecoratorHandler.handle(new GetClientLoan(idClient));

        if (Boolean.TRUE.equals(response.getHasErrors())) {
            return new ResponseEntity<>(new CreditErrorResult(response.getErrors()), getHttpStatusByCode(response.getErrors().get(0).getValue()));
        }
        return new ResponseEntity(new CreditSuccessResult<>(response.getContent()),HttpStatus.OK);
    }

    @GetMapping(value = "{idClient}/products", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get loan detail", notes = "Get loan detail for the given client. Includes basic loan " +
            "info and next installment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get loan detail successfully",
                    response = Response.class),
            @ApiResponse(code = 406, message = "Unexpected errors")})
    public ResponseEntity getCreditsProducts(@RequestHeader final HttpHeaders headers, @PathVariable("idClient") final String idClient) {

        Response<List<LoanDetail>> response = creditProductsDecoratorHandler.handle(new GetLoanDetail(idClient));

        if (Boolean.TRUE.equals(response.getHasErrors())) {
            return new ResponseEntity<>(new CreditErrorResult(response.getErrors()), getHttpStatusByCode(response.getErrors().get(0).getValue()));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
