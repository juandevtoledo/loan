package com.lulobank.credits.starter.v3.adapters.out.loan;

import brave.SpanCustomizer;
import com.lulobank.credits.v3.dto.CreditsConditionV3;
import com.lulobank.credits.v3.port.in.loan.dto.DisbursementLoanRequest;
import com.lulobank.credits.v3.port.in.loan.dto.LoanResponse;
import com.lulobank.credits.v3.port.in.loan.dto.LoanV3Error;
import com.lulobank.credits.v3.port.in.loan.dto.SimulatePayment;
import com.lulobank.tracing.FunctionBrave;
import flexibility.client.connector.ProviderException;
import flexibility.client.models.request.DisbursementRequest;
import flexibility.client.models.response.CreateLoanResponse;
import flexibility.client.models.response.DisbursementResponse;
import flexibility.client.models.response.SimulatedLoanResponse;
import flexibility.client.sdk.FlexibilitySdk;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.lulobank.credits.starter.utils.Constants.ID_LOAN;
import static com.lulobank.credits.starter.utils.Constants.PRODUCT_TYPE_KEY;
import static com.lulobank.credits.starter.utils.Samples.createLoanResponseBuilder;
import static com.lulobank.credits.starter.utils.Samples.credistConditionV3Builder;
import static com.lulobank.credits.starter.utils.Samples.loanRequestBuilder;
import static com.lulobank.credits.starter.utils.Samples.repaymentBuilder;
import static com.lulobank.credits.starter.utils.Samples.simulatePaymentRequestBuilder;
import static com.lulobank.credits.v3.port.in.loan.LoanState.ACTIVE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

public class LoanAdapterTest {

    @Mock
    private FlexibilitySdk flexibilitySdk;
    @Mock
    private FunctionBrave functionBrave;
    @Mock
    private SpanCustomizer spanCustomizer;

    private LoanAdapter loanAdapter;

    @Before
    public void setup() {
        CreditsConditionV3 creditsConditionV3 = credistConditionV3Builder();
        MockitoAnnotations.initMocks(this);
        loanAdapter = new LoanAdapter(flexibilitySdk, creditsConditionV3, functionBrave,spanCustomizer);
    }


    @Test
    public void createLoanTest() throws ProviderException {
        CreateLoanResponse createLoanResponse = createLoanResponseBuilder();
        when(flexibilitySdk.createLoanAccount(any())).thenReturn(createLoanResponse);
        when(functionBrave.decorateChecked(any(), any())).thenReturn(e -> createLoanResponse);
        Try<LoanResponse> response = loanAdapter.create(loanRequestBuilder());
        assertTrue("Not error found", response.isSuccess());
        assertThat("IdLoan is right", response.get().getId(), is(ID_LOAN));
        assertThat("ProductTypeKey is right", response.get().getProductTypeKey(), is(PRODUCT_TYPE_KEY));
    }

	@Test
	public void disbursementLoanSuccess() throws ProviderException {
		DisbursementLoanRequest disbursementLoanRequest = buildDisbursementLoanRequest();
		when(flexibilitySdk.disburseLoan(isA(DisbursementRequest.class)))
				.thenReturn(buildDisbursementResponse());
        when(functionBrave.decorateChecked(any(), any())).thenReturn(e -> buildDisbursementResponse());

		Either<LoanV3Error, String> response = loanAdapter.disbursementLoan(disbursementLoanRequest);

		assertTrue("Response is not right", response.isRight());
		assertThat("Status is not APPROVED", response.get(), equalTo(ACTIVE.name()));
	}


	@Test
    public void disbursementLoanFailed() throws ProviderException {
        DisbursementLoanRequest disbursementLoanRequest = buildDisbursementLoanRequest();
        when(functionBrave.decorateChecked(any(), any())).thenReturn(e ->  error());

        when(flexibilitySdk.disburseLoan(isA(DisbursementRequest.class)))
                .thenThrow(new ProviderException("Error", "Code"));

        Either<LoanV3Error, String> response = loanAdapter.disbursementLoan(disbursementLoanRequest);

        assertTrue("Response is not left", response.isLeft());
        assertThat("Status is not APPROVED", response.getLeft().getCode(), equalTo("Code"));
        assertThat("Status is not APPROVED", response.getLeft().getError(), equalTo("Error"));
    }

	@Test
	public void disbursementLoanFailedUnexpectedException() throws ProviderException {
		DisbursementLoanRequest disbursementLoanRequest = buildDisbursementLoanRequest();
        when(functionBrave.decorateChecked(any(), any())).thenThrow(new RuntimeException("Error"));

        when(flexibilitySdk.disburseLoan(isA(DisbursementRequest.class)))
		.thenThrow(new RuntimeException("Error"));

		Either<LoanV3Error, String> response = loanAdapter.disbursementLoan(disbursementLoanRequest);

		assertTrue("Response is not left", response.isLeft());
		assertThat("Status is not APPROVED", response.getLeft().getCode(), equalTo("100"));
		assertThat("Status is not APPROVED", response.getLeft().getError(), equalTo("Error"));
	}

	@Test
    public void setFlexibilitySdkError() throws ProviderException {
        when(flexibilitySdk.createLoanAccount(any())).thenThrow(new ProviderException("01", "ERROR_PROVIDER"));
        when(functionBrave.decorateChecked(any(), any())).thenReturn(e -> new ProviderException("01", "ERROR_PROVIDER"));
        Try<LoanResponse> response = loanAdapter.create(loanRequestBuilder());
        assertTrue("Error found", response.isFailure());
    }

    @Test
    public void simulateLoanTest() throws ProviderException {
        SimulatedLoanResponse simulatedLoanResponse = new SimulatedLoanResponse();
        simulatedLoanResponse.setRepayment(io.vavr.collection.List.of(repaymentBuilder()).asJava());
        when(flexibilitySdk.simulateLoan(any())).thenReturn(simulatedLoanResponse);
        when(functionBrave.decorateChecked(any(), any())).thenReturn(e -> simulatedLoanResponse);
        Either<LoanV3Error, List<SimulatePayment>> response = loanAdapter.simulateLoan(simulatePaymentRequestBuilder());
        assertFalse("Not error found", response.isLeft());
        assertFalse("List is not empty", response.get().isEmpty());
    }

    @Test
    public void sdk_error() throws ProviderException {
        when(flexibilitySdk.simulateLoan(any())).thenThrow(new ProviderException("Error in core Banking", "502"));
        when(functionBrave.decorateChecked(any(), any())).thenReturn(e ->
        {
            throw new ProviderException("Error in core Banking", "502");
        });
        Either<LoanV3Error, List<SimulatePayment>> response = loanAdapter.simulateLoan(simulatePaymentRequestBuilder());
        assertTrue("error found", response.isLeft());
        assertThat("Code is right", response.getLeft().getCode(), is("502"));
    }

    private DisbursementResponse buildDisbursementResponse() {
    	DisbursementResponse disbursementResponse = new DisbursementResponse();
    	disbursementResponse.setStatus(ACTIVE.name());
		return disbursementResponse;
	}

	private DisbursementLoanRequest buildDisbursementLoanRequest() {
    	DisbursementLoanRequest disbursementLoanRequest = new DisbursementLoanRequest();
    	disbursementLoanRequest.setIdClient("idClient");
    	disbursementLoanRequest.setIdClientMambu("idClientMambu");
    	disbursementLoanRequest.setIdCreditMambu("idCreditMambu");
		return disbursementLoanRequest;
	}
	
    private DisbursementResponse error() throws ProviderException {
        throw  new ProviderException("Error", "Code");
    }
}
