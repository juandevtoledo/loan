package com.lulobank.credits.services.features.getloandetail;

import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.credits.sdk.dto.loandetails.LoanDetail;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import flexibility.client.connector.ProviderException;
import flexibility.client.sdk.FlexibilitySdk;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static com.lulobank.credits.services.outboundadapters.flexibility.FlexibilityMapper.getCreditProductsFromGetLoanResponse;
import static com.lulobank.credits.services.outboundadapters.flexibility.FlexibilityMapper.getLoanRequestFromCreditV3EntityInfo;
import static com.lulobank.credits.services.utils.LogMessages.ERROR_CORE_BANKING_SOLUTION;

public class GetLoanDetailHandler implements Handler<Response<List<LoanDetail>>, GetLoanDetail> {

    private final FlexibilitySdk flexibilitySdk;
    private final CreditsV3Repository creditsV3Repository;
    private static final Double ZERO_BALANCE = 0d;
    private static final Logger logger = LoggerFactory.getLogger(GetLoanDetailHandler.class);

    public GetLoanDetailHandler(FlexibilitySdk flexibilitySdk, CreditsV3Repository creditsV3Repository) {
        this.flexibilitySdk = flexibilitySdk;
        this.creditsV3Repository = creditsV3Repository;
    }

    @Override
    public Response<List<LoanDetail>> handle(GetLoanDetail getLoanDetail) {
        List<CreditsV3Entity> credits = creditsV3Repository
                .findByidClientAndIdLoanAccountMambuNotNull(getLoanDetail.getIdClient()).toJavaList();

        List<LoanDetail> result = credits.stream().filter(credit -> Try.of(() -> {
            LoanDetail loanDetailFromFlexibility = getLoanDetailFromFlexibility(credit);
            return isLoanOpen(loanDetailFromFlexibility);
        }).onFailure(ProviderException.class, e -> logger.error(ERROR_CORE_BANKING_SOLUTION.getMessage(), e, e))
                .get())
                .map(this::getLoanDetailFromFlexibility)
                .collect(Collectors.toList());
        return new Response<>(result);

    }

    private LoanDetail getLoanDetailFromFlexibility(CreditsV3Entity creditsV3Entity) {
        return Try.of(() -> getCreditProductsFromGetLoanResponse(flexibilitySdk.getLoanByLoanAccountId(getLoanRequestFromCreditV3EntityInfo(creditsV3Entity)), creditsV3Entity))
                .onFailure(ProviderException.class, e -> logger.error(ERROR_CORE_BANKING_SOLUTION.getMessage(), e, e))
                .getOrNull();
    }

    private boolean isLoanOpen(LoanDetail loanDetail) {
        return loanDetail != null && loanDetail.getBalance() > ZERO_BALANCE;
    }
}

