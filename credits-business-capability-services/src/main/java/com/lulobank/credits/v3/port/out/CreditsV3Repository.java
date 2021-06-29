package com.lulobank.credits.v3.port.out;

import com.lulobank.credits.v3.dto.OfferEntityV3;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.util.UUID;

public interface CreditsV3Repository {

    Option<CreditsV3Entity> findById(String idCredit);

    Option<CreditsV3Entity> findClientByOffer(
            UUID idCredit, String idClient);

    List<CreditsV3Entity> findByIdClient(String idClient);

    Option<OfferEntityV3> findOfferEntityV3ByIdClient(String idClient, UUID idCredit, String idOffer);

    Try<CreditsV3Entity> save(CreditsV3Entity creditsV3Entity);

    List<CreditsV3Entity> findByStatementsIndex(String statementsIndex);

    Option<CreditsV3Entity> findByIdCreditAndIdLoanAccountMambu(String idCredit, String idCreditCBS);

    Option<CreditsV3Entity> findByIdProductOffer(String idProductOffer);

    List<CreditsV3Entity> findByidClientAndIdLoanAccountMambuNotNull(String idClient);

    Option<CreditsV3Entity> findByIdLoanAccountMambu(String idLoanAccountMambu);

    Option<CreditsV3Entity> findLoanActiveByIdClient(String idClient);
}
