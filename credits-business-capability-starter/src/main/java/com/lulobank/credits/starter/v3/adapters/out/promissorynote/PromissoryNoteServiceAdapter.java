package com.lulobank.credits.starter.v3.adapters.out.promissorynote;

import com.lulobank.credits.starter.v3.mappers.PromissoryNoteMapper;
import com.lulobank.credits.v3.port.in.promissorynote.PromissoryNoteV3Service;
import com.lulobank.credits.v3.port.in.promissorynote.dto.PromissoryNoteRequest;
import com.lulobank.credits.v3.port.in.promissorynote.dto.PromissoryNoteResponse;
import com.lulobank.promissorynote.sdk.operations.IPromissoryNote;
import io.vavr.control.Try;
import lombok.CustomLog;
import okhttp3.ResponseBody;

import java.util.Map;

@CustomLog
public class PromissoryNoteServiceAdapter implements PromissoryNoteV3Service {

    private final IPromissoryNote promissoryNote;

    public PromissoryNoteServiceAdapter(IPromissoryNote promissoryNote) {
        this.promissoryNote = promissoryNote;
    }

    @Override
    public Try<PromissoryNoteResponse> sign(
            PromissoryNoteRequest promissoryNoteRequest, Map<String, String> auth) {

        return promissoryNote.createPromissoryNote(auth, PromissoryNoteMapper.INSTANCE.toCreatePromissoryNoteClientAndSign(promissoryNoteRequest))
                .peekLeft(errorBody -> messageError(errorBody, promissoryNoteRequest))
                .map(PromissoryNoteMapper.INSTANCE::toCreatePromissoryNoteClientAndSignResponse)
                .toTry();

    }


    private void messageError(ResponseBody errorBody, PromissoryNoteRequest promissoryNoteRequest) {
        Try.of(errorBody::string)
                .peek(errorMsg -> log.error(String.format("Error in promissoryNoteService , documentId : %1$s, msg : %2$s",promissoryNoteRequest.getDocumentId().getId(),  errorMsg)))
                .onFailure(exception -> log.error(String.format("Error getting promissoryNoteService' errorBody , msg: %s ", exception.getMessage()), exception));
    }
}
