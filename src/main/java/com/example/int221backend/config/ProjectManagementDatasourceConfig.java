package com.example.int221backend.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.example.int221backend.repositories.local",
        entityManagerFactoryRef = "taskBoardEntityManagerFactory",
        transactionManagerRef = "taskBoardTransactionManager"
)
public class ProjectManagementDatasourceConfig {

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean taskBoardEntityManagerFactory(
            @Qualifier("taskBoardDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder) {
        return builder.dataSource(dataSource)
                .packages("com.example.int221backend.entities.local")
                .build();
    }

    @Bean
    public PlatformTransactionManager taskBoardTransactionManager(
            @Qualifier("taskBoardEntityManagerFactory") LocalContainerEntityManagerFactoryBean taskBoardEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(taskBoardEntityManagerFactory.getObject()));
    }
}
