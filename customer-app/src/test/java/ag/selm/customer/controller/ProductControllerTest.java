package ag.selm.customer.controller;


import ag.selm.customer.client.FavouriteProductsClient;
import ag.selm.customer.client.ProductReviewsClient;
import ag.selm.customer.client.ProductsClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductsClient productsClient;

    @Mock
    FavouriteProductsClient favouriteProductsClient;

    @Mock
    ProductReviewsClient productReviewsClient;

    @InjectMocks
    ProductController productController;

    @Test
    @DisplayName("Элемент NoSuchElementException должно быть транслировано в страницу error/404")
    void handleNoSuchElementException_ReturnsErrors404() {
        //  given
        var exception = new NoSuchElementException("Товар не найден");
        var model = new ConcurrentModel();

        //  when
        var result = productController.handleNoSuchElementException(exception, model);

        //  then
        assertEquals("errors/404", result);
        assertEquals("Товар не найден", model.getAttribute("error"));



    }
}