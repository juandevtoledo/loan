package com.lulobank.credits.v3.mapper;

import com.lulobank.credits.v3.events.CreateStatementMessage;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanStatement;

import java.util.HashMap;
import java.util.Map;

public class LoanStatementCreatedMapper {

    public static CreateStatementMessage loanStatementCreatedFrom(CreditsV3Entity creditsV3Entity, LoanStatement loanStatement) {
        return CreateStatementMessage.builder()
                .idClient(creditsV3Entity.getIdClient())
                .productType("LOAN_ACCOUNT")
                .reportType("LOANSTATEMENTS")
                .data(getDataByEntity(creditsV3Entity, loanStatement))
                .build();
    }

    private static Map<String, Object> getDataByEntity(CreditsV3Entity creditsV3Entity, LoanStatement loanStatement) {
        Map<String, Object> data = new HashMap<>();
        data.put("automaticDebit", creditsV3Entity.getAutomaticDebit());
        data.put("idCreditCBS", creditsV3Entity.getIdLoanAccountMambu());
        data.put("idClientCBS", creditsV3Entity.getIdClientMambu());
        data.put("idCard", creditsV3Entity.getClientInformation().getDocumentId().getId());
        data.put("name", creditsV3Entity.getClientInformation().getName());
        data.put("lastName", creditsV3Entity.getClientInformation().getLastName());
        data.put("middleName", creditsV3Entity.getClientInformation().getMiddleName());
        data.put("secondSurname", creditsV3Entity.getClientInformation().getSecondSurname());
        data.put("email", creditsV3Entity.getClientInformation().getEmail());
        data.put("statementDate", loanStatement.getStatementDate());
        data.put("interestRate", loanStatement.getInterestRate());
        
        data.put("totalInstalments", loanStatement.getTotalInstalments());
        data.put("cutOffDate", loanStatement.getCutOffDate());
        data.put("instalmentDueDate", loanStatement.getInstalmentDueDate());
        
        data.put("instalmentTotalDue", loanStatement.getInstalmentTotalDue());
        data.put("instalmentPrincipalDue", loanStatement.getInstalmentPrincipalDue());
        data.put("instalmentInterestDue", loanStatement.getInstalmentInterestDue());
        data.put("instalmentPenaltiesDue", loanStatement.getInstalmentPenaltiesDue());
        data.put("inArrearsBalance", loanStatement.getInArrearsBalance());
        data.put("insuranceFee", loanStatement.getInsuranceFee());
        data.put("legalExpenses", loanStatement.getLegalExpenses());
        data.put("currentInstalment", loanStatement.getCurrentInstalment());
        data.put("lastPeriodTotalPaid", loanStatement.getLastPeriodTotalPaid());
        data.put("lastPeriodPrincipalPaid", loanStatement.getLastPeriodPrincipalPaid());
        data.put("lastPeriodInterestPaid", loanStatement.getLastPeriodInterestPaid());
        data.put("lastPeriodPenaltyPaid", loanStatement.getLastPeriodPenaltyPaid());
		data.put("lastPeriodInsuranceFee", loanStatement.getLastPeriodInsuranceFee());
		data.put("lastPeriodLegalExpenses", loanStatement.getLastPeriodLegalExpenses());
		data.put("totalBalance", loanStatement.getTotalBalance());
		data.put("principalPaid", loanStatement.getPrincipalPaid());
		data.put("loanAmount", loanStatement.getLoanAmount());
		data.put("disbursementDate", loanStatement.getDisbursementDate());
		data.put("penaltyRate", loanStatement.getPenaltyRate());
		data.put("amortization", loanStatement.getAmortization());
		data.put("daysInArrears", loanStatement.getDaysInArrears());
		data.put("loanState", loanStatement.getLoanState());
        return data;
    }
}