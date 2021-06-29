package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.services.events.ApprovedLoanDocument;
import com.lulobank.credits.v3.dto.ClientInformationV3;
import com.lulobank.credits.v3.dto.CreditType;
import com.lulobank.credits.v3.service.LoanTransaction;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApprovedLoanDocumentMapper {

    ApprovedLoanDocumentMapper INSTANCE = Mappers.getMapper(ApprovedLoanDocumentMapper.class);

    @Mapping(target = "clientInfo.idClient", source = "entity.idClient")
    @Mapping(target = "clientInfo.idCard", source = "entity.clientInformation.documentId.id")
    @Mapping(target = "clientInfo.name", source = "entity.clientInformation.name")
    @Mapping(target = "clientInfo.lastName", source = "entity.clientInformation.lastName")
    @Mapping(target = "decevalInformation.clientAccountId", source = "entity.decevalInformation.clientAccountId")
    @Mapping(target = "decevalInformation.promissoryNoteId", source = "entity.decevalInformation.promissoryNoteId")
    @Mapping(target = "decevalInformation.decevalId", source = "entity.decevalInformation.decevalCorrelationId")
    @Mapping(target = "decevalInformation.confirmationLoanOTP", source = "entity.decevalInformation.confirmationLoanOTP")
    @Mapping(target = "acceptOfferDateTime", source = "entity.acceptDate", dateFormat = "dd-MMM-yyyy HH:mm")
    @Mapping(target = "requestedAmount", source = "entity.loanRequested.amount")
    @Mapping(target = "approvedAmount", source = "entity.acceptOffer.amount")
    @Mapping(target = "installments", source = "entity.acceptOffer.installments")
    @Mapping(target = "interestRate", source = "entity.acceptOffer.interestRate")
    @Mapping(target = "automaticDebit", expression = "java(loanTransaction.getEntity().getAutomaticDebit() ? 1 : 0)")
    @Mapping(target = "paymentDay", source = "entity.dayOfPay")
    ApprovedLoanDocument loanTransactionToApprovedLoanDocument(LoanTransaction loanTransaction);

    @AfterMapping
    default void mapFullName(@MappingTarget ApprovedLoanDocument approvedLoanDocument, LoanTransaction loanTransaction) {
        ApprovedLoanDocument.ClientInfo clientInfo = approvedLoanDocument.getClientInfo();
        ClientInformationV3 clientInformation = loanTransaction.getEntity().getClientInformation();
        clientInfo.setName(clientInformation.getName().concat(SPACE).concat(emptyIfNull(clientInformation.getMiddleName())).trim());
        clientInfo.setLastName(clientInformation.getLastName().concat(SPACE).concat(emptyIfNull(clientInformation.getSecondSurname())).trim());
    }
    
    @AfterMapping
    default void mapPreaproveAmount(@MappingTarget ApprovedLoanDocument approvedLoanDocument, LoanTransaction loanTransaction) {
    	if(CreditType.PREAPPROVED.equals(loanTransaction.getEntity().getCreditType())) { 
    		approvedLoanDocument.setRequestedAmount(loanTransaction.getEntity().getInitialOffer().getAmount());
    		approvedLoanDocument.setApprovedAmount(loanTransaction.getEntity().getInitialOffer().getMaxAmount());
    	}
    }

    default String emptyIfNull(String field) {
        return isNull(field) ? EMPTY : field;
    }
}
