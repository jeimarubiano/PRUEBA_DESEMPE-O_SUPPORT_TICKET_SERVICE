package com.helpflow.support.adapter.out.persistence;

import com.helpflow.support.domain.PersonStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 30) private String document;
    @Column(nullable = false, length = 120) private String name;
    @Column(nullable = false, unique = true, length = 150) private String email;
    @Column(nullable = false, length = 80) private String site;
    @Column(nullable = false, length = 80) private String area;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 15) private PersonStatus status;
    @Column(nullable = false) private String passwordHash;
    @Column(nullable = false, length = 20) private String role;

    protected UserEntity() { }
    public UserEntity(String document, String name, String email, String site, String area, PersonStatus status, String passwordHash, String role) {
        this.document = document; this.name = name; this.email = email; this.site = site; this.area = area;
        this.status = status; this.passwordHash = passwordHash; this.role = role;
    }
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public PersonStatus getStatus() { return status; }
    public String getRole() { return role; }
    public String getPasswordHash() { return passwordHash; }
}
