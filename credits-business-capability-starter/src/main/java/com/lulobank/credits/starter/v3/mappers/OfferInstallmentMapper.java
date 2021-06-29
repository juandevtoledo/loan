package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.starter.v3.adapters.in.dto.OfferInstallment;
import com.lulobank.credits.v3.port.in.productoffer.dto.SimulatedInstallment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OfferInstallmentMapper {

    OfferInstallmentMapper INSTANCE = Mappers.getMapper(OfferInstallmentMapper.class);

    OfferInstallment toOfferInstallment(SimulatedInstallment flexibleLoanV3);
}
