package at.technikum.springrestbackend.repositories;

import at.technikum.springrestbackend.entity.TicketEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketRepository extends ListCrudRepository<TicketEntity, UUID> {
}
