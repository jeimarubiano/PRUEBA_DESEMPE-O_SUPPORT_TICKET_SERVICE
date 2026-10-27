package com.helpflow.support.application.port.out;
import com.helpflow.support.domain.TicketCategory;
import com.helpflow.support.domain.TicketPriority;
public interface TechnicianDispatchPort {
    DispatchSuggestion request(Long ticketId, TicketCategory category, TicketPriority priority, String site);
    record DispatchSuggestion(String technicianId, String availability, int estimatedHours) { }
}
