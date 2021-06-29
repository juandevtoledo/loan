package com.lulobank.credits.v3.port.out.scheduler.automaticdebit.mapper;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.CreateTransactionRequest;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.TransactionRequest;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.TransactionType;
import com.lulobank.credits.v3.usecase.automaticdebit.command.ProcessPayment;

import static com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.TransactionType.ONE_TIME;
import static com.lulobank.credits.v3.port.out.scheduler.automaticdebit.dto.TransactionType.SUBSCRIPTION;

public class SchedulerAutomaticDebitMapper {

    private SchedulerAutomaticDebitMapper() {
    }

    public static TransactionRequest transactionRequest(ProcessPayment processPayment) {
        return TransactionRequest.builder()
                .dayOfPay(processPayment.getDayOfPay())
                .idClient(processPayment.getIdClient())
                .metadata(processPayment.getMetadataEvent())
                .build();
    }

    public static CreateTransactionRequest createOneTimeTransactionRequest(ProcessPayment processPayment, int newDayOfPaid) {
        return CreateTransactionRequest.builder()
                .dayOfPay(newDayOfPaid)
                .idClient(processPayment.getIdClient())
                .idCredit(processPayment.getIdCredit())
                .metadata(buildMetadata(newDayOfPaid, ONE_TIME))
                .build();
    }

    public static TransactionRequest transactionRequest(CreditsV3Entity creditsV3Entity, String metadata) {
        return TransactionRequest.builder()
                .dayOfPay(creditsV3Entity.getDayOfPay())
                .idClient(creditsV3Entity.getIdClient())
                .metadata(metadata)
                .build();
    }

    public static CreateTransactionRequest createTransactionRequest(CreditsV3Entity creditsV3Entity) {
        return CreateTransactionRequest.builder()
                .idClient(creditsV3Entity.getIdClient())
                .idCredit(creditsV3Entity.getIdCredit().toString())
                .dayOfPay(creditsV3Entity.getDayOfPay())
                .metadata(buildMetadata(creditsV3Entity.getDayOfPay(), SUBSCRIPTION))
                .build();
    }

    public static TransactionRequest transactionRequest(CreditsV3Entity credit) {
        return TransactionRequest.builder()
                .idClient(credit.getIdClient())
                .dayOfPay(credit.getDayOfPay())
                .metadata(buildMetadata(credit.getDayOfPay(), SUBSCRIPTION))
                .build();
    }

    private static String buildMetadata(int dayOfPay, TransactionType transactionType) {
        StringBuilder metadata = new StringBuilder();
        return metadata.append(dayOfPay)
                .append("#")
                .append("credits")
                .append("#")
                .append(transactionType.name())
                .toString();
    }


}
