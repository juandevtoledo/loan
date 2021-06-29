package com.lulobank.credits.services.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InAppNotification {
    private String idClient;
    private String tittle;
    private String description;
    private String dateNotification;
    private String action;
}
