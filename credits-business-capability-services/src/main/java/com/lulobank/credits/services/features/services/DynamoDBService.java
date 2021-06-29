package com.lulobank.credits.services.features.services;
import com.lulobank.credits.services.domain.ClientDomain;
import com.lulobank.credits.services.inboundadapters.model.CreateLoanForClient;
import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.services.outboundadapters.repository.CreditsRepository;
import com.lulobank.credits.services.utils.MapperBuilder;

import java.util.UUID;

public class DynamoDBService {
    private CreditsRepository repository;
    public DynamoDBService(CreditsRepository repository){
        this.repository = repository;
    }
    public void persistProductsKeys(CreateLoanForClient createLoanClientRequest, ClientDomain clientDomain) {
        CreditsEntity creditsEntity = new CreditsEntity();
        creditsEntity.setIdCredit(UUID.fromString(createLoanClientRequest.getIdCredit()));
        creditsEntity.setIdClient(createLoanClientRequest.getIdClient());
        creditsEntity.setIdClientMambu(clientDomain.getIdClientMambu());
        creditsEntity.setEncodedKeyClientMambu(clientDomain.getCreateClientResponse().getAccount().getProductKey());
        creditsEntity.setIdLoanAccountMambu(clientDomain.getCreateLoanResponse().getId());
        creditsEntity.setEncodedKeyLoanAccountMambu(clientDomain.getCreateLoanResponse().getProductTypeKey());
        creditsEntity.setLoanConditionsList(MapperBuilder.buildLoanConditionsEntity(createLoanClientRequest.getLoanConditionsList()));
        repository.save(creditsEntity);
    }
}