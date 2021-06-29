package com.lulobank.credits.v3.util;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import com.lulobank.credits.v3.port.out.corebanking.dto.Movement;
import io.vavr.control.Try;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CoreBankingFactory {

    public static class LoanFactory {

        public static LoanInformation loanActive() {
            return getEntityByFile("json/corebanking/LoanInformation.json", LoanInformation.class, Function.identity());
        }

        public static LoanInformation loanPendingApproval() {
            return getEntityByFile("json/corebanking/LoanInformation.json", LoanInformation.class, line->
                   line.replace("\"state\": \"ACTIVE\"","\"state\": \"PENDING_APPROVAL\"") );
        }

        public static LoanInformation loanInArrears() {
            return getEntityByFile("json/corebanking/LoanInformationInArrears.json", LoanInformation.class, Function.identity());
        }

        public static LoanInformation loanPendingPayment() {
            LocalDateTime expectedDate = LocalDateTime.now().plusDays(6);
            String createOn = fieldLocalDateTime("creationOn", LocalDateTime.now().minusMonths(1));
            String cutOffDate = fieldLocalDateTime("cutOffDate", expectedDate.minusDays(10));
            String installmentDate = fieldLocalDateTime("installmentDate", expectedDate);
            return getEntityByFile("json/corebanking/LoanPendingPayment.json", LoanInformation.class,
                    line -> line.replace("\"creationOn\": \"2020-01-27 10:50\"", createOn)
                            .replace("\"cutOffDate\": \"2020-02-17 10:50\"", cutOffDate)
                            .replace("\"installmentDate\": \"2020-02-27 10:50\"", installmentDate)
            );
        }

        public static LoanInformation loanPendingWithLastInstallment() {
            LocalDateTime expectedDate = LocalDateTime.now().plusDays(6);
            String createOn = fieldLocalDateTime("creationOn", LocalDateTime.now().minusMonths(1));
            String cutOffDate = fieldLocalDateTime("cutOffDate", expectedDate.minusDays(10));
            String installmentDate = fieldLocalDateTime("installmentDate", expectedDate);
            return getEntityByFile("json/corebanking/LoanInLastInstallment.json", LoanInformation.class,
                    line -> line.replace("\"creationOn\": \"2020-01-27 10:50\"", createOn)
                            .replace("\"cutOffDate\": \"2020-02-17 10:50\"", cutOffDate)
                            .replace("\"installmentDate\": \"2020-02-27 10:50\"", installmentDate)
            );
        }

        public static LoanInformation loanPaymentIsUpToDate() {
            LocalDateTime expectedDate = LocalDateTime.now().plusMonths(1);
            String createOn = fieldLocalDateTime("creationOn", LocalDateTime.now().minusMonths(1));
            String cutOffDate = fieldLocalDateTime("cutOffDate", expectedDate.minusDays(10));
            String installmentDate = fieldLocalDateTime("installmentDate", expectedDate);
            return getEntityByFile("json/corebanking/LoanPaymentUpToDate.json", LoanInformation.class,
                    line -> line.replace("\"creationOn\": \"2020-01-27 10:50\"", createOn)
                            .replace("\"cutOffDate\": \"2020-02-17 10:50\"", cutOffDate)
                            .replace("\"installmentDate\": \"2020-02-27 10:50\"", installmentDate)
            );
        }

        public static LoanInformation loanForPaymentPlan() {
            return getEntityByFile("json/corebanking/LoanInformationForPaymentPlan.json", LoanInformation.class, Function.identity());
        }

        public static List<Movement> buildLoanMovements() {
            Type listType = new TypeToken<ArrayList<Movement>>() {
            }.getType();
            return getEntityByFile("json/corebanking/LoanMovements.json", listType, Function.identity());
        }

        public static List<Movement> buildLoanMovementsForFiltering() {
            Type listType = new TypeToken<ArrayList<Movement>>() {
            }.getType();
            return getEntityByFile("json/corebanking/LoanMovementsForFiltering.json", listType, Function.identity());
        }

        private static <T> T getEntityByFile(String fileName, Class<T> clazz, Function<String, String> replace) {
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new Util.JsonDateTimeArrayDeserializer()).create();
            return gson.fromJson(geContentFile(fileName, replace), clazz);
        }

        private static <T> T getEntityByFile(String fileName, Type type, Function<String, String> replace) {
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new Util.JsonDateTimeArrayDeserializer()).create();
            return gson.fromJson(geContentFile(fileName, replace), type);
        }

        private static String geContentFile(String s, Function<String, String> replace) {
            URL url = Resources.getResource(s);
            return Try.of(() -> new BufferedReader(new FileReader(url.getPath())))
                    .map(br -> br.lines().collect(Collectors.joining()))
                    .map(replace::apply)
                    .get();
        }

        private static String fieldLocalDateTime(String field, LocalDateTime time) {
            return "\"".concat(field).concat("\"").concat(" : \"").concat(time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).concat("\"");
        }


    }
}
