package at.technikum.springrestbackend.repositories;

import at.technikum.springrestbackend.entity.TicketEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends ListCrudRepository<TicketEntity, UUID> {
    List<TicketEntity> findAllByUserId(UUID userId);
}
