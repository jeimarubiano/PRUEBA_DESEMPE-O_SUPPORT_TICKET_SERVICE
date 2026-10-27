package com.helpflow.support.application;

import com.helpflow.support.adapter.out.persistence.TicketHistoryEntity;
import com.helpflow.support.adapter.out.persistence.TicketHistoryRepository;
import com.helpflow.support.adapter.out.persistence.TicketRepository;
import com.helpflow.support.domain.TicketStatus;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class SlaEscalationService {
    private static final List<TicketStatus> OPEN_STATUSES = List.of(TicketStatus.ABIERTO, TicketStatus.ASIGNADO, TicketStatus.EN_PROGRESO);
    private final TicketRepository tickets;
    private final TicketHistoryRepository histories;
    private final Counter overdueCounter;

    public SlaEscalationService(TicketRepository tickets, TicketHistoryRepository histories, MeterRegistry metrics) {
        this.tickets = tickets;
        this.histories = histories;
        this.overdueCounter = Counter.builder("helpflow.tickets.sla.overdue").description("Tickets escalados después de vencer su SLA").register(metrics);
    }

    @Scheduled(fixedDelayString = "${app.sla-monitoring-delay-ms:60000}")
    @Transactional
    public void escalateOverdueTickets() {
        tickets.findByStatusInAndSlaDueAtBeforeAndSlaEscalatedFalse(OPEN_STATUSES, Instant.now()).forEach(ticket -> {
            ticket.markSlaEscalated();
            histories.save(new TicketHistoryEntity(ticket, ticket.getStatus(), ticket.getStatus(), "Escalamiento automático: SLA vencido"));
            overdueCounter.increment();
        });
    }
}
