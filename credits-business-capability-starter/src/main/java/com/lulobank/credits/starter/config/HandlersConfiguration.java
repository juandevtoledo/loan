package com.lulobank.credits.starter.config;

import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.core.crosscuttingconcerns.PostActionsDecoratorHandler;
import com.lulobank.core.crosscuttingconcerns.ValidatorDecoratorHandler;
import com.lulobank.core.validations.Validator;
import com.lulobank.credits.sdk.dto.clientloandetail.GetClientLoan;
import com.lulobank.credits.sdk.dto.payment.InstallmentPaidResponse;
import com.lulobank.credits.sdk.dto.payment.PaymentInstallment;
import com.lulobank.credits.services.domain.CreditsConditionDomain;
import com.lulobank.credits.services.features.clientloandetail.ClientLoanDetailHandler;
import com.lulobank.credits.services.features.clientloandetail.ClientLoanValidator;
import com.lulobank.credits.services.features.clientproductoffer.ClientProductOfferHandler;
import com.lulobank.credits.services.features.getloandetail.GetLoanDetail;
import com.lulobank.credits.services.features.getloandetail.GetLoanDetailHandler;
import com.lulobank.credits.services.features.getloandetail.GetLoanDetailValidator;
import com.lulobank.credits.services.features.payment.PaymentInstallmentHandler;
import com.lulobank.credits.services.features.payment.SendMessageGoodStandingToSqs;
import com.lulobank.credits.services.features.riskmodelscore.model.ClientProductOffer;
import com.lulobank.credits.services.features.riskmodelscore.validator.ClientProductOfferValidator;
import com.lulobank.credits.services.features.services.SQSMessageService;
import com.lulobank.credits.services.outboundadapters.repository.CreditsRepository;
import com.lulobank.credits.services.outboundadapters.sqs.SendMessageToReportingSQS;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.service.NextInstallmentsService;
import flexibility.client.sdk.FlexibilitySdk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class HandlersConfiguration {

    @Autowired
    private CreditsRepository repository;

    @Autowired
    private CreditsV3Repository creditsV3Repository;

    @Autowired

    private SQSMessageService sqsMessageService;
    @Autowired
    private FlexibilitySdk flexibilitySdk;

    @Autowired
    private CreditsConditionDomain creditsConditionDomain;

    @Bean
    @Qualifier("creditProductsDecoratorHandler")
    public ValidatorDecoratorHandler creditProductsDecoratorHandler() {
        List<Validator<GetLoanDetail>> validators = new ArrayList<>();
        validators.add(new GetLoanDetailValidator());

        return new ValidatorDecoratorHandler<>(new GetLoanDetailHandler(flexibilitySdk, creditsV3Repository), validators);
    }


    @Bean
    @Qualifier("productOfferedClientDecoratorHandlerV2")
    public ValidatorDecoratorHandler productOfferedClientDecoratorHandlerV2() {
        List<Validator<ClientProductOffer>> validators = new ArrayList<>();
        validators.add(new ClientProductOfferValidator());

        return new ValidatorDecoratorHandler<>(new ClientProductOfferHandler(repository), validators);
    }

    @Bean
    @Qualifier("clientLoanDecoratorHandler")
    public ValidatorDecoratorHandler clientLoanDecoratorHandler(NextInstallmentsService nextInstallmentsService) {
        List<Validator<GetClientLoan>> validators = new ArrayList<>();
        validators.add(new ClientLoanValidator());
        return new ValidatorDecoratorHandler<>(
                new ClientLoanDetailHandler(this.flexibilitySdk, this.repository, nextInstallmentsService), validators);
    }

    @Bean
    @Qualifier("paymentInstallmentDecoratorHandler")
    public PostActionsDecoratorHandler paymentInstallmentDecoratorHandler() {
        List<Action<Response<InstallmentPaidResponse>, PaymentInstallment>> actions = new ArrayList<>();

        SendMessageToReportingSQS sendMessageGoodStandingToSqs = new SendMessageGoodStandingToSqs(sqsMessageService);
        actions.add(sendMessageGoodStandingToSqs);
        return new PostActionsDecoratorHandler<>(new PaymentInstallmentHandler(this.flexibilitySdk, this.creditsV3Repository), actions);
    }

    @Bean
    public NextInstallmentsService nextInstallmentsService() {
        return new NextInstallmentsService(creditsConditionDomain);
    }
}
