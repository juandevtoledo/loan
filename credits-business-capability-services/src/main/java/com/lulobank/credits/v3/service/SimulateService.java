package com.lulobank.credits.v3.service;

import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.mapper.OfferEntityV3Mapper;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.SimulatePayment;
import com.lulobank.credits.v3.service.dto.OfferInformationRequest;
import io.vavr.control.Option;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import static com.lulobank.credits.v3.port.out.corebanking.mapper.SimulateLoanMapper.simulatePaymentRequestFrom;

@CustomLog
@RequiredArgsConstructor
public class SimulateService {

    private final CoreBankingService loanService;

    public Option<OfferEntityV3> createOffer(OffersTypeV3 offersTypeV3, OfferInformationRequest offerInformationRequest) {
        return loanService.simulateLoan(simulatePaymentRequestFrom(offerInformationRequest, offersTypeV3))
                .peekLeft(loanV3Error -> log.error("Error to try simulate : offer : {} , errorMsg: {} , errorCode: {} by idClient : {} ", offersTypeV3.name(), loanV3Error.getBusinessCode(), loanV3Error.getProviderCode(), offerInformationRequest.getIdClient()))
                .toOption()
                .flatMap(simulatePayments -> getOfferEntity(offersTypeV3, offerInformationRequest, simulatePayments));

    }

    private Option<OfferEntityV3> getOfferEntity(OffersTypeV3 offersTypeV3, OfferInformationRequest offerInformationRequest, List<SimulatePayment> simulatePayments) {
        return Option.ofOptional(simulatePayments.stream().findFirst())
                .filter(simulatePayment -> amountInstallmentLessThanClientCapacity(offerInformationRequest, simulatePayment.getTotalDue(), offersTypeV3.name()))
                .map(simulatePayment -> OfferEntityV3Mapper.INSTANCE.offerEntityV3To(simulatePayment, offerInformationRequest, offersTypeV3));
    }


    private boolean amountInstallmentLessThanClientCapacity(OfferInformationRequest offerInformationRequest, BigDecimal simulatePaymentTotalDue, String offersName) {
        boolean isValidAmount = simulatePaymentTotalDue.compareTo(BigDecimal.valueOf(offerInformationRequest.getClientMonthlyAmountCapacity())) <= 0;
        if (!isValidAmount) {
            log.info("Offer : {} ->  no valid, Amount ( {} )  is less than RiskEngine's MaxAmountInstallment ( {} ), idClient : {} ",
                    offersName, simulatePaymentTotalDue, offerInformationRequest.getClientMonthlyAmountCapacity(), offerInformationRequest.getIdClient());
        }
        return isValidAmount;
    }

}
