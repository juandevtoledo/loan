package com.lulobank.credits.v3.usecase.loandetail;

import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.GetMovementsRequest;
import com.lulobank.credits.v3.usecase.loandetail.dto.LoanDetail;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.lulobank.credits.v3.util.CoreBankingFactory.LoanFactory.buildLoanMovements;
import static com.lulobank.credits.v3.util.CoreBankingFactory.LoanFactory.loanActive;
import static com.lulobank.credits.v3.util.CoreBankingFactory.LoanFactory.loanPendingApproval;
import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.creditsEntityWithAcceptOffer;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class LoanDetailUseCaseTest {

    public static final String ID_CLIENT = UUID.randomUUID().toString();
    private LoanDetailUseCase loanDetailUseCase;
    @Mock
    private CreditsV3Repository creditsV3Repository;
    @Mock
    private CoreBankingService coreBankingService;
    @Captor
    private ArgumentCaptor<String> creditIdCaptor;
    @Captor
    private ArgumentCaptor<String> clientIdCaptor;
    @Captor
    private ArgumentCaptor<GetMovementsRequest> getMovementsRequestCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        loanDetailUseCase = new LoanDetailUseCase(creditsV3Repository, coreBankingService);
    }

    @Test
    public void loanDetail_WhenClientHasLoan() {
        when(coreBankingService.getLoanInformation(creditIdCaptor.capture(), clientIdCaptor.capture())).thenReturn(Either.right(loanActive()));
        when(coreBankingService.getLoanMovements(getMovementsRequestCaptor.capture())).thenReturn(Either.right(buildLoanMovements()));
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        Either<UseCaseResponseError, LoanDetail> response = loanDetailUseCase.execute(ID_CLIENT);
        assertThat(response.isRight(), is(true));
        assertMovementsRequest(getMovementsRequestCaptor.getValue());
        assertThatLoanDetailIsValid(response.get());
    }

    @Test
    public void loanDetail_WhenLoanIsPendingState() {
        when(coreBankingService.getLoanInformation(creditIdCaptor.capture(), clientIdCaptor.capture())).thenReturn(Either.right(loanPendingApproval()));
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        when(coreBankingService.getLoanMovements(any())).thenReturn(Either.right(buildLoanMovements()));
        Either<UseCaseResponseError, LoanDetail> response = loanDetailUseCase.execute(ID_CLIENT);
        assertThat(response.isRight(), is(true));
        assertThat(response.get().getState(), is("PENDING"));
    }

    @Test
    public void loanDetail_WhenNotExistCreditInRepository() {
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(Option.none());
        Either<UseCaseResponseError, LoanDetail> response = loanDetailUseCase.execute(ID_CLIENT);
        assertThat(response.isLeft(), is(true));
        assertUseCaseError(response.getLeft(), "D", "404", "CRE_101");
    }

    @Test
    public void loanDetail_WhenCoreBankingError() {
        when(coreBankingService.getLoanInformation(creditIdCaptor.capture(), clientIdCaptor.capture())).thenReturn(Either.left(CoreBankingError.clientWithOutAccountsError()));
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        Either<UseCaseResponseError, LoanDetail> response = loanDetailUseCase.execute(ID_CLIENT);
        assertThat(response.isLeft(), is(true));
        assertUseCaseError(response.getLeft(), "P_CB", "502", "CRE_107");
    }


    @Test
    public void loanDetail_WhenCoreBankingMovementsError() {
        when(coreBankingService.getLoanInformation(creditIdCaptor.capture(), clientIdCaptor.capture())).thenReturn(Either.right(loanActive()));
        when(creditsV3Repository.findLoanActiveByIdClient(anyString())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        when(coreBankingService.getLoanMovements(any())).thenReturn(Either.left(CoreBankingError.defaultError()));
        Either<UseCaseResponseError, LoanDetail> response = loanDetailUseCase.execute(ID_CLIENT);
        assertThat(response.isLeft(), is(true));
        assertUseCaseError(response.getLeft(), "P_CB", "502", "CRE_108");
    }


    private void assertThatLoanDetailIsValid(LoanDetail loanDetail) {
        assertThat(loanDetail.getIdCredit(), is("014176ef-e291-4db6-9b49-18d253a8ae5d"));
        assertThat(loanDetail.getIdLoanCBS(), is("43341876755"));
        assertThat(loanDetail.getCreateOn(), is(LocalDateTime.parse("2020-02-27 10:50", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        assertThat(loanDetail.getInstallments(), is(2));
        assertThat(loanDetail.getPaidAmount().getAmount().getValue(), is(BigDecimal.valueOf(3307627.57).setScale(2, BigDecimal.ROUND_UP)));
        assertThat(loanDetail.getPaidAmount().getCurrency().getValue(), is("COP"));
        assertThat(loanDetail.getRequestedAmount().getAmount().getValue(), is(BigDecimal.valueOf(3500000)));
        assertThat(loanDetail.getRequestedAmount().getCurrency().getValue(), is("COP"));
        assertThat(loanDetail.getRates().getMonthlyNominal().getRoundValue(), is(BigDecimal.valueOf(2.50d).setScale(2, BigDecimal.ROUND_UP)));
        assertThat(loanDetail.getRates().getAnnualEffective().getRoundValue(), is(BigDecimal.valueOf(34.49d).setScale(2, BigDecimal.ROUND_UP)));
        assertThat(loanDetail.getPaymentPlanList().size(), is(2));
        assertThat(loanDetail.getPaymentPlanList().get(0).getState(), is("PAID"));
        assertThat(loanDetail.getPaymentPlanList().get(0).getTotalDue(), is(BigDecimal.valueOf(650).setScale(2, BigDecimal.ROUND_UP)));
    }

    private void assertUseCaseError(UseCaseResponseError useCaseResponseError, String d, String s, String cre_101) {
        assertThat(useCaseResponseError.getDetail(), is(d));
        assertThat(useCaseResponseError.getProviderCode(), is(s));
        assertThat(useCaseResponseError.getBusinessCode(), is(cre_101));
    }

    private void assertMovementsRequest(GetMovementsRequest getMovementsRequest) {
        assertThat(getMovementsRequest.getClientId(), is("1999368732"));
        assertThat(getMovementsRequest.getLoanNumber(), is("YAMW127"));
    }

}
