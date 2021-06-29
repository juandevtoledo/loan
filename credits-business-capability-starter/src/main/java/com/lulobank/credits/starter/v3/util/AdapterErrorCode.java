package com.lulobank.credits.starter.v3.util;

import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_100;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_101;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_102;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_103;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_104;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_106;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_107;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_108;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_109;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_110;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_116;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_117;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_118;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_119;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_120;
import static com.lulobank.credits.v3.vo.GeneralErrorStatus.GEN_001;


@Getter
@AllArgsConstructor
public enum AdapterErrorCode {
    BAD_REQUEST(List.of(GEN_001.name()), HttpStatus.BAD_REQUEST),
    NOT_FOUND(List.of(CRE_101.name()), HttpStatus.NOT_FOUND),
    NOT_ACCEPTABLE(List.of(CRE_102.name(), CRE_119.name(), CRE_120.name(), CRE_116.name(),CRE_106.name(),CRE_109.name() , CRE_107.name()), HttpStatus.NOT_ACCEPTABLE),
    INTERNAL_SERVER_ERROR(List.of(CRE_100.name()), HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_GATEWAY(List.of(CRE_103.name(), CRE_104.name(),  CRE_108.name(), CRE_110.name(), CRE_117.name(), CRE_118.name()), HttpStatus.BAD_GATEWAY);

    private final List<String> businessCodes;
    private final HttpStatus httpStatus;
}
