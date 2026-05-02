package org.bloggers.ts_users.repositories;

import org.bloggers.ts_users.entities.Role;
import org.bloggers.ts_users.entities.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends MongoRepository<UserProfile, String> {

    Optional<UserProfile> findByCredentialsEmail(String email);

    Optional<UserProfile> findByCredentialsUsername(String username);

    Optional<UserProfile> findByIdAndIsActiveTrueAndIsDeletedFalse(String email);

    Page<UserProfile> findByIsActiveTrueAndIsDeletedFalse(Pageable pageable);

    Page<UserProfile> findByIsActiveTrueAndIsDeletedFalseAndRole(Role role, Pageable pageable);

}
