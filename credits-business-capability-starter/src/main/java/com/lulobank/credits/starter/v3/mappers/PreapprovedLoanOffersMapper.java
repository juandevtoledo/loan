package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.starter.v3.adapters.in.dto.PreapprovedLoanOffersResponse;
import com.lulobank.credits.v3.usecase.preappoveloanoffers.dto.OfferedResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PreapprovedLoanOffersMapper {

    PreapprovedLoanOffersMapper INSTANCE = Mappers.getMapper(PreapprovedLoanOffersMapper.class);

    PreapprovedLoanOffersResponse preapprovedLoanOffersResponseTo(OfferedResponse offeredResponse);
}
