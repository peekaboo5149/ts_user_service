package org.bloggers.ts_users.strategy;

import org.bloggers.ts_users.entities.UserProfile;

import java.util.Optional;

@FunctionalInterface
public interface UserIdentifierStrategy {

    Optional<UserProfile> find(String value);

}
