package com.lulobank.credits.v3.usecase.nextinstallment;

import com.lulobank.credits.v3.port.in.nextinstallment.dto.Flag;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;

import java.util.Arrays;

import static com.lulobank.credits.v3.util.LocalDateComparator.isAfterOrEquals;
import static com.lulobank.credits.v3.util.LocalDateComparator.isBeforeOrEquals;
import static java.time.LocalDate.now;

public enum InstallmentState {

    IN_ARREARS {
        @Override
        public boolean identity(LoanInformation loanInformation) {
            return "ACTIVE_IN_ARREARS".equals(loanInformation.getState());
        }

        @Override
        public Flag flagsByState(LoanInformation loanInformation, CreditsV3Entity credit) {
            return Flag.builder()
                    .payNow(true)
                    .minimumPaymentActive(loanInformation.isMinimumPaymentActive())
                    .automaticDebitActive(credit.getAutomaticDebit())
                    .customerOweMoney(loanInformation.doesCustomerOweMoney())
                    .build();
        }
    },
    NEXT_TO_PAY {
        @Override
        public boolean identity(LoanInformation loanInformation) {
            return isBeforeOrEquals(now(), loanInformation.getInstallmentDate().toLocalDate()) &&
                    isAfterOrEquals(now(), loanInformation.getCutOffDate().toLocalDate()) && loanInformation.doesCustomerOweMoney();
        }

        @Override
        public Flag flagsByState(LoanInformation loanInformation, CreditsV3Entity credit) {
            return Flag.builder()
                    .payNow(false)
                    .minimumPaymentActive(loanInformation.isMinimumPaymentActive())
                    .automaticDebitActive(credit.getAutomaticDebit())
                    .customerOweMoney(loanInformation.doesCustomerOweMoney())
                    .build();
        }
    },
    UP_TO_DAY {
        @Override
        public boolean identity(LoanInformation loanInformation) {
            return isBeforeOrEquals(now(), loanInformation.getInstallmentDate().toLocalDate());
        }

        @Override
        public Flag flagsByState(LoanInformation loanInformation, CreditsV3Entity credit) {

            return Flag.builder()
                    .payNow(false)
                    .minimumPaymentActive(loanInformation.isMinimumPaymentActive())
                    .automaticDebitActive(credit.getAutomaticDebit())
                    .customerOweMoney(loanInformation.doesCustomerOweMoney())
                    .build();
        }
    },
    NONE {
        @Override
        public boolean identity(LoanInformation loanInformation) {
            return false;
        }

        @Override
        public Flag flagsByState(LoanInformation loanInformation, CreditsV3Entity credit) {
            return Flag.builder()
                    .automaticDebitActive(credit.getAutomaticDebit())
                    .build();
        }
    };

    public abstract boolean identity(LoanInformation loanInformation);

    public abstract Flag flagsByState(LoanInformation loanInformation, CreditsV3Entity credit);

    public static InstallmentState get(LoanInformation loanInformation) {
        return Arrays.stream(InstallmentState.values())
                .filter(installmentState -> installmentState.identity(loanInformation))
                .findFirst()
                .orElse(NONE);
    }
}
