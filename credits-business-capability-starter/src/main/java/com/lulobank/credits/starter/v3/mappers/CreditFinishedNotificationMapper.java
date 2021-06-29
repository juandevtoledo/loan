package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.services.events.CreditFinishedNotificationEvent;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreditFinishedNotificationMapper {

    String DESCRIPTION = "El dinero del cr\u00E9dito No. %s por $ %s se ha depositado en tu Lulo Cuenta.";

    CreditFinishedNotificationMapper INSTANCE = Mappers.getMapper(CreditFinishedNotificationMapper.class);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "transactionType", constant = "CREDIT_FINISHED")
    @Mapping(target = "inAppNotification.idClient", source = "creditsV3Entity.idClient")
    @Mapping(target = "inAppNotification.tittle", constant = "Desembolso exitoso")
    @Mapping(target = "inAppNotification.dateNotification", expression = "java(java.time.LocalDateTime.now().toString())")
    @Mapping(target = "inAppNotification.action", constant = "OPENED_LOAN")
    CreditFinishedNotificationEvent creditsV3EntityToNewNotificationEvent(CreditsV3Entity creditsV3Entity);

    @AfterMapping
    default void mapDescription(@MappingTarget CreditFinishedNotificationEvent creditFinishedNotificationEvent, CreditsV3Entity creditsV3Entity) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es"));
        symbols.setDecimalSeparator('.');
        DecimalFormat formatter = new DecimalFormat("###,###,###", symbols);
        String descriptionFormatted = String.format(DESCRIPTION, creditsV3Entity.getIdLoanAccountMambu(),
                formatter.format(creditsV3Entity.getAcceptOffer().getAmount()));
        creditFinishedNotificationEvent.setDescription(descriptionFormatted);
        creditFinishedNotificationEvent.getInAppNotification().setDescription(descriptionFormatted);
    }
}
