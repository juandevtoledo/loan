package com.lulobank.credits.services.outboundadapters.repository;

import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.services.outboundadapters.model.LoanStatus;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@EnableScan
public interface CreditsRepository extends CrudRepository<CreditsEntity, UUID> {

    Optional<CreditsEntity> findByIdCredit(UUID idCredit);

    List<CreditsEntity> findByidClient(String idClient);

    List<CreditsEntity> findByidClientAndLoanStatusIsNotAndAcceptDateNotNull(String idClient, LoanStatus loanStatus);

}
