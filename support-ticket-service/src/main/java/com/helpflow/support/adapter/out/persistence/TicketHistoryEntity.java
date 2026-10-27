package com.helpflow.support.adapter.out.persistence;

import com.helpflow.support.domain.TicketStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name = "ticket_histories")
public class TicketHistoryEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="ticket_id") private TicketEntity ticket;
    @Enumerated(EnumType.STRING) private TicketStatus previousStatus;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private TicketStatus newStatus;
    @Column(nullable=false, length=1000) private String observation;
    @Column(nullable=false) private Instant changedAt;
    protected TicketHistoryEntity() { }
}
