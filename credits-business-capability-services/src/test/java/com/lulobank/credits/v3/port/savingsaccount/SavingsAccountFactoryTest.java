package com.lulobank.credits.v3.port.savingsaccount;

import com.lulobank.credits.v3.port.in.savingsaccount.SavingsAccountFactory;
import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountRequest;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.util.EntitiesFactory;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SavingsAccountFactoryTest {


    @Test
    public void mappingLoanRequest() {

        SavingsAccountRequest savingsAccountRequest = SavingsAccountFactory.createSavingsAccountRequest(EntitiesFactory.CreditsEntityFactory.creditsEntityWithAcceptOffer());
        CreditsV3Entity creditsV3Entity = EntitiesFactory.CreditsEntityFactory.creditsEntityWithAcceptOffer();

        assertThat(savingsAccountRequest.getIdClient().equals(creditsV3Entity.getIdClient()), is(true));
        assertThat(savingsAccountRequest.getClientInformation().getDocumentId().getId().equals(creditsV3Entity.getClientInformation().getDocumentId().getId()), is(true));
        assertThat(savingsAccountRequest.getClientInformation().getDocumentId().getIssueDate().equals(creditsV3Entity.getClientInformation().getDocumentId().getIssueDate()), is(true));
        assertThat(savingsAccountRequest.getClientInformation().getEmail().equals(creditsV3Entity.getClientInformation().getEmail()), is(true));
        assertThat(savingsAccountRequest.getClientInformation().getGender().equals(creditsV3Entity.getClientInformation().getGender()), is(true));
        assertThat(savingsAccountRequest.getClientInformation().getLastName().equals(creditsV3Entity.getClientInformation().getLastName()), is(true));
        assertThat(savingsAccountRequest.getClientInformation().getName().equals(creditsV3Entity.getClientInformation().getName()), is(true));

    }

}
