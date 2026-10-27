package com.helpflow.dispatch.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DispatchRequest(
        @NotNull @Positive Long ticketId,
        @NotBlank String categoria,
        @NotBlank String prioridad,
        String sede
) { }

