package ag.selm.customer.entity;

import java.util.UUID;

public record ProductReview(UUID id, int rating, String review) {
}
