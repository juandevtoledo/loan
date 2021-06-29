package com.lulobank.credits.v3.service.mapper;

import com.lulobank.credits.v3.dto.ClientInformationV3;
import com.lulobank.credits.v3.events.GoodStandingCertificateEvent;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import io.vavr.control.Option;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static com.lulobank.credits.v3.port.in.loan.LoanState.APPROVED;
import static com.lulobank.credits.v3.port.in.loan.LoanState.CLOSED;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Mapper(imports = {LocalDateTime.class})
public interface CloseLoanServiceMapper {

    CloseLoanServiceMapper INSTANCE = Mappers.getMapper(CloseLoanServiceMapper.class);

    @Mapping(target = "loanStatus.status", constant = "CLOSED")
    @Mapping(target = "loanStatus.certificationSent", constant = "true")
    @Mapping(target = "closedDate", expression = "java(LocalDateTime.now())")
    @Mapping(target = "statementsIndex", source = "creditsV3Entity", qualifiedByName = "statementsIndex")
    CreditsV3Entity loanStatusClosed(CreditsV3Entity creditsV3Entity);

    @Named("statementsIndex")
    default String getStatementsIndex(CreditsV3Entity creditsEntity) {
        return Option.of(creditsEntity.getStatementsIndex())
                .map(index -> index.replace(APPROVED.name(), CLOSED.name()))
                .getOrElse(EMPTY);
    }

    @Mapping(target = "acceptDate", source = "acceptDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "closedDate", source = "closedDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "amount", source = "acceptOffer.amount")
    @Mapping(target = "typeReport", constant = "GOODSTANDINGCERTIFICATE")
    @Mapping(target = "clientInformationByIdClient.content", source = "creditsV3Entity.clientInformation", qualifiedByName = "clientInfoContent")
    GoodStandingCertificateEvent goodStandingCertificateEvent(CreditsV3Entity creditsV3Entity);

    @Named("clientInfoContent")
    default GoodStandingCertificateEvent.Content getContent(ClientInformationV3 clientInformation) {
        return GoodStandingCertificateEvent.Content.builder()
                .idCard(clientInformation.getDocumentId().getId())
                .name(
                        Option.of(clientInformation.getMiddleName())
                                .map(middleName -> clientInformation.getName().concat(" ").concat(middleName))
                                .getOrElse(clientInformation.getName())
                )
                .lastName(
                        Option.of(clientInformation.getSecondSurname())
                                .map(secondSurname -> clientInformation.getLastName().concat(" ").concat(secondSurname))
                                .getOrElse(clientInformation.getLastName()))
                .emailAddress(clientInformation.getEmail())
                .build();
    }
}
