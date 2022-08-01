package edu.hawaii.its.mis.config;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import oracle.jdbc.pool.OracleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.util.Assert;

@Configuration
public class AppConfiguration {

    private final Logger logger = LoggerFactory.getLogger(AppConfiguration.class);

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.user}")
    private String user;

    @Value("${jdbc.password}")
    private String password;

    @PostConstruct
    public void afterPropertiesSet() {
        Assert.hasLength(url, "property 'url' is required");
        Assert.hasLength(user, "property 'user' is required");
        Assert.hasLength(password, "property 'password' is required");
    }

    @Bean
    @Primary
    @Profile("!dev")
    public DataSource dataSource() {
        OracleDataSource dataSource = null;
        try {
            dataSource = new OracleDataSource();
            dataSource.setURL(url);
            dataSource.setUser(user);
            dataSource.setPassword(password);
            dataSource.setImplicitCachingEnabled(true);
            dataSource.getConnection().setReadOnly(true);
        } catch (Exception e) {
            logger.error("Error: ", e);
        }

        return dataSource;
    }

    @Override
    public String toString() {
        return "AppConfiguration ["
                + "jdbc.url=" + url
                + "]";
    }

}
