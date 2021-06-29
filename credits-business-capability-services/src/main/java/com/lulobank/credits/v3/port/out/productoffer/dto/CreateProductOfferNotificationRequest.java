package com.lulobank.credits.v3.port.out.productoffer.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateProductOfferNotificationRequest {
    private String idClient;
    private String type;
    private Integer value;
}
