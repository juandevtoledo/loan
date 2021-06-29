package com.lulobank.credits.services.features.payment;

import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import com.lulobank.core.utils.EventUtils;
import com.lulobank.credits.services.events.GoodStandingCertificateEvent;
import com.lulobank.credits.sdk.dto.payment.InstallmentPaid;
import com.lulobank.credits.sdk.dto.payment.PaymentInstallment;
import com.lulobank.credits.services.features.services.SQSMessageService;
import com.lulobank.credits.services.outboundadapters.sqs.SendMessageToReportingSQS;

import java.util.Objects;

import static com.lulobank.credits.services.utils.ConverterObjectUtil.createGoodStandingCertificateEvent;
import static java.lang.Boolean.TRUE;

public class SendMessageGoodStandingToSqs extends SendMessageToReportingSQS<PaymentInstallment> {

    public SendMessageGoodStandingToSqs(SQSMessageService sqsMessageService) {
        super(sqsMessageService);
    }

    @Override
    public Event buildEvent(Response response, PaymentInstallment command) {
        Event<GoodStandingCertificateEvent> event = null;
        if (TRUE.equals(command.getPaidInFull())) {
            InstallmentPaid installmentPaid = getInstallmentPaid(response);
            if (Objects.nonNull(installmentPaid)) {
                GoodStandingCertificateEvent goodStandingCertificateEvent = createGoodStandingCertificateEvent(installmentPaid);
                event = new EventUtils().getEvent(goodStandingCertificateEvent);
            }
        }
        return event;
    }
    private InstallmentPaid getInstallmentPaid(Response response){
        InstallmentPaid  installmentPaid=null;
        if (response.getContent() instanceof InstallmentPaid){
            installmentPaid = (InstallmentPaid) response.getContent();
        }
        return installmentPaid;
    }
}
