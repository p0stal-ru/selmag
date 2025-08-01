package ag.selm.feedback.controller.payload;

public record NewProductReviewPayload(
        Integer productId,
        Integer rating,
        String review) {
}
