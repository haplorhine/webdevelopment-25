package at.technikum.springrestbackend.repositories;

import at.technikum.springrestbackend.entity.UserEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends ListCrudRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);
}

