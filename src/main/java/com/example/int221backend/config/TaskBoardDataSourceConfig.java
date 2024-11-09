package com.example.int221backend.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class TaskBoardDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.task.datasource")
    public DataSourceProperties taskBoardDataSourceProps() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource taskBoardDataSource() {
        return taskBoardDataSourceProps().initializeDataSourceBuilder().build();
    }
}
