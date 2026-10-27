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
    protected TicketEntity() { }
}
