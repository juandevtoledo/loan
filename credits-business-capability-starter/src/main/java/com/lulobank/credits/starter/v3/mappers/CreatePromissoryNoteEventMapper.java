package com.lulobank.credits.starter.v3.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.lulobank.credits.services.events.CreatePromissoryNoteMessage;
import com.lulobank.credits.v3.port.out.promissorynote.dto.PromissoryNoteAsyncServiceRequest;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.credits.v3.usecase.command.AcceptOffer;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreatePromissoryNoteEventMapper {

	String ID_CREDIT = "idCredit";
	
	CreatePromissoryNoteEventMapper INSTANCE = Mappers.getMapper(CreatePromissoryNoteEventMapper.class);

	@Mapping(target = "name", source = "loanTransaction.entity.clientInformation.name")
	@Mapping(target = "lastName", source = "loanTransaction.entity.clientInformation.lastName")
	@Mapping(target = "middleName", source = "loanTransaction.entity.clientInformation.middleName")
	@Mapping(target = "secondSurname", source = "loanTransaction.entity.clientInformation.secondSurname")
	@Mapping(target = "email", source = "loanTransaction.entity.clientInformation.email")
	@Mapping(target = "documentId", source = "loanTransaction.entity.clientInformation.documentId")
	@Mapping(target = "idClient", source = "loanTransaction.entity.idClient")
	@Mapping(target = ID_CREDIT, source = ID_CREDIT)
	@Mapping(target = "headers", source = "acceptOffer.credentials.headers")
	@Mapping(target = "headersToSQS", source = "acceptOffer.credentials.headersToSQS")
	@Mapping(target = "confirmationLoanOTP", source = "acceptOffer.confirmationLoanOTP")
	@Mapping(target = "idCbs", source = "loanTransaction.savingsAccountResponse.idCbs")
	@Mapping(target = "accountId", source = "loanTransaction.savingsAccountResponse.accountId")
	CreatePromissoryNoteMessage loanTransactionToCreatePromissoryNoteMessage(LoanTransaction loanTransaction,
			String idCredit, AcceptOffer acceptOffer);
	
	@Mapping(target = "name", source = "loanTransaction.entity.clientInformation.name")
	@Mapping(target = "lastName", source = "loanTransaction.entity.clientInformation.lastName")
	@Mapping(target = "middleName", source = "loanTransaction.entity.clientInformation.middleName")
	@Mapping(target = "secondSurname", source = "loanTransaction.entity.clientInformation.secondSurname")
	@Mapping(target = "email", source = "loanTransaction.entity.clientInformation.email")
	@Mapping(target = "documentId", source = "loanTransaction.entity.clientInformation.documentId")
	@Mapping(target = "idClient", source = "loanTransaction.entity.idClient")
	@Mapping(target = ID_CREDIT, source = ID_CREDIT)
	@Mapping(target = "headers", source = "promissoryNoteAsyncServiceReques.credentials.headers")
	@Mapping(target = "headersToSQS", source = "promissoryNoteAsyncServiceReques.credentials.headersToSQS")
	@Mapping(target = "confirmationLoanOTP", source = "promissoryNoteAsyncServiceReques.confirmationLoanOTP")
	@Mapping(target = "idCbs", source = "loanTransaction.savingsAccountResponse.idCbs")
	@Mapping(target = "accountId", source = "loanTransaction.savingsAccountResponse.accountId")
	CreatePromissoryNoteMessage loanTransactionToCreatePromissoryNoteMessage(LoanTransaction loanTransaction,
			String idCredit, PromissoryNoteAsyncServiceRequest promissoryNoteAsyncServiceReques);
}
