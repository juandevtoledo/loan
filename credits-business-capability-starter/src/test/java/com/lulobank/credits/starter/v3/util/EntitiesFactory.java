package com.lulobank.credits.starter.v3.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lulobank.credits.starter.v3.adapters.in.automaticdebitoption.dto.UpdateAutomaticDebitOptiontRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.nextinstallment.NextInstallmentResponse;
import com.lulobank.credits.v3.port.in.nextinstallment.dto.NextInstallment;
import com.lulobank.credits.v3.port.in.rescheduledloan.RescheduledLoanMessage;
import com.lulobank.credits.v3.usecase.loandetail.dto.LoanDetail;
import com.lulobank.credits.v3.usecase.movement.dto.Movement;
import com.lulobank.credits.v3.usecase.payment.dto.PaymentResult;
import com.lulobank.credits.v3.usecase.paymentplan.command.PaymentPlanUseCaseResponse;
import io.vavr.control.Try;
import org.modelmapper.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ResourceUtils.getFile;

public class EntitiesFactory {

    public static class PaymentPlanFactory {

        public static PaymentPlanUseCaseResponse paymentPlanUseCaseResponse() {
            return getObjectByFile("classpath:mocks/v3/Payment-Plan-Response.json",
                    PaymentPlanUseCaseResponse.class);
        }

    }

    public static class RescheduledLoanEventFactory {

        public static RescheduledLoanMessage ok() {
            return getObjectByFile("classpath:mocks/rescheduledloan/RescheduledLoan.json",
                    RescheduledLoanMessage.class);
        }
    }

    public static class LoanInformationFactory {

        public static NextInstallment getNextInstallment() {
            return getObjectByFile("classpath:mocks/v3/nextinstallment/nextInstallmentUseCaseResponse.json",
                    NextInstallment.class);
        }

        public static NextInstallmentResponse getNextInstallmentResponse() {
            return getObjectByFile("classpath:mocks/v3/nextinstallment/nextInstallmentResponse.json",
                    NextInstallmentResponse.class);
        }

        public static LoanDetail getLoanDetail() {
            return getObjectByFile("classpath:mocks/v3/loandetail/LoanDetailUseCaseResponse.json",
                    LoanDetail.class);
        }

        public static List<Movement> getLoanMovements() {
            Type listType = new TypeToken<ArrayList<Movement>>(){}.getType();
            return getObjectByFile("classpath:mocks/v3/movements/LoanMovementsResponseOk.json",
                    listType);
        }
    }

    public static class AutomaticDebitFactory {

        public static UpdateAutomaticDebitOptiontRequest updateAutomaticDebitOptiontRequest() {
            return new UpdateAutomaticDebitOptiontRequest(true);
        }
    }

    public static class PaymentFactory {

        public static PaymentResult paymentResponse(LocalDateTime time ) {
            return  PaymentResult.builder()
                    .amountPaid(BigDecimal.valueOf(3000000d))
                    .date(time)
                    .transactionId("transaction_id")
                    .build();
        }
    }

    private static <T> T getObjectByFile(String file, Class<T> clazz) {
        return getGson().fromJson(getContentFile(file), clazz);
    }

    private static <T> T getObjectByFile(String file, Type type) {
        return getGson().fromJson(getContentFile(file), type);
    }

    private static String getContentFile(String file) {
        return Try.of(() -> new BufferedReader(new FileReader(getFile(file).getPath())))
                .map(br -> br.lines().collect(Collectors.joining()))
                .get();
    }

    private static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new Util.JsonDateArrayDeserializer())
                .registerTypeAdapter(LocalDateTime.class, new Util.JsonDateTimeArrayDeserializer()).create();
    }
}
