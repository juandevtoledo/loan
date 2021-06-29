package com.lulobank.credits.v3.usecase.installment.util;
import com.lulobank.credits.v3.usecase.installment.dto.ExtraAmountInstallmentResult;
import io.vavr.control.Option;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
public enum ExtraAmountType {

    TOTAL_PAYMENT() {
        @Override
        public boolean predicate(BigDecimal installmentExpected, BigDecimal totalBalance, BigDecimal amount) {
            return amount.compareTo(totalBalance) >= 0;
        }
        @Override
        public ExtraAmountInstallmentResult action(BigDecimal installmentExpected, BigDecimal totalBalance, BigDecimal amount) {
            return ExtraAmountInstallmentResult
                    .builder()
                    .minimumValue(BigDecimal.ZERO)
                    .extraAmount(BigDecimal.ZERO)
                    .totalValue(totalBalance)
                    .paymentType(ExtraAmountType.TOTAL_PAYMENT.name())
                    .build();
        }
    },
    MINIMUM_PAYMENT() {
        @Override
        public boolean predicate(BigDecimal installmentExpected, BigDecimal totalBalance, BigDecimal amount) {
            return  amount.compareTo(installmentExpected) <= 0;
        }

        @Override
        public ExtraAmountInstallmentResult action(BigDecimal installmentExpected, BigDecimal totalBalance, BigDecimal amount) {
            return ExtraAmountInstallmentResult
                    .builder()
                    .minimumValue(amount)
                    .extraAmount(BigDecimal.ZERO)
                    .totalValue(BigDecimal.ZERO)
                    .paymentType(ExtraAmountType.MINIMUM_PAYMENT.name())
                    .build();
        }
    },
    EXTRA_AMOUNT_PAYMENT() {
        @Override
        public boolean predicate(BigDecimal installmentExpected, BigDecimal totalBalance, BigDecimal amount) {
            return installmentExpected.compareTo(BigDecimal.ZERO) == 0;
        }
        @Override
        public ExtraAmountInstallmentResult action(BigDecimal installmentExpected, BigDecimal totalBalance, BigDecimal amount) {
            return ExtraAmountInstallmentResult
                    .builder()
                    .minimumValue(BigDecimal.ZERO)
                    .extraAmount(amount)
                    .totalValue(BigDecimal.ZERO)
                    .paymentType(ExtraAmountType.EXTRA_AMOUNT_PAYMENT.name())
                    .build();
        }
    },
    MINIMUM_AND_EXTRA_AMOUNT_PAYMENTS() {
        @Override
        public boolean predicate(BigDecimal installmentExpected, BigDecimal totalBalance, BigDecimal amount) {
            return installmentExpected.compareTo(BigDecimal.ZERO) > 0;
        }
        @Override
        public ExtraAmountInstallmentResult action(BigDecimal installmentExpected, BigDecimal totalBalance, BigDecimal amount) {
            return ExtraAmountInstallmentResult
                    .builder()
                    .minimumValue(installmentExpected)
                    .extraAmount(amount.subtract(installmentExpected))
                    .totalValue(BigDecimal.ZERO)
                    .paymentType(ExtraAmountType.MINIMUM_AND_EXTRA_AMOUNT_PAYMENTS.name())
                    .build();
        }
    };

    public abstract boolean predicate(BigDecimal installmentExpected, BigDecimal totalBalance, BigDecimal amount);

    public abstract ExtraAmountInstallmentResult action(BigDecimal installmentExpected, BigDecimal totalBalance, BigDecimal amount);


    public static Option<ExtraAmountInstallmentResult> get(BigDecimal installmentExpected, BigDecimal totalBalance, BigDecimal amount) {
        return Option.ofOptional(Arrays.stream(ExtraAmountType.values())
                .filter(extraAmountType -> extraAmountType.predicate(installmentExpected, totalBalance, amount))
                .findFirst()
                .map(extraAmountType -> extraAmountType.action(installmentExpected, totalBalance, amount)));
    }

}
