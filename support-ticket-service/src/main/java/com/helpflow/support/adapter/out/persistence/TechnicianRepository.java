package com.helpflow.support.adapter.out.persistence;
import com.helpflow.support.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TechnicianRepository extends JpaRepository<TechnicianEntity,Long> { List<TechnicianEntity> findBySpecialtyAndStatus(TicketCategory specialty, PersonStatus status); }
