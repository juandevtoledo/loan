package com.lulobank.credits.v3.port.in.promissorynote.dto;

import com.lulobank.credits.v3.dto.DocumentIdV3;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromissoryNoteRequest {
    private DocumentIdV3 documentId;
    private String name;
    private String lastName;
    private String email;

}
