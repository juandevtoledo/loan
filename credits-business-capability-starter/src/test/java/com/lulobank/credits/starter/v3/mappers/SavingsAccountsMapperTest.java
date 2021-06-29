package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.v3.dto.ClientInformationV3;
import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountRequest;
import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountResponse;
import com.lulobank.savingsaccounts.sdk.dto.createsavingaccountv3.CreateSavingsAccountRequest;
import com.lulobank.savingsaccounts.sdk.dto.createsavingaccountv3.SavingAccountCreated;

import org.junit.Test;

import static com.lulobank.credits.starter.utils.Constants.ACCOUNT_ID;
import static com.lulobank.credits.starter.utils.Constants.EMAIL;
import static com.lulobank.credits.starter.utils.Constants.ID_CARD;
import static com.lulobank.credits.starter.utils.Constants.ID_LOAN;
import static com.lulobank.credits.starter.utils.Constants.NAME;
import static com.lulobank.credits.starter.utils.Samples.clientInformationV3Builder;
import static com.lulobank.credits.starter.utils.Samples.createSavingAccountCreated;
import static com.lulobank.credits.starter.utils.Samples.savingsAccountRequestBuilder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SavingsAccountsMapperTest {

    @Test
    public void toSavingsAccountRequestMapper() {
        ClientInformationV3 clientInformationV3 = clientInformationV3Builder();
        SavingsAccountRequest savingsAccountRequest = savingsAccountRequestBuilder(clientInformationV3);
        CreateSavingsAccountRequest savingsAccountRequest1 = SavingsAccountsMapper.INSTANCE.toSavingsAccountRequest(savingsAccountRequest);
        assertThat("IdCard is right", savingsAccountRequest1.getClientInformation().getDocumentId().getId(), is(ID_CARD));
        assertThat("Email is right", savingsAccountRequest1.getClientInformation().getEmail(), is(EMAIL));
        assertThat("Name is right", savingsAccountRequest1.getClientInformation().getName(), is(NAME));
    }

    @Test
    public void toSavingsAccountResponseMapper() {
    	SavingAccountCreated createSavingsAccountResponse = createSavingAccountCreated();
        SavingsAccountResponse savingsAccountResponse = SavingsAccountsMapper.INSTANCE.toSavingsAccountResponse(createSavingsAccountResponse);
        assertThat("IdClient is right", savingsAccountResponse.getIdCbs(), is(ID_LOAN));
        assertThat("IdCard is right", savingsAccountResponse.getAccountId(), is(ACCOUNT_ID));
    }

}
