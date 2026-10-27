package com.helpflow.support.application;

import com.helpflow.support.adapter.out.persistence.TechnicianEntity;
import com.helpflow.support.adapter.out.persistence.TechnicianRepository;
import com.helpflow.support.adapter.out.persistence.TicketHistoryRepository;
import com.helpflow.support.adapter.out.persistence.TicketRepository;
import com.helpflow.support.application.port.out.TechnicianDispatchPort;
import com.helpflow.support.domain.PersonStatus;
import com.helpflow.support.domain.TicketCategory;
import com.helpflow.support.domain.TicketPriority;
import com.helpflow.support.domain.TicketStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TicketAssignmentServiceTest {
    @Test
    void assignsAnActiveSpecialistWithAvailableCapacityAndRecordsHistory() {
        TicketRepository tickets = mock(TicketRepository.class);
        TechnicianRepository technicians = mock(TechnicianRepository.class);
        TicketHistoryRepository histories = mock(TicketHistoryRepository.class);
        TechnicianDispatchPort dispatch = mock(TechnicianDispatchPort.class);
        var service = new TicketAssignmentService(tickets, technicians, histories, dispatch);
        var ticket = mock(com.helpflow.support.adapter.out.persistence.TicketEntity.class);
        var technician = mock(TechnicianEntity.class);

        when(ticket.getId()).thenReturn(10L);
        when(ticket.getCategory()).thenReturn(TicketCategory.RED);
        when(ticket.getPriority()).thenReturn(TicketPriority.ALTA);
        when(ticket.getStatus()).thenReturn(TicketStatus.ABIERTO);
        when(technician.getId()).thenReturn(2L);
        when(technicians.findBySpecialtyAndStatus(TicketCategory.RED, PersonStatus.ACTIVO)).thenReturn(List.of(technician));
        when(tickets.countByTechnicianIdAndStatusIn(eq(2L), anyList())).thenReturn(1L);
        when(dispatch.request(10L, TicketCategory.RED, TicketPriority.ALTA, "N/A"))
                .thenReturn(new TechnicianDispatchPort.DispatchSuggestion("TEC-014", "MEDIA", 4));

        service.assignCreatedTicket(ticket);

        verify(dispatch).request(10L, TicketCategory.RED, TicketPriority.ALTA, "N/A");
        verify(ticket).assign(technician);
        verify(histories).save(any());
    }
}
