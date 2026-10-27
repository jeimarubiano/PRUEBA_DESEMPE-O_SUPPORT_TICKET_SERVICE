package com.helpflow.support.adapter.out.persistence;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.time.Instant;
import com.helpflow.support.domain.TicketStatus;
public interface TicketRepository extends JpaRepository<TicketEntity,Long> {
 @EntityGraph(attributePaths={"requester","technician"}) Optional<TicketEntity> findWithRelationsById(Long id);
 List<TicketEntity> findByRequesterEmail(String email);
 long countByTechnicianIdAndStatusIn(Long technicianId, List<TicketStatus> statuses);
 List<TicketEntity> findByStatusInAndSlaDueAtBeforeAndSlaEscalatedFalse(List<TicketStatus> statuses, Instant instant);
}
