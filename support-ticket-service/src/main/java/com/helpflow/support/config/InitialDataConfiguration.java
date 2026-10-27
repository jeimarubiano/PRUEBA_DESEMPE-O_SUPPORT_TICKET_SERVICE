package com.helpflow.support.config;

import com.helpflow.support.adapter.out.persistence.TechnicianEntity;
import com.helpflow.support.adapter.out.persistence.TechnicianRepository;
import com.helpflow.support.adapter.out.persistence.UserEntity;
import com.helpflow.support.adapter.out.persistence.UserRepository;
import com.helpflow.support.domain.PersonStatus;
import com.helpflow.support.domain.TicketCategory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class InitialDataConfiguration {
    @Bean CommandLineRunner initialData(UserRepository users, TechnicianRepository technicians, PasswordEncoder encoder, @Value("${app.bootstrap-admin-password:}") String bootstrapPassword) {
        return args -> {
            if (!bootstrapPassword.isBlank() && !users.existsByEmail("admin@helpflow.com")) users.save(new UserEntity("ADMIN-001", "Administrador HelpFlow", "admin@helpflow.com", "Bogota", "TI", PersonStatus.ACTIVO, encoder.encode(bootstrapPassword), "ROLE_ADMIN"));
            if (technicians.count() == 0) {
                for (TicketCategory category : TicketCategory.values()) technicians.save(new TechnicianEntity("TEC-" + category.ordinal(), "Técnico " + category.name(), new BigDecimal("3000000"), LocalDate.of(2024, 1, 1), PersonStatus.ACTIVO, category));
            }
        };
    }
}
