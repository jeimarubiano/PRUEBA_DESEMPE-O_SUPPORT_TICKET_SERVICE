package com.helpflow.dispatch.domain;

import com.helpflow.dispatch.api.DispatchRequest;
import com.helpflow.dispatch.api.DispatchResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DispatchService {
    private static final List<String> TECHNICIANS = List.of(
            "TEC-001", "TEC-007", "TEC-014", "TEC-021", "TEC-032", "TEC-045"
    );

    public DispatchResponse dispatch(DispatchRequest request) {
        int seed = Math.abs((request.ticketId() + "|" + request.categoria().trim().toUpperCase()
                + "|" + request.prioridad().trim().toUpperCase()).hashCode());
        String technicianId = TECHNICIANS.get(seed % TECHNICIANS.size());
        int estimatedHours = seed % 13;
        String availability = estimatedHours <= 2 ? "ALTA"
                : estimatedHours <= 8 ? "MEDIA" : "BAJA";
        return new DispatchResponse(request.ticketId(), technicianId, availability, estimatedHours);
    }
}

