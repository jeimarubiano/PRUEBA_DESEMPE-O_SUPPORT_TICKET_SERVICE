package com.helpflow.support.adapter.out.persistence;

import com.helpflow.support.domain.*;
import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name = "tickets")
public class TicketEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="requester_id") private UserEntity requester;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="technician_id") private TechnicianEntity technician;
    @Column(nullable=false, length=140) private String title;
    @Column(nullable=false, length=2000) private String description;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private TicketCategory category;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private TicketPriority priority;
    @Column(nullable=false) private String site;
    @Column(nullable=false) private String affectedAsset;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private TicketStatus status;
    @Column(nullable=false) private Instant createdAt;
    @Column(nullable=false) private Instant slaDueAt;
    @Column(nullable=false) private boolean slaEscalated;
    protected TicketEntity() { }
    public TicketEntity(UserEntity requester, String title, String description, TicketCategory category, TicketPriority priority, String site, String affectedAsset, Instant slaDueAt) {
        this.requester=requester; this.title=title; this.description=description; this.category=category; this.priority=priority; this.site=site; this.affectedAsset=affectedAsset;
        this.status=TicketStatus.ABIERTO; this.createdAt=Instant.now(); this.slaDueAt=slaDueAt;
    }
    public Long getId() { return id; } public TicketStatus getStatus() { return status; }
    public TicketCategory getCategory() { return category; } public TicketPriority getPriority() { return priority; }
    public Instant getSlaDueAt() { return slaDueAt; } public String getTitle() { return title; }
    public boolean isSlaEscalated() { return slaEscalated; }
    public UserEntity getRequester() { return requester; }
    public void changeStatus(TicketStatus next) {
        if (!canTransitionTo(next)) throw new IllegalStateException("Transición inválida: " + status + " -> " + next);
        this.status=next;
    }
    public void assign(TechnicianEntity technician) { this.technician=technician; this.status=TicketStatus.ASIGNADO; }
    public boolean canTransitionTo(TicketStatus next) {
        return switch (status) {
            case ABIERTO -> next == TicketStatus.ASIGNADO || next == TicketStatus.CANCELADO;
            case ASIGNADO -> next == TicketStatus.EN_PROGRESO || next == TicketStatus.CANCELADO;
            case EN_PROGRESO -> next == TicketStatus.RESUELTO || next == TicketStatus.CANCELADO;
            case RESUELTO -> next == TicketStatus.CERRADO;
            case CERRADO, CANCELADO -> false;
        };
    }
    public void markSlaEscalated() { this.slaEscalated = true; }
}
