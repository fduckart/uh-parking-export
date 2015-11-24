package edu.hawaii.its.mis.config;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import edu.hawaii.its.mis.sftp.SftpSessionFactory;

@Configuration
public class SftpSessionConfiguration {

    @Value("${sftp.enabled}")
    private boolean isEnabled;

    @Value("${sftp.host}")
    private String host;

    @Value("${sftp.port}")
    private int port;

    @Value("${sftp.timeout}")
    private int timeout;

    @Value("${sftp.username}")
    private String username;

    @Value("${sftp.password:}")
    private String password;

    @Value("${sftp.known.hosts:}")
    private String knownHosts;

    @Value("${sftp.private.key.path:}")
    private Resource privateKeyPath;

    @Value("${sftp.private.key.passphrase:}")
    private String privateKeyPassphrase;

    @PostConstruct
    public void init() {
        Assert.hasLength(username, "property 'username' is required");
        Assert.hasLength(host, "property 'host' is required");
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Bean
    public SftpSessionFactory cachingSessionFactory() {
        SftpSessionFactory factory = new SftpSessionFactory(false, isEnabled);
        factory.setHost(host);
        factory.setPort(port);
        factory.setTimeout(timeout);
        factory.setUser(username);

        if (privateKeyPath != null) {
            factory.setPrivateKey(privateKeyPath);
        }
        if (isNotEmpty(privateKeyPassphrase)) {
            factory.setPrivateKeyPassphrase(privateKeyPassphrase);
        }

        if (isNotEmpty(password)) {
            factory.setPassword(password);
        }
        if (isNotEmpty(knownHosts)) {
            factory.setKnownHosts(knownHosts);
        }

        return factory;
    }

    private boolean isNotEmpty(String s) {
        return s != null && s.length() > 0;
    }

}
