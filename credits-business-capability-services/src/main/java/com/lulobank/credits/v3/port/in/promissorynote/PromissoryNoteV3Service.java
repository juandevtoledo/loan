package com.lulobank.credits.v3.port.in.promissorynote;

import java.util.Map;

import com.lulobank.credits.v3.port.in.promissorynote.dto.PromissoryNoteRequest;
import com.lulobank.credits.v3.port.in.promissorynote.dto.PromissoryNoteResponse;

import io.vavr.control.Try;

public interface PromissoryNoteV3Service {

    Try<PromissoryNoteResponse> sign(PromissoryNoteRequest promissoryNoteRequest, Map<String, String> auth);

}