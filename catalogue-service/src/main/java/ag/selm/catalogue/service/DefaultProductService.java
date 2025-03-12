package ag.selm.catalogue.service;

import ag.selm.catalogue.entity.Product;
import ag.selm.catalogue.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultProductService implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public Iterable<Product> findAllProducts(String filter) {
        if (filter != null && !filter.isBlank()) {
            return productRepository.findAllByTitleLikeIgnoreCase("%" + filter + "%");
        } else {
            return productRepository.findAll();
        }
    }

    @Override
    @Transactional
    public Product createProduct(String title, String details) {
        return productRepository.save(new Product(null, title, details));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findProduct(int productId) {
        return productRepository.findById(productId);
    }

    @Override
    @Transactional
    public void updateProduct(Integer id, String title, String details) {
        productRepository.findById(id)
                .ifPresentOrElse(product -> {
                    product.setTitle(title);
                    product.setDetails(details);
                }, () -> {
                    throw new NoSuchElementException();
                });
    }

    @Override
    @Transactional
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }
}
