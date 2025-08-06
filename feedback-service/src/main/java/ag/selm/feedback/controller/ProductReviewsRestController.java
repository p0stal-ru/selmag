package ag.selm.feedback.controller;

import ag.selm.feedback.controller.payload.NewProductReviewPayload;
import ag.selm.feedback.entity.ProductReview;
import ag.selm.feedback.service.ProductReviewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("feedback-api/product-reviews")
@RequiredArgsConstructor
@Slf4j
public class ProductReviewsRestController {

    private final ProductReviewsService productReviewsService;

//    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @GetMapping("by-productId={productId}")//     ?productId={productId} / by-productId={productId}
    public Flux<ProductReview> findProductReviewsByProductId(@PathVariable("productId") int productId) {
        return productReviewsService.findProductReviewsByProduct(productId);
//        return reactiveMongoTemplate.find(Query.query(Criteria.where("productId").is(productId)), ProductReview.class);
    }

    @PostMapping
    public Mono<ResponseEntity<ProductReview>> createProductReview(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
           @Valid @RequestBody Mono<NewProductReviewPayload> payloadMono,
           UriComponentsBuilder uriComponentsBuilder) {
        return authenticationTokenMono
                .flatMap(authToken -> payloadMono
                .flatMap(payload -> productReviewsService.createProductReview(payload.productId(),
                        payload.rating(), payload.review(), authToken.getToken().getSubject())))
                .map(productReview -> ResponseEntity
                        .created(uriComponentsBuilder.replacePath("/feedback-api/product-reviews/{id}")
                                .build(productReview.getId()))
                        .body(productReview));
    }


}
