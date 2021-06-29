package com.lulobank.credits.services.outboundadapters.flexibility;

import com.lulobank.credits.sdk.dto.loandetails.NextInstallment;
import com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.CbsLoanStateEnum;
import flexibility.client.models.response.GetLoanResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FlexibilityLoanResponseV3ServicesTest {

    private FlexibilityLoanServices testClass;

    private GetLoanResponse getLoanResponse;

    private Double amountInstallemtPendig;

    private Integer countInstallemtPaid;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        getLoanResponse = new GetLoanResponse();
        amountInstallemtPendig = 10002d;
        countInstallemtPaid = 2;
        List<GetLoanResponse.PaymentPlanItem> paymentPlanItemApiList = new ArrayList<>();
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-09-01T12:00:00",10000d, CbsLoanStateEnum.PAID.name()));
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-10-01T12:00:00",10001d, CbsLoanStateEnum.PAID.name()));
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-12-01T12:00:00",10003d, CbsLoanStateEnum.PENDING.name()));
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-11-15T13:00:00",amountInstallemtPendig, CbsLoanStateEnum.PENDING.name()));


        getLoanResponse.setPaymentPlanItemApiList(paymentPlanItemApiList);
        testClass= new FlexibilityLoanServices(getLoanResponse);

    }

    @Test
    public void should_Return_nextInstallment_Since_GetLoanResponse_contain_Installment_in_PENDING(){
        NextInstallment nextInstallment =  testClass.getNextInstallment();
        assertTrue(!Objects.isNull(nextInstallment));
        assertEquals(amountInstallemtPendig,nextInstallment.getAmount());
    }

    @Test
    public void should_Return_nextInstallment_Since_GetLoanResponse_contain_Installment_in_LATE(){
        //
        List<GetLoanResponse.PaymentPlanItem> paymentPlanItemApiList = new ArrayList<>();
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-09-01T12:00:00",10000d, CbsLoanStateEnum.PAID.name()));
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-10-01T12:00:00",10001d, CbsLoanStateEnum.PAID.name()));
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-12-01T12:00:00",10003d, CbsLoanStateEnum.PENDING.name()));
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-11-15T13:00:00",amountInstallemtPendig, CbsLoanStateEnum.LATE.name()));

        getLoanResponse.getPaymentPlanItemApiList().clear();
        getLoanResponse.setPaymentPlanItemApiList(paymentPlanItemApiList);
        //
        NextInstallment nextInstallment =  testClass.getNextInstallment();
        assertTrue(!Objects.isNull(nextInstallment));
        assertEquals(amountInstallemtPendig,nextInstallment.getAmount());
    }

    @Test
    public void should_return_nextInstallment_Since_GetLoanResponse_contain_Installment_in_PARTIAL_PAID(){
        //
        List<GetLoanResponse.PaymentPlanItem> paymentPlanItemApiList = new ArrayList<>();
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-09-01T12:00:00",10000d, CbsLoanStateEnum.PAID.name()));
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-10-01T12:00:00",10001d, CbsLoanStateEnum.PAID.name()));
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-12-01T12:00:00",10003d, CbsLoanStateEnum.PENDING.name()));
        paymentPlanItemApiList.add(this.initPaymentPlanItem("2019-11-15T13:00:00",amountInstallemtPendig, CbsLoanStateEnum.PARTIALLY_PAID.name()));

        getLoanResponse.getPaymentPlanItemApiList().clear();
        getLoanResponse.setPaymentPlanItemApiList(paymentPlanItemApiList);
        //
        NextInstallment nextInstallment =  testClass.getNextInstallment();
        assertTrue(!Objects.isNull(nextInstallment));
        assertEquals(amountInstallemtPendig,nextInstallment.getAmount());
    }

    @Test
    public void should_Return_Count_nextInstallment_Since_GetLoanResponse_contain_Installment_in_PAID(){
        Integer count =  testClass.countPaidInstallments();
        assertEquals(countInstallemtPaid,count);
    }

    private GetLoanResponse.PaymentPlanItem initPaymentPlanItem(String dateString,Double value,String state){
        LocalDateTime date = LocalDateTime.parse(dateString);
        GetLoanResponse.PaymentPlanItem  paymentPlanItem = new GetLoanResponse.PaymentPlanItem();

        paymentPlanItem.setState(state);
        paymentPlanItem.setTotalDue(value);
        paymentPlanItem.setDueDate(date);
        return paymentPlanItem;
    }
}
