package com.lulobank.credits.v3.usecase.installment.util;

import com.lulobank.credits.v3.usecase.installment.dto.ExtraAmountInstallmentResult;
import io.vavr.control.Option;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class ExtraAmountTypeTest {


    @Test
    public void minimumPaidTest(){
        BigDecimal instalmentTotalDue = BigDecimal.valueOf(10000);
        BigDecimal totalBalance = BigDecimal.valueOf(50000);
        BigDecimal amount = BigDecimal.valueOf(10000);
        Option<ExtraAmountInstallmentResult> result = ExtraAmountType.get(instalmentTotalDue,totalBalance,amount);

        Assert.assertEquals(amount,result.get().getMinimumValue());
        Assert.assertEquals(BigDecimal.ZERO,result.get().getExtraAmount());
        Assert.assertEquals(BigDecimal.ZERO,result.get().getTotalValue());
        Assert.assertEquals(ExtraAmountType.MINIMUM_PAYMENT.name(),result.get().getPaymentType());

    }

    @Test
    public void extraAmountPaidTest(){
        BigDecimal instalmentTotalDue = BigDecimal.ZERO;
        BigDecimal totalBalance = BigDecimal.valueOf(50000);
        BigDecimal amount = BigDecimal.valueOf(2000);
        Option<ExtraAmountInstallmentResult> result = ExtraAmountType.get(instalmentTotalDue,totalBalance,amount);

        Assert.assertEquals(BigDecimal.ZERO,result.get().getMinimumValue());
        Assert.assertEquals(amount,result.get().getExtraAmount());
        Assert.assertEquals(BigDecimal.ZERO,result.get().getTotalValue());
        Assert.assertEquals(ExtraAmountType.EXTRA_AMOUNT_PAYMENT.name(),result.get().getPaymentType());

    }

    @Test
    public void minimumAndExtraPaidTest(){
        BigDecimal instalmentTotalDue = BigDecimal.valueOf(10000);
        BigDecimal totalBalance = BigDecimal.valueOf(50000);
        BigDecimal amount = BigDecimal.valueOf(20000);
        Option<ExtraAmountInstallmentResult> result = ExtraAmountType.get(instalmentTotalDue,totalBalance,amount);

        Assert.assertEquals(instalmentTotalDue,result.get().getMinimumValue());
        Assert.assertEquals(amount.subtract(instalmentTotalDue),result.get().getExtraAmount());
        Assert.assertEquals(BigDecimal.ZERO,result.get().getTotalValue());
        Assert.assertEquals(ExtraAmountType.MINIMUM_AND_EXTRA_AMOUNT_PAYMENTS.name(),result.get().getPaymentType());

    }

    @Test
    public void totalPaidTest(){
        BigDecimal instalmentTotalDue = BigDecimal.valueOf(10000);
        BigDecimal totalBalance = BigDecimal.valueOf(50000);
        BigDecimal amount = BigDecimal.valueOf(50000);
        Option<ExtraAmountInstallmentResult> result = ExtraAmountType.get(instalmentTotalDue,totalBalance,amount);

        Assert.assertEquals(BigDecimal.ZERO,result.get().getMinimumValue());
        Assert.assertEquals(BigDecimal.ZERO,result.get().getExtraAmount());
        Assert.assertEquals(totalBalance,result.get().getTotalValue());
        Assert.assertEquals(ExtraAmountType.TOTAL_PAYMENT.name(),result.get().getPaymentType());

    }

    @Test
    public void amountHigherTotalPaidTest(){
        BigDecimal instalmentTotalDue = BigDecimal.valueOf(10000);
        BigDecimal totalBalance = BigDecimal.valueOf(50000);
        BigDecimal amount = BigDecimal.valueOf(200000);
        Option<ExtraAmountInstallmentResult> result = ExtraAmountType.get(instalmentTotalDue,totalBalance,amount);

        Assert.assertEquals(BigDecimal.ZERO,result.get().getMinimumValue());
        Assert.assertEquals(BigDecimal.ZERO,result.get().getExtraAmount());
        Assert.assertEquals(totalBalance,result.get().getTotalValue());
        Assert.assertEquals(ExtraAmountType.TOTAL_PAYMENT.name(),result.get().getPaymentType());

    }
}
