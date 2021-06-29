package com.lulobank.credits.v3.port.in.savingsaccount;

import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountRequest;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;

public class SavingsAccountFactory {

    public static SavingsAccountRequest createSavingsAccountRequest(CreditsV3Entity creditsV3Entity){
        SavingsAccountRequest savingsAccountRequest = new SavingsAccountRequest();
        savingsAccountRequest.setClientInformation(creditsV3Entity.getClientInformation());
        savingsAccountRequest.setIdClient(creditsV3Entity.getIdClient());
        return savingsAccountRequest;
    }


}
