package com.lulobank.credits.v3.service;

import com.lulobank.credits.v3.port.in.loan.dto.LoanResponse;
import com.lulobank.credits.v3.port.in.promissorynote.dto.PromissoryNoteResponse;
import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountResponse;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import lombok.Getter;

@Getter
public class LoanTransaction {

    private PromissoryNoteResponse promissoryNoteResponse;

    private SavingsAccountResponse savingsAccountResponse;

    private LoanResponse loanResponse;

    private CreditsV3Entity entity;
    
    public LoanTransaction setSavingsAccountResponse(SavingsAccountResponse savingsAccountResponse) {
    	this.savingsAccountResponse = savingsAccountResponse;
    	return this;
    }
    
    public LoanTransaction setPromissoryNoteResponse(PromissoryNoteResponse promissoryNoteResponse) {
    	this.promissoryNoteResponse = promissoryNoteResponse;
    	return this;
    }
    
    public LoanTransaction setLoanResponse(LoanResponse loanResponse) {
    	this.loanResponse = loanResponse;
    	return this;
    }
    
    public LoanTransaction setCreditsV3Entity(CreditsV3Entity entity) {
    	this.entity = entity;
    	return this;
    }

}
