package com.lulobank.credits.sdk.dto.errorv3;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResultV3 {
    private String failure;
    private String code;
    private String detail;

    public ErrorResultV3(String failure, Integer value) {
        this.failure = failure;
        this.code = String.valueOf(value);
    }
}
