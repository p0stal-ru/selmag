package ag.selm.customer.client;

import ag.selm.customer.client.exception.ClientBadRequestException;
import ag.selm.customer.client.payload.NewProductReviewPayload;
import ag.selm.customer.entity.ProductReview;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@RequiredArgsConstructor
public class WebClientProductReviewsClient implements ProductReviewsClient {

    private WebClient webClient;

    @Override
    public Flux<ProductReview> findProductReviewsByProductId(int productId) {
        return webClient
                .get()
                .uri("feedback-api/product-reviews/by-product-id/{productId}", productId)
                .retrieve()
                .bodyToFlux(ProductReview.class);
    }

    @Override
    public Mono<ProductReview> createProductReview(int productId, int rating, String review) {

        return webClient
                .post()
                .uri("feedback-api/product-reviews")
                .bodyValue(new NewProductReviewPayload(productId, rating, review))
                .retrieve()
                .bodyToMono(ProductReview.class)
                .onErrorMap(WebClientResponseException.BadRequest.class,
                        exception -> new ClientBadRequestException(exception,
                                ((List<String>) exception.getResponseBodyAs(ProblemDetail.class)
                                        .getProperties().get("errors"))));
    }
}
