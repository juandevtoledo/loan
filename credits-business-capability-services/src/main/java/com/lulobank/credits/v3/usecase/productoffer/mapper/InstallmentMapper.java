package com.lulobank.credits.v3.usecase.productoffer.mapper;

import com.lulobank.credits.v3.dto.FlexibleLoanV3;
import com.lulobank.credits.v3.port.in.productoffer.dto.SimulatedInstallment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InstallmentMapper {

    InstallmentMapper INSTANCE = Mappers.getMapper(InstallmentMapper.class);

    SimulatedInstallment toInstallment(FlexibleLoanV3 flexibleLoanV3);
}
