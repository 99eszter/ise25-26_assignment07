package de.seuhd.campuscoffee.api.controller;

import de.seuhd.campuscoffee.api.dtos.ReviewDto;
import de.seuhd.campuscoffee.api.mapper.DtoMapper;
import de.seuhd.campuscoffee.api.openapi.CrudOperation;
import de.seuhd.campuscoffee.domain.model.objects.Review;
import de.seuhd.campuscoffee.domain.ports.api.CrudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static de.seuhd.campuscoffee.api.openapi.Operation.*;
import static de.seuhd.campuscoffee.api.openapi.Resource.REVIEW;

/**
 * Controller for handling reviews for POS, authored by users.
 */
@Tag(name="Reviews", description="Operations for managing reviews for points of sale.")
@Controller
@RequestMapping("/api/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController extends CrudController<Review, ReviewDto, Long> {

    private final @NonNull CrudService<Review, Long> reviewService;
    private final @NonNull DtoMapper<Review, ReviewDto> reviewMapper;

    @Override
    protected @NonNull CrudService<Review, Long> service() {
        return reviewService;
    }

    @Override
    protected @NonNull DtoMapper<Review, ReviewDto> mapper() {
        return reviewMapper;
    }

    @Operation
    @CrudOperation(operation=GET_ALL, resource=REVIEW)
    @GetMapping("")
    public @NonNull ResponseEntity<List<ReviewDto>> getAll() {
        return super.getAll();
    }

    @Operation
    @GetMapping("/{id}")
    public @NonNull ResponseEntity<ReviewDto> getById(@PathVariable Long id) {
        return super.getById(id);
    }

    @Operation
    @PostMapping("")
    public @NonNull ResponseEntity<ReviewDto> create(@RequestBody @NonNull ReviewDto dto) {
        return super.create(dto);
    }

    @Operation
    @PutMapping("/{id}")
    public @NonNull ResponseEntity<ReviewDto> update(@PathVariable Long id, @RequestBody @NonNull ReviewDto dto) {
        try {
            // ensure id from path is applied to payload
            dto.getClass().getMethod("setId", Long.class).invoke(dto, id);
        } catch (Exception ignored) {
            // if setter not present, assume DTO already contains id
        }
        return super.update(dto);
    }

    @Operation
    @DeleteMapping("/{id}")
    public @NonNull ResponseEntity<Void> delete(@PathVariable Long id) {
        return super.delete(id);
    }

    @Operation
    @GetMapping("/filter")
    public ResponseEntity<List<ReviewDto>> filter(
            @RequestParam("pos_id") Long posId,
            @RequestParam("approved") Boolean approved
    ) {
        ResponseEntity<List<ReviewDto>> allResponse = super.getAll();
        List<ReviewDto> body = allResponse.getBody();
        if (body == null) {
            return ResponseEntity.ok(List.of());
        }
        List<ReviewDto> filtered = body.stream()
                .filter(dto -> {
                    try {
                        Object dtoPos = dto.getClass().getMethod("getPosId").invoke(dto);
                        Object dtoApproved = dto.getClass().getMethod("getApproved").invoke(dto);
                        return posId.equals(dtoPos) && approved.equals(dtoApproved);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toList();
        return ResponseEntity.ok(filtered);
    }

    @Operation
    @PostMapping("/{id}/approve")
    public ResponseEntity<ReviewDto> approve(
            @PathVariable Long id,
            @RequestParam("user_id") Long userId
    ) {
        ResponseEntity<ReviewDto> existing = super.getById(id);
        ReviewDto dto = existing.getBody();
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            // try to set approved flag and approver id if setters exist
            try {
                dto.getClass().getMethod("setApproved", Boolean.class).invoke(dto, true);
            } catch (NoSuchMethodException ignored) {
            }
            try {
                dto.getClass().getMethod("setApprovedBy", Long.class).invoke(dto, userId);
            } catch (NoSuchMethodException ignored) {
            }
        } catch (Exception e) {
            // ignore reflection issues
        }
        return super.update(dto);
    }
}
