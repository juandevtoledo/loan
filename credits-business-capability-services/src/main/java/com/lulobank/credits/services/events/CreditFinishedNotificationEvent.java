package com.lulobank.credits.services.events;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreditFinishedNotificationEvent {

    private String id;
    private String transactionType;
    private InAppNotification inAppNotification;
    private String description;
}
