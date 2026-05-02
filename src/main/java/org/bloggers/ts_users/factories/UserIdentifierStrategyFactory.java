package org.bloggers.ts_users.factories;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.bloggers.ts_users.dto.request.IdentifierType;
import org.bloggers.ts_users.exceptions.BadRequestException;
import org.bloggers.ts_users.strategy.UserIdentifierStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserIdentifierStrategyFactory {

    private final Map<String, UserIdentifierStrategy> strategies;

    public UserIdentifierStrategy getStrategy(@NotNull IdentifierType type) {
        UserIdentifierStrategy strategy = strategies.get(type.name().toUpperCase());

        if (strategy == null) {
            throw new BadRequestException("Invalid identifier type provided.");
        }

        return strategy;
    }
}
