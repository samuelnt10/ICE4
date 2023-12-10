package ca.gbc.productservice;

import ca.gbc.productservice.dto.ProductRequest;
import ca.gbc.productservice.dto.ProductResponse;
import ca.gbc.productservice.model.Product;
import ca.gbc.productservice.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class ProductServiceApplicationTests extends AbstractContainerBaseTest {

    @Autowired //allows us dependencies injection
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    private ProductRequest getProductRequest(){
        return ProductRequest.builder().
                name("Apple iPad 2022").
                description("Apple iPad version 2022").
                price(BigDecimal.valueOf(1200))
                .build();
    }

    private List<Product> getProducts() {

        List<Product> products = new ArrayList<>();
        UUID uuid = UUID.randomUUID();

        Product product = Product.builder()
                .id(uuid.toString())
                .name("Apple iPad 2022")
                .description("Apple iPad version 2022").
                price(BigDecimal.valueOf(1200))
                .build();

        products.add(product);

        return products;
    }


    private String convertObjectToString(List<ProductResponse> productList) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(productList);
    }


    private List<ProductResponse> convertStringToObject(String jsonString) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, new TypeReference<List<ProductResponse>>() {});

    }


    //Testing
    @Test
    void createProduct() throws Exception{

        ProductRequest productRequest = getProductRequest();
        String productRequestJsonString =objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productRequestJsonString))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        //Assertions
        Assertions.assertFalse(productRepository.findAll().isEmpty());

        Query query = new Query();
        query.addCriteria(Criteria.where("name").is("Apple iPad 2022"));
        List<Product> product = mongoTemplate.find(query, Product.class);
        Assertions.assertFalse(product.isEmpty());
    }

    /****************************************
        - BDD --> Behaviour Driven Development
            - Given --> Setup
            - When --> Action
            - Then ---> Verifying
     ***************************************/
    @Test
    void getAllProducts() throws Exception {

        //Given
        productRepository.saveAll(getProducts());

        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/product")
                .accept(MediaType.APPLICATION_JSON));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
        response.andDo(MockMvcResultHandlers.print()); // useful in debugging NOT dynamic testing.

        MvcResult result = response.andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNodes = new ObjectMapper().readTree(jsonResponse);

        int actualSize = jsonNodes.size();
        int expectedSize = getProducts().size();

        assertEquals(expectedSize, actualSize);

    }

    @Test
    void updateProduct() throws Exception{
        //Given
        Product savedProduct = Product.builder()
                .id(UUID.randomUUID().toString())
                .name("Widget")
                .description("Widget Original Price")
                .price(BigDecimal.valueOf(100)).build();

        //Saved Product with original price
         productRepository.save(savedProduct);

        //Prepare updated product nad productRequest
         savedProduct.setPrice(BigDecimal.valueOf(200));
         String productRequestString = objectMapper.writeValueAsString(savedProduct);

        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/product/" + savedProduct.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(productRequestString));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isNoContent());
        response.andDo(MockMvcResultHandlers.print());

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(savedProduct.getId()));
        Product storedProduct = mongoTemplate.findOne(query, Product.class);

        assertEquals(savedProduct.getPrice(), storedProduct.getPrice());

    }

    @Test
    void deleteProduct() throws Exception {

        // Given
        Product savedProduct = Product.builder()
                .id(UUID.randomUUID().toString())
                .name("Java Microservices Programming")
                .description("Course Textbook - Java Microservices Programming")
                .price(BigDecimal.valueOf(200)).build();

        productRepository.save(savedProduct); // Save and get the updated instance

        // When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/product/" + savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isNoContent());
        response.andDo(MockMvcResultHandlers.print());

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(savedProduct.getId()));
        Long productCount = mongoTemplate.count(query, Product.class);

        assertEquals(0, productCount);
    }



}
