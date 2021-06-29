package com.lulobank.credits.sdk.dto.acceptoffer;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditWithOffer implements Command {
    private String idCredit;
    private String idOffer;
}
