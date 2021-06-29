package com.lulobank.credits.v3.usecase.intialsoffersv3.mapper;

import com.lulobank.credits.v3.dto.InitialOfferV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.usecase.intialsoffersv3.command.GetOffersByClient;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

@Mapper
public interface CreditsV3EntityMapper {

    CreditsV3EntityMapper INSTANCE = Mappers.getMapper(CreditsV3EntityMapper.class);

    @Mapping(target = "loanRequested.purpose", source = "getOffersByClient.loanPurpose")
    @Mapping(target = "loanRequested.amount", source = "getOffersByClient.clientLoanRequestedAmount")
    @Mapping(target = "initialOffer", source = "initialOfferV3")
    CreditsV3Entity     creditsV3EntityTo(InitialOfferV3 initialOfferV3, GetOffersByClient getOffersByClient);

    @BeforeMapping
    default void setId(@MappingTarget CreditsV3Entity creditsV3Entity) {
        creditsV3Entity.setIdCredit(UUID.randomUUID());
    }

    @AfterMapping
    default void setInterestRate(@MappingTarget CreditsV3Entity creditsV3Entity) {
        creditsV3Entity.getInitialOffer().setGenerateDate(LocalDateTime.now());
    }
}
