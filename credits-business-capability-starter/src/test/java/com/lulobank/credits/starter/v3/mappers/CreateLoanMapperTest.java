package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.starter.utils.Samples;
import com.lulobank.credits.v3.port.in.loan.dto.LoanRequest;
import com.lulobank.credits.v3.port.in.loan.dto.LoanResponse;
import flexibility.client.models.request.CreateLoanRequest;
import flexibility.client.models.response.CreateLoanResponse;
import org.junit.Test;

import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.utils.Constants.ID_LOAN;
import static com.lulobank.credits.starter.utils.Constants.PRODUCT_TYPE_KEY;
import static com.lulobank.credits.starter.utils.Samples.loanRequestBuilder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CreateLoanMapperTest {

    @Test
    public void toCreateLoanRequestMapper() {
        LoanRequest loanRequest = loanRequestBuilder();
        CreateLoanRequest createLoanRequest = CreateLoanMapper.INSTANCE.toCreateLoanRequest(loanRequest);
        assertThat("IdClient is right", createLoanRequest.getClientId(), is(ID_CLIENT));
        assertThat("ProductTypeKey is right", createLoanRequest.getProductTypeKey(), is(PRODUCT_TYPE_KEY));
    }

    @Test
    public void toLoanResponsetMapper() {
        CreateLoanResponse createLoanResponse=new CreateLoanResponse();
        createLoanResponse.setId(ID_LOAN);
        createLoanResponse.setProductTypeKey(PRODUCT_TYPE_KEY);
        LoanResponse loanResponse = CreateLoanMapper.INSTANCE.toLoanResponse(createLoanResponse);
        assertThat("IdLoan is right", loanResponse.getId(), is(ID_LOAN));
        assertThat("ProductTypeKey is right", loanResponse.getProductTypeKey(), is(PRODUCT_TYPE_KEY));
    }

}
