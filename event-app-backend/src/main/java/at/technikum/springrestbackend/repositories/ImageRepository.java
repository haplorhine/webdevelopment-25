package at.technikum.springrestbackend.repositories;

import at.technikum.springrestbackend.entity.ImageEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ImageRepository extends ListCrudRepository<ImageEntity, UUID> {
}