package com.lulobank.credits.services.features.clientproductoffer;

import com.amazonaws.SdkClientException;
import com.lulobank.core.Response;
import com.lulobank.credits.sdk.dto.clientproduct.offer.Offer;
import com.lulobank.credits.sdk.dto.clientproduct.offer.Offered;
import com.lulobank.credits.services.BaseUnitTestClass;
import com.lulobank.credits.services.features.initialoffer.OffersTypeEnum;
import com.lulobank.credits.services.features.riskmodelscore.model.ClientProductOffer;
import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.services.outboundadapters.model.FlexibleLoan;
import com.lulobank.credits.services.outboundadapters.model.InitialOffer;
import com.lulobank.credits.services.outboundadapters.model.OfferEntity;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.lulobank.credits.services.Constant.AMOUNT_LOAN;
import static com.lulobank.credits.services.Constant.AMOUNT_LOAN_INSTALLMENT;
import static com.lulobank.credits.services.Constant.FEE_INSURANCE;
import static com.lulobank.credits.services.Constant.ID_CLIENT;
import static com.lulobank.credits.services.Constant.ID_CREDIT;
import static com.lulobank.credits.services.Constant.ID_OFFER_COMFORTABLE_LOAN;
import static com.lulobank.credits.services.Constant.ID_OFFER_FAST_LOAN;
import static com.lulobank.credits.services.Constant.ID_OFFER_FLEXIBLE_LOAN;
import static com.lulobank.credits.services.Constant.INTEREST_RATE;
import static com.lulobank.credits.services.Constant.MONTHLY_NOMINAL_RATE;
import static com.lulobank.credits.services.Sample.creditsEntityBuilder;
import static com.lulobank.credits.services.Sample.flexibleLoanBuilder;
import static com.lulobank.credits.services.Sample.initialOffersBuilder;
import static com.lulobank.credits.services.Sample.offerEntityBuilder;
import static com.lulobank.credits.services.features.initialoffer.OffersTypeEnum.COMFORTABLE_LOAN;
import static com.lulobank.credits.services.features.initialoffer.OffersTypeEnum.FAST_LOAN;
import static com.lulobank.credits.services.features.initialoffer.OffersTypeEnum.FLEXIBLE_LOAN;
import static com.lulobank.credits.services.features.initialoffer.RiskModelResponse.OK;
import static com.lulobank.credits.services.utils.CreditsErrorResultEnum.OFFER_NOT_FOUND;
import static com.lulobank.credits.services.utils.HttpCodes.BAD_GATEWAY;
import static com.lulobank.credits.services.utils.HttpCodes.NOT_FOUND;
import static com.lulobank.credits.services.utils.LogMessages.INTERNAL_DYNAMODB_ERROR;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ClientProductOfferHandlerTest extends BaseUnitTestClass {

    private ClientProductOfferHandler testHandler;
    private List<CreditsEntity> creditsEntityList;
    private ClientProductOffer request;
    private static final String ID_CREDIT_2 = UUID.randomUUID().toString();


    @Override
    protected void init() {
        creditsEntityList=new ArrayList<>();
        setCreditsListWithOffers();
        testHandler = new ClientProductOfferHandler(creditsRepository);
        request = new ClientProductOffer(ID_CLIENT);
    }

    @Test
    public void getOffers() {
        when(creditsRepository.findByidClient(any())).thenReturn(creditsEntityList);
        Response<Offered> response=testHandler.handle(request);
        assertTrue("Reponse not contains errors", FALSE.equals(response.getHasErrors()));
        Offered offered=response.getContent();
        assertEquals("Amount is right",AMOUNT_LOAN,offered.getAmount());
        assertEquals("IdCredit is right",ID_CREDIT,offered.getIdCredit());
        assertEquals("IdCredit is right",OK.name(),offered.getRiskModelResponse());
        assertTrue("CurrenDateFormat is right",isTheSameDate(offered.getCurrentDate()));
        Optional<Offer> comfortableOfferLoan =getOffer(offered.getOffers(),ID_OFFER_COMFORTABLE_LOAN);
        assertTrue("Confortable Loan is present",comfortableOfferLoan.isPresent());
        comfortableOfferLoan.ifPresent(offer -> {
            assertEquals("Confortable Loan Amount is right",AMOUNT_LOAN,offer.getAmount());
            assertEquals("Confortable Loan Name is right",COMFORTABLE_LOAN.getDescription(),offer.getName());
            assertEquals("Confortable Loan Number Installment is right",COMFORTABLE_LOAN.getInstallment(),offer.getInstallments());
        });
        Optional<Offer> fastOfferLoan =getOffer(offered.getOffers(),ID_OFFER_FAST_LOAN);
        assertTrue("Fast Loan is present",fastOfferLoan.isPresent());
        fastOfferLoan.ifPresent(offer -> {
            assertEquals("Fast Loan Amount is right",AMOUNT_LOAN,offer.getAmount());
            assertEquals("Fast Loan Name is right",FAST_LOAN.getDescription(),offer.getName());
            assertEquals("Fast Loan Number installmentis right",FAST_LOAN.getInstallment(),offer.getInstallments());
        });
        Optional<Offer> flexibleOfferLoan =getOffer(offered.getOffers(),ID_OFFER_FLEXIBLE_LOAN);
        assertTrue("Flexible Loan is present",flexibleOfferLoan.isPresent());
        flexibleOfferLoan.ifPresent(offer -> {
            assertEquals("Flexible Loan Amount is right",AMOUNT_LOAN,offer.getAmount());
            assertEquals("Flexible Loan Name is right",FLEXIBLE_LOAN.getDescription(),offer.getName());
            assertEquals("Flexible Loan Number installmentis right",FLEXIBLE_LOAN.getInstallment(),offer.getInstallments());
        });
    }

    @Test
    public void getOffersWithNoPreApproved() {
        InitialOffer initialOfferBefore = initialOffersBuilder(OK,AMOUNT_LOAN,LocalDateTime.now().minusMinutes(1),getOfferEntities());
        CreditsEntity creditsEntityPreApproved = creditsEntityBuilder(ID_CREDIT_2,ID_CLIENT,initialOfferBefore);
        creditsEntityPreApproved.setCreditType("PREAPPROVED");

        creditsEntityList.add(creditsEntityPreApproved);
        when(creditsRepository.findByidClient(any())).thenReturn(creditsEntityList);

        Response<Offered> response = testHandler.handle(request);
        assertThat(response.getHasErrors(), is(false));

        Offered offered = response.getContent();
        assertThat(offered.getAmount(), is(AMOUNT_LOAN));
        assertThat(offered.getIdCredit(), is(ID_CREDIT));
        assertThat(offered.getRiskModelResponse(), is(OK.name()));
        assertThat(offered.getCurrentDate(), notNullValue());

        assertOffer(getOffer(offered.getOffers(), ID_OFFER_COMFORTABLE_LOAN), COMFORTABLE_LOAN);
        assertOffer(getOffer(offered.getOffers(), ID_OFFER_FAST_LOAN), FAST_LOAN);
        assertOffer(getOffer(offered.getOffers(), ID_OFFER_FLEXIBLE_LOAN), FLEXIBLE_LOAN);
    }

    private void assertOffer(Optional<Offer> comfortableOfferLoan, OffersTypeEnum comfortableLoan) {
        assertThat(comfortableOfferLoan.isPresent(), is(true));

        Offer offer = comfortableOfferLoan.get();
        assertThat(offer.getAmount(), is(AMOUNT_LOAN));
        assertThat(offer.getName(), is(comfortableLoan.getDescription()));
        assertThat(offer.getInstallments(), is(comfortableLoan.getInstallment()));
    }

    @Test
    public void creditsNotFound() {
        when(creditsRepository.findByidClient(any())).thenReturn(Collections.emptyList());
        Response<Offered> response = testHandler.handle(request);
        assertTrue("Reponse not contains errors", TRUE.equals(response.getHasErrors()));
        assertTrue("Status is 404", response.getErrors().stream().findFirst()
                .map(validationResult -> NOT_FOUND.equals(validationResult.getValue())).orElse(false));
        assertTrue("Failure is CREDIT_NOT_EXIST", response.getErrors().stream().findFirst()
                .map(validationResult -> OFFER_NOT_FOUND.name().equals(validationResult.getFailure())).orElse(false));
    }
    @Test
    public void dynamoException() {
        when(creditsRepository.findByidClient(any())).thenThrow(new SdkClientException("DYNAMO_ERROR"));
        Response<Offered> response = testHandler.handle(request);
        assertTrue("Reponse not contains errors", TRUE.equals(response.getHasErrors()));
        assertTrue("Status is 502", response.getErrors().stream().findFirst()
                .map(validationResult -> BAD_GATEWAY.equals(validationResult.getValue())).orElse(false));
        assertTrue("Failure is INTERNAL_DYNAMODB_ERROR", response.getErrors().stream().findFirst()
                .map(validationResult -> INTERNAL_DYNAMODB_ERROR.name().equals(validationResult.getFailure())).orElse(false));
    }

    private Optional<Offer> getOffer(List<Offer> offers, String idOffer) {
        return offers.stream().filter(offer -> idOffer.equals(offer.getIdOffer()))
                .findFirst();
    }
    private Boolean isTheSameDate(String localDateTimeString) {
        LocalDateTime localDateTime = LocalDateTime.parse(localDateTimeString);
        LocalDate now = LocalDate.now();
        return localDateTime.toLocalDate().equals(now);
    }

    @NotNull
    private List<OfferEntity> getOfferEntities() {
        List<OfferEntity> offerEntities = new ArrayList<>();
        offerEntities.add(buildSimpleOffersToTest(ID_OFFER_COMFORTABLE_LOAN, COMFORTABLE_LOAN, AMOUNT_LOAN, AMOUNT_LOAN_INSTALLMENT));
        offerEntities.add(buildSimpleOffersToTest(ID_OFFER_FAST_LOAN, FAST_LOAN, AMOUNT_LOAN, AMOUNT_LOAN_INSTALLMENT));
        offerEntities.add(buildSimpleOffersToTest(ID_OFFER_FLEXIBLE_LOAN, FLEXIBLE_LOAN, AMOUNT_LOAN, AMOUNT_LOAN_INSTALLMENT, buildFlexibleLoan()));
        return offerEntities;
    }

    private OfferEntity buildSimpleOffersToTest(String idOffer, OffersTypeEnum offersTypeEnum, Double amount, Double amountInstallment) {
        return offerEntityBuilder(idOffer,offersTypeEnum,amount,amountInstallment,FEE_INSURANCE,INTEREST_RATE,null);
    }
    private OfferEntity buildSimpleOffersToTest(String idOffer, OffersTypeEnum offersTypeEnum, Double amount, Double amountInstallment,List<FlexibleLoan> flexibleLoanList) {
        return offerEntityBuilder(idOffer,offersTypeEnum,amount,amountInstallment,FEE_INSURANCE,INTEREST_RATE,flexibleLoanList);
    }
    private void setCreditsListWithOffers() {
        InitialOffer initialOffer=initialOffersBuilder(OK,AMOUNT_LOAN, LocalDateTime.now(),getOfferEntities());
        CreditsEntity creditsEntity=creditsEntityBuilder(ID_CREDIT,ID_CLIENT,initialOffer);
        InitialOffer initialOfferBefore=initialOffersBuilder(OK,AMOUNT_LOAN,LocalDateTime.now().minusMinutes(1),getOfferEntities());
        CreditsEntity creditsEntityBefore=creditsEntityBuilder(ID_CREDIT_2,ID_CLIENT,initialOfferBefore);
        creditsEntityList.add(creditsEntity);
        creditsEntityList.add(creditsEntityBefore);
    }

    private List<FlexibleLoan> buildFlexibleLoan() {
        List<FlexibleLoan> flexibleLoans = new ArrayList<>();
        flexibleLoans.add(flexibleLoanBuilder(COMFORTABLE_LOAN.getInstallment(),AMOUNT_LOAN_INSTALLMENT));
        flexibleLoans.add(flexibleLoanBuilder(COMFORTABLE_LOAN.getInstallment()-1,AMOUNT_LOAN_INSTALLMENT-1));;
        return flexibleLoans;
    }
}
