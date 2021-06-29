package com.lulobank.credits.sdk.dto.errorv3;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenericResponse {
    private ErrorResultV3 error;
}
