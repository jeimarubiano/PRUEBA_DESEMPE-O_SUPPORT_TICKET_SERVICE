package com.helpflow.support.application;

import com.helpflow.support.adapter.out.persistence.TechnicianRepository;
import com.helpflow.support.adapter.out.persistence.TicketEntity;
import com.helpflow.support.adapter.out.persistence.TicketHistoryEntity;
import com.helpflow.support.adapter.out.persistence.TicketHistoryRepository;
import com.helpflow.support.adapter.out.persistence.TicketRepository;
import com.helpflow.support.domain.PersonStatus;
import com.helpflow.support.domain.TicketStatus;
import com.helpflow.support.application.port.out.TechnicianDispatchPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TicketAssignmentService {
    private static final int MAX_ACTIVE_TICKETS = 5;
    private static final java.util.List<TicketStatus> ACTIVE_STATUSES = java.util.List.of(TicketStatus.ASIGNADO, TicketStatus.EN_PROGRESO);
    private final TicketRepository tickets;
    private final TechnicianRepository technicians;
    private final TicketHistoryRepository histories;
    private final TechnicianDispatchPort dispatch;
    public TicketAssignmentService(TicketRepository tickets, TechnicianRepository technicians, TicketHistoryRepository histories, TechnicianDispatchPort dispatch) {
        this.tickets = tickets; this.technicians = technicians; this.histories = histories; this.dispatch = dispatch;
    }
    @Transactional
    public TicketEntity assign(Long ticketId) {
        var ticket = tickets.findWithRelationsById(ticketId).orElseThrow(() -> new IllegalArgumentException("Ticket no existe"));
        return assignTicket(ticket);
    }
    @Transactional
    public TicketEntity assignCreatedTicket(TicketEntity ticket) {
        return assignTicket(ticket);
    }
    @Transactional
    public TicketEntity changeStatus(Long ticketId, TicketStatus next, String observation) {
        var ticket = tickets.findWithRelationsById(ticketId).orElseThrow(() -> new IllegalArgumentException("Ticket no existe"));
        var previous = ticket.getStatus();
        ticket.changeStatus(next);
        histories.save(new TicketHistoryEntity(ticket, previous, next, observation));
        return ticket;
    }
    private TicketEntity assignTicket(TicketEntity ticket) {
        var suggestion = dispatch.request(ticket.getId(), ticket.getCategory(), ticket.getPriority(), "N/A");
        var technician = technicians.findBySpecialtyAndStatus(ticket.getCategory(), PersonStatus.ACTIVO).stream()
                .filter(candidate -> tickets.countByTechnicianIdAndStatusIn(candidate.getId(), ACTIVE_STATUSES) < MAX_ACTIVE_TICKETS)
                .min(java.util.Comparator.comparingLong(candidate -> tickets.countByTechnicianIdAndStatusIn(candidate.getId(), ACTIVE_STATUSES)))
                .orElseThrow(() -> new IllegalStateException("No hay técnico activo con capacidad para la categoría " + ticket.getCategory()));
        var previous = ticket.getStatus();
        ticket.assign(technician);
        histories.save(new TicketHistoryEntity(ticket, previous, TicketStatus.ASIGNADO, "Asignación automática. Sugerencia externa: " + suggestion.technicianId() + ", disponibilidad " + suggestion.availability() + ", estimado " + suggestion.estimatedHours() + "h"));
        return ticket;
    }
}
