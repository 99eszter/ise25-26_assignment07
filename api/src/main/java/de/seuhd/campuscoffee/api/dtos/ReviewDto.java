package de.seuhd.campuscoffee.api.dtos;

import lombok.Builder;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/**
 * DTO record for POS metadata.
 */
@Builder(toBuilder = true)
public record ReviewDto(
        @Nullable Long id,
        @Nullable Instant createdAt,
        @Nullable Instant updatedAt,
        @NotNull Long posId,
        @NotNull Long authorId,
        @NotBlank String review,
        @Nullable Boolean approved
) implements Dto<Long> {
    @Override
    public @Nullable Long getId() {
        return id;
    }
}
