package ca.gbc.productservice.repository;

import ca.gbc.productservice.model.Product;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;

public interface ProductRepository extends MongoRepository<Product, String> {
    @DeleteQuery
    void deleteById(@NonNull String productId);
}
