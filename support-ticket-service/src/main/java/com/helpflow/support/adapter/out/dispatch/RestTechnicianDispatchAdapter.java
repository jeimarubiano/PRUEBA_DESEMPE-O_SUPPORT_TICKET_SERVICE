package com.helpflow.support.adapter.out.dispatch;

import com.helpflow.support.application.port.out.TechnicianDispatchPort;
import com.helpflow.support.domain.TicketCategory;
import com.helpflow.support.domain.TicketPriority;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RestTechnicianDispatchAdapter implements TechnicianDispatchPort {
    private final RestClient client;
    public RestTechnicianDispatchAdapter(RestClient.Builder builder, @Value("${dispatch.base-url:http://localhost:8081}") String baseUrl) { this.client=builder.baseUrl(baseUrl).build(); }
    public DispatchSuggestion request(Long ticketId, TicketCategory category, TicketPriority priority, String site) {
        Response response=client.post().uri("/technician-dispatch").contentType(MediaType.APPLICATION_JSON).body(new Request(ticketId,category.name(),priority.name(),site)).retrieve().body(Response.class);
        if(response==null) throw new IllegalStateException("Respuesta vacía del servicio de despacho");
        return new DispatchSuggestion(response.tecnicoId(),response.disponibilidad(),response.tiempoEstimadoHoras());
    }
    record Request(Long ticketId,String categoria,String prioridad,String sede){} record Response(Long ticketId,String tecnicoId,String disponibilidad,int tiempoEstimadoHoras){}
}
