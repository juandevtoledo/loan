package com.lulobank.credits.services.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewReportEvent {
    private String idClient;
    private String typeReport;
    private String idProduct;

}
