package com.lulobank.credits.services;

import java.math.BigDecimal;
import java.util.UUID;

public final class Constant {
    public static final String ID_CLIENT = UUID.randomUUID().toString();
    public static final String ID_CREDIT = UUID.randomUUID().toString();
    public static final String ID_PRODUCT_OFFER = UUID.randomUUID().toString();
    public static final Double FEE_INSURANCE = 0.0312;
    public static final Float INTEREST_RATE = 18f;
    public static final Double AMOUNT_LOAN = 2000000d;
    public static final Double AMOUNT_LOAN_INSTALLMENT = 1000d;
    public static final String ID_OFFER_COMFORTABLE_LOAN = UUID.randomUUID().toString();
    public static final String ID_OFFER_FAST_LOAN = UUID.randomUUID().toString();
    public static final String ID_OFFER_FLEXIBLE_LOAN = UUID.randomUUID().toString();
    public static final Float MONTHLY_NOMINAL_RATE = 1.28f;
    public static final BigDecimal TOTAL_DUE = new BigDecimal(288696d);
    public static final BigDecimal INTEREST_DUE = new BigDecimal(149178d);
    public static final BigDecimal FEES_DUE = new BigDecimal(2726d);
    public static final Integer INSTALLMENT = 48;
    public static final BigDecimal AMOUNT_PAYMENT = new BigDecimal(300000d);
}
