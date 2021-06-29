package com.lulobank.credits.v3.port.out.queue;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.service.LoanTransaction;

import java.util.Map;

public interface NotificationV3Service {

    void loanCreatedNotification(LoanTransaction event);

    void requestDigitalEvidence(LoanTransaction event, Map<String, Object> auth);

    void riskEngineResultEventV2Notification(CreditsV3Entity event);
}
