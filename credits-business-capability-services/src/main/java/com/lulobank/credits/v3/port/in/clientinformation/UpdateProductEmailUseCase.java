package com.lulobank.credits.v3.port.in.clientinformation;

import static com.lulobank.credits.services.utils.LogMessages.CLIENT_EMAIL_UPDATED;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.CbsLoanStateEnum;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.util.UseCase;

import io.vavr.control.Try;
import lombok.CustomLog;

@CustomLog
public class UpdateProductEmailUseCase implements UseCase<UpdateProductEmailMessage,Try<Void>> {

    private final CreditsV3Repository creditsRepository;

    public UpdateProductEmailUseCase(CreditsV3Repository creditsRepository) {
        this.creditsRepository = creditsRepository;
    }

    @Override
    public Try<Void> execute(UpdateProductEmailMessage payload) {
        return Try.run(() -> {
            creditsRepository.findByIdClient(payload.getIdClient())
                    .filter(validateFields)
                    .filter(credit -> isNewEmailNew.test(credit, payload.getNewEmail()))
                    .filter(creditIsNotClosed)
                    .peek(credit -> credit.getClientInformation()
                            .setEmail(payload.getNewEmail()))
                    .peek(creditsRepository::save)
                    .peek(credit -> log.info(CLIENT_EMAIL_UPDATED.getMessage(), credit.getIdCredit()));
        }).onFailure(error -> log.error("Error updating email, idClient : {}, error : {} ", payload.getIdClient(), error.getMessage(), error));
    }

    private final Predicate<CreditsV3Entity> validateFields = credit -> Objects.nonNull(credit.getClientInformation()) && Objects.nonNull(credit.getIdLoanAccountMambu());
    private final BiPredicate<CreditsV3Entity, String> isNewEmailNew = (credit, newEmail) -> Boolean.FALSE.equals(credit.getClientInformation().getEmail().equalsIgnoreCase(newEmail));
    private final Predicate<CreditsV3Entity> creditIsNotClosed = credit -> Objects.isNull(credit.getLoanStatus()) || Boolean.FALSE.equals(CbsLoanStateEnum.CLOSED.name().equals(credit.getLoanStatus().getStatus()));
}
