package de.seuhd.campuscoffee.api.dtos;

import lombok.Builder;
import org.jspecify.annotations.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

/**
 * DTO record for POS metadata.
 */
@Builder(toBuilder = true)
public record ReviewDto(
        @Nullable Long id
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
