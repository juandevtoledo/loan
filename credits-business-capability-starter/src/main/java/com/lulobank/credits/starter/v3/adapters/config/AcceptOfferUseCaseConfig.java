package com.lulobank.credits.starter.v3.adapters.config;

import com.lulobank.credits.starter.v3.adapters.out.otp.config.ValidateOtpServiceConfig;
import com.lulobank.credits.starter.v3.adapters.out.schedule.config.ScheduleAdapterConfig;
import com.lulobank.credits.starter.v3.handler.AcceptOfferHandler;
import com.lulobank.credits.v3.dto.CreditsConditionV3;
import com.lulobank.credits.v3.port.in.loan.LoanV3Service;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.clients.ClientService;
import com.lulobank.credits.v3.port.out.otp.ValidateOtpService;
import com.lulobank.credits.v3.port.out.productoffer.ProductOfferService;
import com.lulobank.credits.v3.port.out.promissorynote.PromissoryNoteAsyncService;
import com.lulobank.credits.v3.port.out.saving.SavingAccountService;
import com.lulobank.credits.v3.service.PreApproveOfferService;
import com.lulobank.credits.v3.usecase.acceptoffer.AcceptOfferUseCase;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ ValidateOtpServiceConfig.class, ScheduleAdapterConfig.class})
public class AcceptOfferUseCaseConfig {

	@Bean
	public AcceptOfferUseCase getAcceptOfferUseCase(PromissoryNoteAsyncService promissoryNoteAsyncService,
			CreditsV3Repository creditsV3Repository, BeanFactory beanFactory, CreditsConditionV3 creditsConditionV3,
			LoanV3Service loanV3Service, SavingAccountService savingAccountService,
			ProductOfferService productOfferService, ValidateOtpService validateOtpService,
			ClientService clientService) {

		return new AcceptOfferUseCase(promissoryNoteAsyncService, creditsV3Repository, new PreApproveOfferService(),
				creditsConditionV3, loanV3Service, savingAccountService, productOfferService, validateOtpService, clientService);

	}

	@Bean
	public AcceptOfferHandler getAcceptOfferHandler(AcceptOfferUseCase acceptOfferUseCase) {
		return new AcceptOfferHandler(acceptOfferUseCase);
	}

}
