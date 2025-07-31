package ag.selm.customer.controller;

import ag.selm.customer.client.ProductsClient;
import ag.selm.customer.entity.FavouriteProduct;
import ag.selm.customer.service.FavouriteProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products")
public class ProductsController {

    private final ProductsClient productsClient;

    private final FavouriteProductsService favouriteProductsService;

    @GetMapping("list")
    public Mono<String> getProductsListPage(Model model,
                                            @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("filter", filter);
        return this.productsClient.findAllProducts(filter)
                .collectList()
//                .collect(Collectors.toList())
                .doOnNext(products -> model.addAttribute( "products", products))
                .thenReturn("customer/products/list");
    }

    @GetMapping("favourites")
    public Mono<String> getFavouriteProductsPage(Model model,
                                                 @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("filter", filter);
        return this.favouriteProductsService.findFavouriteProducts()    // Сначала пы получаем список избранных товаров, которые у на есть
                .map(FavouriteProduct::getProductId)// Преобразуем в список индентификаторов товаров
                .collectList()// flux собираем в список Mono, затем с помощью flatmap объединяем текущий стрим с новым стримом,
                // который возвращает на список товаров из каталога
                .flatMap(favouriteProducts -> this.productsClient.findAllProducts(filter)
                        .filter(product -> favouriteProducts.contains(product.id())) // фильтруем, проверяя, что товар добавлен в список избранного
                        .collectList()// собираем список товаров, который был добавлен в избранное
                        .doOnNext(products -> model.addAttribute("products", products))) // И возвращаем его в атрибут модели products
                .thenReturn("customer/products/favourites");
    }
}
