package com.lulobank.credits.v3.port.in.promissorynote;

import com.lulobank.credits.v3.port.in.promissorynote.dto.SignPromissoryNoteResponse;
import com.lulobank.credits.v3.usecase.command.AcceptOffer;
import io.vavr.control.Try;

import java.util.Map;

public interface ValidForPromissoryNoteSing {

    Try<SignPromissoryNoteResponse> execute(AcceptOffer acceptOffer,Map<String, String> auth);

}
