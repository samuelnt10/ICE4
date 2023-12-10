package ca.gbc.inventoryservice;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractContainerBaseTest {
    static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER;

    static {
        POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>("postgres:latest");
        POSTGRE_SQL_CONTAINER.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry _registry) {
        _registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
        _registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
        _registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);
    }
}
