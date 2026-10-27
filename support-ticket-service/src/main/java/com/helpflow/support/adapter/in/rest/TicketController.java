package com.helpflow.support.adapter.in.rest;

import com.helpflow.support.adapter.out.persistence.*;
import com.helpflow.support.domain.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import com.helpflow.support.application.TicketAssignmentService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@RestController @RequestMapping("/tickets")
public class TicketController {
 private final TicketRepository tickets; private final UserRepository users; private final TicketAssignmentService assignment; private final MeterRegistry metrics;
 public TicketController(TicketRepository tickets, UserRepository users, TicketAssignmentService assignment, MeterRegistry metrics) { this.tickets=tickets; this.users=users; this.assignment=assignment; this.metrics=metrics; }
 @PostMapping @ResponseStatus(HttpStatus.CREATED) @Transactional
 public TicketResponse create(@Valid @RequestBody CreateTicketRequest r, Authentication auth) {
   var requester=users.findByEmail(auth.getName()).filter(u->u.getStatus()==PersonStatus.ACTIVO).orElseThrow(()->new IllegalStateException("Usuario inactivo"));
   var t=tickets.save(new TicketEntity(requester,r.title(),r.description(),r.category(),r.priority(),r.site(),r.affectedAsset(),Instant.now().plus(sla(r.priority()))));
   Counter.builder("helpflow.tickets.created").tag("category",r.category().name()).tag("priority",r.priority().name()).register(metrics).increment(); return TicketResponse.of(t);
 }
 @PostMapping("/{id}/assign") public TicketResponse assign(@PathVariable Long id,Authentication auth) { if(auth.getAuthorities().stream().noneMatch(a->a.getAuthority().equals("ROLE_ADMIN"))) throw new SecurityException("Solo un administrador puede asignar"); return TicketResponse.of(assignment.assign(id)); }
 @GetMapping public List<TicketResponse> mine(Authentication auth) { return tickets.findByRequesterEmail(auth.getName()).stream().map(TicketResponse::of).toList(); }
 @PatchMapping("/{id}/status") @Transactional
 public TicketResponse changeStatus(@PathVariable Long id,@Valid @RequestBody ChangeStatusRequest r,Authentication auth) {
   var t=tickets.findWithRelationsById(id).orElseThrow(()->new IllegalArgumentException("Ticket no existe"));
   if(!t.getRequester().getEmail().equals(auth.getName()) && !auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN")||a.getAuthority().equals("ROLE_TECNICO"))) throw new SecurityException("No autorizado");
   t.changeStatus(r.status()); return TicketResponse.of(t);
 }
 private Duration sla(TicketPriority p) { return switch(p) { case BAJA->Duration.ofHours(72); case MEDIA->Duration.ofHours(24); case ALTA->Duration.ofHours(8); case CRITICA->Duration.ofHours(2); }; }
 public record CreateTicketRequest(@NotBlank String title,@NotBlank String description,@NotNull TicketCategory category,@NotNull TicketPriority priority,@NotBlank String site,@NotBlank String affectedAsset) {}
 public record ChangeStatusRequest(@NotNull TicketStatus status,@NotBlank String observation) {}
 public record TicketResponse(Long id,String title,TicketCategory category,TicketPriority priority,TicketStatus status,Instant slaDueAt) { static TicketResponse of(TicketEntity t){return new TicketResponse(t.getId(),t.getTitle(),t.getCategory(),t.getPriority(),t.getStatus(),t.getSlaDueAt());} }
}
