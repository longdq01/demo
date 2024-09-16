package com.example.consumer.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@Data
@ConfigurationProperties(prefix = "mysql")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.example.consumer.repository.mysql")
public class MySQLConfig {
    private String driver;
    private String url;
    private String username;
    private String password;

    private int maxPoolSize;
    private int timeout;
    private int minIdle;
    private int maxLifetime;
    private int idleTimeout;


    @Bean
    public DataSource hikariDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .driverClassName(driver)
                .url(url)
                .username(username)
                .password(password)
                .build();
        dataSource.setMaximumPoolSize(maxPoolSize);
        dataSource.setConnectionTimeout(timeout);
        dataSource.setMinimumIdle(minIdle);
        dataSource.setIdleTimeout(maxLifetime);
        dataSource.setMaxLifetime(idleTimeout);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(hikariDataSource());
        factory.setPackagesToScan("com.example.consumer.model.entity");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return txManager;
    }
}
