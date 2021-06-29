package com.lulobank.credits.v3.usecase.waitinglist;

import com.lulobank.credits.v3.port.out.clients.ClientService;
import com.lulobank.credits.v3.port.out.productoffer.ProductOfferService;
import com.lulobank.credits.v3.port.out.productoffer.dto.ProductOfferRequest;
import com.lulobank.credits.v3.port.out.productoffer.dto.ProductOfferRequest.ProductOfferStatus;
import com.lulobank.credits.v3.port.out.queue.RiskEngineNotificationService;
import com.lulobank.credits.v3.usecase.waitinglist.command.AddToWaitingListRequest;
import com.lulobank.credits.v3.util.UseCase;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import lombok.CustomLog;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.sleuth.instrument.async.TraceableExecutorService;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

@CustomLog
public class AddToWaitingListUseCase
		implements UseCase<AddToWaitingListRequest, Either<UseCaseResponseError, Boolean>> {

	private final ProductOfferService productOfferService;
	private final BeanFactory beanFactory;
	private final ClientService clientService;
	private final RiskEngineNotificationService riskEngineNotificationService;

	public AddToWaitingListUseCase(BeanFactory beanFactory, ProductOfferService productOfferService,
			ClientService clientService,
			RiskEngineNotificationService riskEngineNotificationService) {

		this.beanFactory = beanFactory;
		this.productOfferService = productOfferService;
		this.clientService = clientService;
		this.riskEngineNotificationService = riskEngineNotificationService;
	}

	@Override
	public Either<UseCaseResponseError, Boolean> execute(AddToWaitingListRequest addToWaitingListRequest) {
		log.info("[AddToWaitingListUseCase] execute()");

		return productOfferService.updateProductOffer(buildProductOfferRequest(addToWaitingListRequest), addToWaitingListRequest.getAuth())
		.peek(response -> Future.run(executor(),
				() -> notifyRiskEngine(addToWaitingListRequest)))
		.map(response -> true);
	}
	
	private void notifyRiskEngine(AddToWaitingListRequest addToWaitingListRequest) {
		clientService.getClientInformation(addToWaitingListRequest.getIdClient(), addToWaitingListRequest.getAuth())
				.peek(riskEngineNotificationService::sendRiskEngineNotification)
				.peek(clientInformationResponse -> log.info(String.format("Risk Engine was notified success, idClient: %s", addToWaitingListRequest.getIdClient())))
				.peekLeft(error -> log.error(String.format("Error trying to notify risk engine: %s", error.getBusinessCode())));
    }
	
	private ProductOfferRequest buildProductOfferRequest(AddToWaitingListRequest addToWaitingListRequest) {
		ProductOfferRequest productOfferRequest = new ProductOfferRequest();
		productOfferRequest.setIdClient(addToWaitingListRequest.getIdClient());
		productOfferRequest.setIdProductOffer(addToWaitingListRequest.getIdProductOffer());
		productOfferRequest.setStatus(ProductOfferStatus.CLOSED);
		return productOfferRequest;
	}

	private Executor executor() {
        return new TraceableExecutorService(beanFactory, ForkJoinPool.commonPool(), "AddToWaitingListUseCase [clients]");
    }
}
