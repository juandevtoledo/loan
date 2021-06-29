package com.lulobank.credits.starter.v3.adapters.out.dynamo;

import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.starter.v3.adapters.out.dynamo.dto.CreditsDto;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@EnableScan
public interface CreditsDataRepository extends CrudRepository<CreditsEntity, UUID> {

    List<CreditsEntity> findByIdClient(String idClient);

    CreditsDto save(CreditsDto creditsDto);

    List<CreditsEntity> findByStatementsIndex(String statementsIndex);
}
