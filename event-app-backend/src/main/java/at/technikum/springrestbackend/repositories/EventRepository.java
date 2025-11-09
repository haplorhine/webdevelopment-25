package at.technikum.springrestbackend.repositories;


import at.technikum.springrestbackend.entity.EventEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventRepository extends ListCrudRepository<EventEntity, UUID> {

}
