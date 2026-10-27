package com.helpflow.support.domain;

import com.helpflow.support.adapter.out.persistence.TicketEntity;
import com.helpflow.support.adapter.out.persistence.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TicketStatusTransitionTest {
    private TicketEntity ticket() {
        var user = new UserEntity("100", "Ana", "ana@test.com", "Bogota", "TI", PersonStatus.ACTIVO, "hash", "ROLE_USUARIO");
        return new TicketEntity(user, "Red caída", "No hay red", TicketCategory.RED, TicketPriority.ALTA, "Bogota", "PC-1", Instant.now().plusSeconds(3600));
    }

    @Test void onlyAllowsDefinedLifecycleTransitions() {
        var ticket = ticket();
        assertTrue(ticket.canTransitionTo(TicketStatus.ASIGNADO));
        assertFalse(ticket.canTransitionTo(TicketStatus.RESUELTO));
        assertThrows(IllegalStateException.class, () -> ticket.changeStatus(TicketStatus.RESUELTO));
    }
}
