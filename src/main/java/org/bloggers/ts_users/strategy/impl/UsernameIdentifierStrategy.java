package org.bloggers.ts_users.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.bloggers.ts_users.entities.UserProfile;
import org.bloggers.ts_users.repositories.UserProfileRepository;
import org.bloggers.ts_users.strategy.UserIdentifierStrategy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("USERNAME")
@RequiredArgsConstructor
class UsernameIdentifierStrategy implements UserIdentifierStrategy {

    private final UserProfileRepository userProfileRepository;

    @Override
    public Optional<UserProfile> find(String username) {
        return userProfileRepository.findByCredentialsUsername(username);
    }
}
