package ag.selm.customer.client.payload;


public record NewProductReviewPayload(int productId, int rating, String review) {

}
