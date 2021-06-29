package com.lulobank.credits.services.utils;

import com.lulobank.core.Response;
import com.lulobank.credits.services.events.GoodStandingCertificateEvent;
import com.lulobank.credits.services.events.YaTransferCreateWallet;
import com.lulobank.credits.sdk.dto.payment.InstallmentPaid;
import com.lulobank.credits.services.inboundadapters.model.CreateLoanClientResponse;
import com.lulobank.credits.services.inboundadapters.model.CreateLoanForClient;
import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.promissorynote.sdk.dto.CreatePromissoryNoteClientAndSign;
import com.lulobank.promissorynote.sdk.dto.DocumentId;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class ConverterObjectUtil {

    private static final String PREFIX = "57";
    private static final String GOOD_STANDING_REPORT_TYPE = "GOODSTANDINGCERTIFICATE";

    private ConverterObjectUtil(){}

    public static YaTransferCreateWallet createYaTransferCreateWallet(Response<CreateLoanClientResponse> loanClientResponseResponse, CreateLoanForClient loanClientRequest){
        YaTransferCreateWallet yaTransferCreateWallet = new YaTransferCreateWallet();
        yaTransferCreateWallet.setIdClient(loanClientRequest.getIdClient());
        yaTransferCreateWallet.setIdClientCBS(loanClientResponseResponse.getContent().getIdClient());
        yaTransferCreateWallet.setIdSavingAccount(loanClientResponseResponse.getContent().getSavingAccountNumber());
        yaTransferCreateWallet.setPhonePrefix(PREFIX);
        yaTransferCreateWallet.setPhoneNumber(loanClientRequest.getMobilePhone());
        return yaTransferCreateWallet;
    }

    public static CreatePromissoryNoteClientAndSign createPromissoryNoteClientAndSign(CreditsEntity creditsEntity) {
        CreatePromissoryNoteClientAndSign request = new CreatePromissoryNoteClientAndSign();
        if (Objects.nonNull(creditsEntity.getClientInformation())) {
            setDocumentId(creditsEntity, request);
            request.setName(creditsEntity.getClientInformation().getName());
            request.setLastName(creditsEntity.getClientInformation().getLastName());
            request.setEmail(creditsEntity.getClientInformation().getEmail());
        }
        return request;
    }

    private static void setDocumentId(CreditsEntity creditsEntity, CreatePromissoryNoteClientAndSign request) {
        if (Objects.nonNull(creditsEntity.getClientInformation().getDocumentId())) {
            request.setDocumentId(new DocumentId());
            request.getDocumentId().setId(creditsEntity.getClientInformation().getDocumentId().getId());
            request.getDocumentId().setType(creditsEntity.getClientInformation().getDocumentId().getType());
        }
    }

    public static GoodStandingCertificateEvent createGoodStandingCertificateEvent(CreditsEntity creditsEntity){
        GoodStandingCertificateEvent goodStandingCertificateEvent = new GoodStandingCertificateEvent();
        goodStandingCertificateEvent.setIdClient(creditsEntity.getIdClient());
        goodStandingCertificateEvent.setTypeReport(GOOD_STANDING_REPORT_TYPE);
        goodStandingCertificateEvent.setIdLoanAccountMambu(creditsEntity.getIdLoanAccountMambu());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if(Objects.nonNull(creditsEntity.getAcceptDate())) {
            goodStandingCertificateEvent.setAcceptDate(creditsEntity.getAcceptDate().format(dtf));
        }
        if(Objects.nonNull(creditsEntity.getClosedDate())){
            goodStandingCertificateEvent.setClosedDate(creditsEntity.getClosedDate().format(dtf));
        }
        //TODO: eliminar este converte, hace parte de un servicio deprectado
        if(Objects.nonNull(creditsEntity.getAcceptOffer())) {
            goodStandingCertificateEvent.setAmount(BigDecimal.valueOf(creditsEntity.getAcceptOffer().getAmount()));
        }
        return goodStandingCertificateEvent;
    }
    public static GoodStandingCertificateEvent createGoodStandingCertificateEvent(InstallmentPaid installmentPaid){
        GoodStandingCertificateEvent goodStandingCertificateEvent = new GoodStandingCertificateEvent();
        goodStandingCertificateEvent.setIdClient(installmentPaid.getIdClient());
        goodStandingCertificateEvent.setTypeReport(GOOD_STANDING_REPORT_TYPE);
        goodStandingCertificateEvent.setIdLoanAccountMambu(installmentPaid.getIdLoan());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (Objects.nonNull(installmentPaid.getAcceptDate())) {
            goodStandingCertificateEvent.setAcceptDate(installmentPaid.getAcceptDate().format(dtf));
        }
        if (Objects.nonNull(installmentPaid.getClosedDate())) {
            goodStandingCertificateEvent.setClosedDate(installmentPaid.getClosedDate().format(dtf));
        }
        Optional.ofNullable(installmentPaid.getAmountOffer()).ifPresent(goodStandingCertificateEvent::setAmount);
        return goodStandingCertificateEvent;
    }
}
