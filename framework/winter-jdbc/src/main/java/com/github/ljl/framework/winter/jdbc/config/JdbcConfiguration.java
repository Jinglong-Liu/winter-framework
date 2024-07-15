package com.github.ljl.framework.winter.jdbc.config;

import com.github.ljl.framework.winter.context.annotation.Autowired;
import com.github.ljl.framework.winter.context.annotation.Bean;
import com.github.ljl.framework.winter.context.annotation.Configuration;
import com.github.ljl.framework.winter.context.annotation.Value;
import com.github.ljl.framework.winter.jdbc.template.JdbcTemplate;
import com.github.ljl.framework.winter.jdbc.template.StandardJdbcTemplate;
import com.github.ljl.framework.winter.jdbc.transaction.bean.DataSourceTransactionManager;
import com.github.ljl.framework.winter.jdbc.transaction.bean.StandardTransactionManager;
import com.github.ljl.framework.winter.jdbc.transaction.bean.TransactionalBeanPostProcessor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 12:25
 **/

@Configuration
public class JdbcConfiguration {

    @Bean(destroyMethod = "close")
    public DataSource dataSource(
            // properties:
            @Value("${winter.datasource.url}") String url, //
            @Value("${winter.datasource.username}") String username, //
            @Value("${winter.datasource.password}") String password, //
            @Value("${winter.datasource.driver-class-name:}") String driver, //
            @Value("${winter.datasource.maximum-pool-size:20}") int maximumPoolSize, //
            @Value("${winter.datasource.minimum-pool-size:1}") int minimumPoolSize, //
            @Value("${winter.datasource.connection-timeout:30000}") int connTimeout //
    ) {
        HikariConfig config = new HikariConfig();
        config.setAutoCommit(false);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        if (driver != null) {
            config.setDriverClassName(driver);
        }
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumPoolSize);
        config.setConnectionTimeout(connTimeout);
        return new HikariDataSource(config);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(@Autowired DataSource dataSource) {
        return new StandardJdbcTemplate(dataSource);
    }

    @Bean
    TransactionalBeanPostProcessor transactionalBeanPostProcessor() {
        return new TransactionalBeanPostProcessor();
    }

    @Bean
    StandardTransactionManager standardTransactionManager(@Autowired DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

