package com.lulobank.credits.starter.v3.adapters.in.dto.movement;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MovementsResponse extends AdapterResponse {
    private final List<Movement> movements;
}
