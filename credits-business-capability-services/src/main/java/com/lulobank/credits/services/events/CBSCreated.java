package com.lulobank.credits.services.events;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
//TODO: el refactor para cambiar el nombre de esta clase afecta el servicio de clientes
public class CBSCreated {
    private String idClient;
    private String idCbs;
    private String idCbsHash;
}