package com.lulobank.credits.services.port.inbound;

import com.lulobank.credits.sdk.dto.acceptofferv2.CreditWithOffer;
import io.vavr.control.Try;

public interface OtpService {

     Try<Boolean> validateOffer( CreditWithOffer creditWithOffer);

}
