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
        dispatch.request(ticket.getId(), ticket.getCategory(), ticket.getPriority(), "N/A");
        var technician = technicians.findBySpecialtyAndStatus(ticket.getCategory(), PersonStatus.ACTIVO).stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay técnico disponible para la categoría"));
        var previous = ticket.getStatus();
        ticket.assign(technician);
        histories.save(new TicketHistoryEntity(ticket, previous, TicketStatus.ASIGNADO, "Asignación inicial automática"));
        return ticket;
    }
}
