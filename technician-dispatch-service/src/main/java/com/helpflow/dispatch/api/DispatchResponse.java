package com.helpflow.dispatch.api;

public record DispatchResponse(
        Long ticketId,
        String tecnicoId,
        String disponibilidad,
        int tiempoEstimadoHoras
) { }

