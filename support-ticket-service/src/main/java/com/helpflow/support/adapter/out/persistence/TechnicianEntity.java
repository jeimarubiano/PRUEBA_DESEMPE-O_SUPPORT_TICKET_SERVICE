package com.helpflow.support.adapter.out.persistence;

import com.helpflow.support.domain.PersonStatus;
import com.helpflow.support.domain.TicketCategory;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name = "technicians")
public class TechnicianEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true) private String document;
    @Column(nullable=false) private String name;
    @Column(nullable=false, precision=12, scale=2) private BigDecimal salary;
    @Column(nullable=false) private LocalDate affiliationDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private PersonStatus status;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private TicketCategory specialty;
    protected TechnicianEntity() { }
    public TechnicianEntity(String document, String name, BigDecimal salary, LocalDate affiliationDate, PersonStatus status, TicketCategory specialty) { this.document=document;this.name=name;this.salary=salary;this.affiliationDate=affiliationDate;this.status=status;this.specialty=specialty; }
    public Long getId() { return id; } public PersonStatus getStatus() { return status; } public TicketCategory getSpecialty() { return specialty; }
}
