package com.trustai.notification_service.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "mail")
@RefreshScope
public class MailProperties {

    private String host;
    private Integer port;
    private String username;
    private String password;
    private String protocol = "smtp";
    private Charset defaultEncoding;
    private final Map<String, String> properties;

    //private boolean smtpAuth;
    //private boolean starttlsEnable;

    private From from;


    @Data
    public static class From {
        private String name;
        private String address;
    }

    @Override
    public String toString() {
        return "{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", protocol='" + protocol + '\'' +
                ", from=" + from +
                '}';
    }
}
