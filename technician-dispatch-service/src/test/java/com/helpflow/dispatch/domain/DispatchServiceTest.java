package com.helpflow.dispatch.domain;

import com.helpflow.dispatch.api.DispatchRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DispatchServiceTest {
    private final DispatchService service = new DispatchService();

    @Test
    void returnsSameRecommendationForSameCombination() {
        var request = new DispatchRequest(125L, "RED", "ALTA", "BOGOTA");

        assertThat(service.dispatch(request)).isEqualTo(service.dispatch(request));
    }
}

