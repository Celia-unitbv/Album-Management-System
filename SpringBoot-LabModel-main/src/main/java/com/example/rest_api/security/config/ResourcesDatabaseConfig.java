package com.example.rest_api.security.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.example.rest_api.database.resources.repository",
        entityManagerFactoryRef = "resourcesEntityManagerFactory",
        transactionManagerRef = "resourcesTransactionManager"
)
public class ResourcesDatabaseConfig {

    @Bean(name = "resourcesDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.resources")
    public DataSource resourcesDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url("jdbc:postgresql://localhost:5432/resources_db")
                .username("postgres")
                .password("1q2w3e")
                .build();
    }

    @Bean(name = "resourcesEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean resourcesEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("resourcesDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.example.rest_api.database.resources.model") // Modelele
                .persistenceUnit("resources")
                .properties(Map.of(
                        "hibernate.hbm2ddl.auto", "update", // Asigură-te că schema este generată
                        "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"
                ))
                .build();
    }


    @Bean(name = "resourcesTransactionManager")
    public PlatformTransactionManager resourcesTransactionManager(
            @Qualifier("resourcesEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
