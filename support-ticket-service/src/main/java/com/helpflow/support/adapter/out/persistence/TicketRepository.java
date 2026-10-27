package com.helpflow.support.adapter.out.persistence;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface TicketRepository extends JpaRepository<TicketEntity,Long> {
 @EntityGraph(attributePaths={"requester","technician"}) Optional<TicketEntity> findWithRelationsById(Long id);
 List<TicketEntity> findByRequesterEmail(String email);
}
