package com.lulobank.credits.v3.service;

import com.lulobank.credits.sdk.dto.clientloandetail.Installment;
import com.lulobank.credits.services.domain.CreditsConditionDomain;
import com.lulobank.credits.v3.service.dto.InstallmentDetail;
import com.lulobank.credits.v3.service.dto.LoanDetail;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.CbsLoanStateEnum.LATE;
import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.CbsLoanStateEnum.PARTIALLY_PAID;
import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.CbsLoanStateEnum.PENDING;
import static com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.FlexibilityLoanMovementsPaidStatesEnum.PAID;
import static com.lulobank.credits.services.utils.DatesUtil.TIMESTAMP_FORMAT;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.math.BigDecimal.ZERO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocalDateTime.class})
public class NextInstallmentsServiceTest {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    private static final String TOTAL_DUE_IS_OK_MESSAGE = "Total due is ok";
    private static final String INSTALLMENT_PAID_IS_FALSE_MESSAGE = "Installment Paid is false";
    private static final String DISABLE_MINIMUM_IS_FALSE_MESSAGE = "Disable minimum is false";
    private static final String DUE_ON_FORMATTER_IS_RIGHT_MESSAGE = "Due On Formatter is right";
    public static final BigDecimal INSTALMENT_DUE = BigDecimal.valueOf(560000d);
    public static final BigDecimal TOTAL_DUE = INSTALMENT_DUE.multiply(BigDecimal.valueOf(2));
    private CreditsConditionDomain creditsConditionDomain;
    private NextInstallmentsService nextInstallmentsService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        creditsConditionDomain = new CreditsConditionDomain();
        creditsConditionDomain.setMinimumPayDay(15);
        creditsConditionDomain.setFeeAmountInstallement(1d);
        nextInstallmentsService = new NextInstallmentsService(creditsConditionDomain);
    }

    @Test
    public void getNextInstallment_when_instalment_is_partial_paid() {
        LoanDetail loanDetail = loanDetailBuilder().installments(getTest_getNextInstallment_Partial_Paid()).build();
        Installment installment = nextInstallmentsService.get(loanDetail);
        assertFalse(Objects.isNull(installment));
        assertTrue(installment.isInstallmentPaid());
        String dateNextInstallmentExpectedStr = getDateString(LocalDateTime.now().plusDays(1).plusMonths(1));
        String dateNextInstallmentStr = getDateString(LocalDateTime.parse(installment.getDueOn()));
        assertEquals(dateNextInstallmentExpectedStr, dateNextInstallmentStr);
        assertTrue(DUE_ON_FORMATTER_IS_RIGHT_MESSAGE, validateDateFormatter(installment.getDueOn()));
    }

    @Test
    public void getNextInstallment_when_instalment_is_paid() {
        LoanDetail loanDetail = loanDetailBuilder().installments(getTest_getNextInstallment_Paid()).build();
        Installment installment = nextInstallmentsService.get(loanDetail);
        assertFalse(Objects.isNull(installment));
        assertTrue(installment.isInstallmentPaid());
        String dateNextInstallmentExpectedStr = getDateString(LocalDateTime.now().plusDays(1).plusMonths(1));
        String dateNextInstallmentStr = getDateString(LocalDateTime.parse(installment.getDueOn()));
        assertEquals(dateNextInstallmentExpectedStr, dateNextInstallmentStr);
        assertTrue(DUE_ON_FORMATTER_IS_RIGHT_MESSAGE, validateDateFormatter(installment.getDueOn()));
    }

    @Test
    public void getNextInstallment_is_paid_in_same_months() {
        LoanDetail loanDetail = loanDetailBuilder().installments(getTest_getNextInstallment_Paid_same_months()).build();
        Installment installment = nextInstallmentsService.get(loanDetail);
        assertFalse(Objects.isNull(installment));
        assertTrue(installment.isInstallmentPaid());
        String dateNextInstallmentExpectedStr = getDateString(LocalDateTime.now().plusMonths(1));
        String dateNextInstallmentStr = getDateString(LocalDateTime.parse(installment.getDueOn()));
        assertEquals(dateNextInstallmentExpectedStr, dateNextInstallmentStr);
        assertTrue(DUE_ON_FORMATTER_IS_RIGHT_MESSAGE, validateDateFormatter(installment.getDueOn()));
    }

    @Test
    public void getNextInstallment_when_is_not_paid() {
        LoanDetail loanDetail = loanDetailBuilder().installments(getTest_getNextInstallment_NotPaid()).build();
        Installment installment = nextInstallmentsService.get(loanDetail);
        assertFalse(Objects.isNull(installment));
        assertFalse(installment.isInstallmentPaid());
        String dateNextInstallmentExpectedStr = getDateString(LocalDateTime.now().plusDays(1));
        String dateNextInstallmentStr = getDateString(LocalDateTime.parse(installment.getDueOn()));
        assertEquals(dateNextInstallmentExpectedStr, dateNextInstallmentStr);
        assertTrue(DUE_ON_FORMATTER_IS_RIGHT_MESSAGE, validateDateFormatter(installment.getDueOn()));
    }

    @Test
    public void getNextInstallment_when_paid_is_due() {
        LoanDetail loanDetail = loanDetailBuilder().installments(getTest_getNextInstallment_late()).build();
        Installment installment = nextInstallmentsService.get(loanDetail);
        assertFalse(Objects.isNull(installment));
        assertFalse(installment.isInstallmentPaid());
        String dateNextInstallmentExpectedStr = getDateString(LocalDateTime.now().plusMonths(-1));
        String dateNextInstallmentStr = getDateString(LocalDateTime.parse(installment.getDueOn()));
        assertEquals(dateNextInstallmentExpectedStr, dateNextInstallmentStr);
        assertTrue(DUE_ON_FORMATTER_IS_RIGHT_MESSAGE, validateDateFormatter(installment.getDueOn()));
    }

    @Ignore
    @Test
    public void getNextInstallment_when_paid_is_min() {
        LoanDetail loanDetail = loanDetailBuilder().installments(getTest_getNextInstallment_MinPay()).build();
        PowerMockito.mockStatic(LocalDateTime.class);
        LocalDateTime localDateTimeMock = LocalDateTime.now().withDayOfMonth(15).plusDays(16);
        when(LocalDateTime.now()).thenReturn(localDateTimeMock);
        Installment installment = nextInstallmentsService.get(loanDetail);
        assertFalse(Objects.isNull(installment));
        assertTrue(installment.isInstallmentPaid());
        String dateNextInstallmentExpectedStr = getDateString(LocalDateTime.now().plusMonths(-1));
        String dateNextInstallmentStr = getDateString(LocalDateTime.parse(installment.getDueOn()));
        assertEquals(dateNextInstallmentExpectedStr, dateNextInstallmentStr);
        assertTrue(DUE_ON_FORMATTER_IS_RIGHT_MESSAGE, validateDateFormatter(installment.getDueOn()));
    }

    @Test
    public void getNextInstallment_when_paid_is_late() {
        LoanDetail loanDetail = loanDetailBuilder().installments(getTest_installments_Late()).build();
        Installment installment = nextInstallmentsService.get(loanDetail);
        assertFalse(Objects.isNull(installment));
        assertFalse(DISABLE_MINIMUM_IS_FALSE_MESSAGE, installment.isDisableMinimumPayment());
        assertFalse(INSTALLMENT_PAID_IS_FALSE_MESSAGE, installment.isInstallmentPaid());
        assertTrue(DUE_ON_FORMATTER_IS_RIGHT_MESSAGE, validateDateFormatter(installment.getDueOn()));
    }

    @Test
    public void getNextInstallment_when_paid_is_partial() {
        LoanDetail loanDetail =
                loanDetailBuilder()
                .installments(getTest_getNextInstallment_PartiallyPaid())
                .amountInstallment(TOTAL_DUE)
                .build();
        Installment installment = nextInstallmentsService.get(loanDetail);
        assertFalse(Objects.isNull(installment));
        assertFalse(DISABLE_MINIMUM_IS_FALSE_MESSAGE, installment.isDisableMinimumPayment());
        assertFalse(INSTALLMENT_PAID_IS_FALSE_MESSAGE, installment.isInstallmentPaid());
        LocalDateTime localDateTime = LocalDateTime.now().plusMonths(1);
        localDateTime = localDateTime.plusMonths(1);
        String dateNextInstallmentExpectedStr = getDateString(localDateTime);
        String dateNextInstallmentStr = getDateString(LocalDateTime.parse(installment.getDueOn()));
        assertEquals(dateNextInstallmentExpectedStr, dateNextInstallmentStr);
        assertTrue(DUE_ON_FORMATTER_IS_RIGHT_MESSAGE, validateDateFormatter(installment.getDueOn()));
        assertEquals(TOTAL_DUE_IS_OK_MESSAGE, TOTAL_DUE, installment.getAmount());
    }

    @Test
    public void getNextInstallment_is_paid_after_currentTime() {
        LoanDetail loanDetail = loanDetailBuilder().installments(getTest_getNextInstallment_Paid_after_current_time()).build();
        Installment installment = nextInstallmentsService.get(loanDetail);
        assertFalse(Objects.isNull(installment));
        assertTrue(installment.isInstallmentPaid());
        String dateNextInstallmentExpectedStr = getDateString(LocalDateTime.now().plusMonths(3));
        String dateNextInstallmentStr = getDateString(LocalDateTime.parse(installment.getDueOn()));
        assertEquals(dateNextInstallmentExpectedStr, dateNextInstallmentStr);
        assertTrue(DUE_ON_FORMATTER_IS_RIGHT_MESSAGE, validateDateFormatter(installment.getDueOn()));
    }

    private LoanDetail.LoanDetailBuilder loanDetailBuilder() {
        return LoanDetail.builder()
                .balance(BigDecimal.valueOf(10000))
                .accruedPenalty(ZERO)
                .penaltyBalance(ZERO)
                .accruedInterest(ZERO);
    }

    private InstallmentDetail getInstallmentDetail(String state, LocalDateTime localDateTime,
                                                   BigDecimal totalDue) {
        return InstallmentDetail.builder()
                .state(state)
                .dueDate(localDateTime)
                .lastPaidDate(localDateTime.plusMonths(1))
                .totalDue(totalDue)
                .build();
    }

    private List<InstallmentDetail> getTest_getNextInstallment_Partial_Paid() {
        LocalDateTime dateTest = LocalDateTime.now().plusDays(1);
        List<InstallmentDetail> installmentDetails = new ArrayList<>();
        installmentDetails.add(getInstallmentDetail(PAID.name(), dateTest.plusMonths(-1), ZERO));
        installmentDetails.add(getInstallmentDetail(PAID.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PARTIALLY_PAID.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        return installmentDetails;
    }

    private List<InstallmentDetail> getTest_getNextInstallment_Paid() {
        LocalDateTime dateTest = LocalDateTime.now().plusDays(1);
        List<InstallmentDetail> installmentDetails = new ArrayList<>();
        installmentDetails.add(getInstallmentDetail(PAID.name(), dateTest.plusMonths(-1), ZERO));
        installmentDetails.add(getInstallmentDetail(PAID.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        return installmentDetails;
    }

    private List<InstallmentDetail> getTest_getNextInstallment_Paid_same_months() {
        List<InstallmentDetail> installmentDetails = new ArrayList<>();
        installmentDetails.add(getInstallmentDetail(PAID.name(), LocalDateTime.now().withDayOfMonth(1), ZERO));
        installmentDetails.add(getInstallmentDetail(PENDING.name(), LocalDateTime.now().plusMonths(1), ZERO));
        installmentDetails.add(getInstallmentDetail(PENDING.name(), LocalDateTime.now().plusMonths(1), ZERO));
        installmentDetails.add(getInstallmentDetail(PENDING.name(), LocalDateTime.now().plusMonths(1), ZERO));
        return installmentDetails;
    }

    private List<InstallmentDetail> getTest_getNextInstallment_NotPaid() {
        List<InstallmentDetail> installmentDetails = new ArrayList<>();
        LocalDateTime dateTest = LocalDateTime.now().plusDays(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        return installmentDetails;
    }

    private List<InstallmentDetail> getTest_getNextInstallment_late() {
        List<InstallmentDetail> installmentDetails = new ArrayList<>();
        LocalDateTime dateTest = LocalDateTime.now().plusMonths(-1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        return installmentDetails;
    }

    private List<InstallmentDetail> getTest_getNextInstallment_MinPay() {
        LocalDateTime dateTest = LocalDateTime.now().withDayOfMonth(15);
        List<InstallmentDetail> installmentDetails = new ArrayList<>();
        installmentDetails.add(getInstallmentDetail(PAID.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PAID.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        return installmentDetails;
    }

    private List<InstallmentDetail> getTest_installments_Late() {
        LocalDateTime dateTest = LocalDateTime.now().plusMonths(-2);
        List<InstallmentDetail> installmentDetails = new ArrayList<>();
        installmentDetails.add(getInstallmentDetail(LATE.name(), dateTest, BigDecimal.valueOf(10d)));
        dateTest = dateTest.plusMonths(-1);
        installmentDetails.add(getInstallmentDetail(LATE.name(), dateTest, BigDecimal.valueOf(20d)));
        dateTest = LocalDateTime.now();
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, BigDecimal.valueOf(10d)));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, BigDecimal.valueOf(10d)));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, BigDecimal.valueOf(10d)));
        return installmentDetails;
    }

    private List<InstallmentDetail> getTest_getNextInstallment_PartiallyPaid() {
        List<InstallmentDetail> installmentDetails = new ArrayList<>();
        LocalDateTime dateTest = LocalDateTime.now().plusMonths(1);
        installmentDetails.add(getInstallmentDetail(LATE.name(), dateTest, INSTALMENT_DUE));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, INSTALMENT_DUE));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        return installmentDetails;
    }

    private List<InstallmentDetail> getTest_getNextInstallment_Paid_after_current_time() {
        List<InstallmentDetail> installmentDetails = new ArrayList<>();
        LocalDateTime dateTest = LocalDateTime.now().plusMonths(2);
        installmentDetails.add(getInstallmentDetail(PAID.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        dateTest = dateTest.plusMonths(1);
        installmentDetails.add(getInstallmentDetail(PENDING.name(), dateTest, ZERO));
        return installmentDetails;
    }

    private String getDateString(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return localDateTime.format(formatter);
    }

    private boolean validateDateFormatter(String localDate) {
        try {
            LocalDateTime.parse(localDate, TIMESTAMP_FORMAT);
            return TRUE;
        } catch (DateTimeParseException e) {
            return FALSE;
        }
    }
}
