package com.example.ems.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.file")
public class FileStorageProperties {

    private String uploadDir;

    private String allowedContentTypes;

    public List<String> getAllowedContentTypesList() {
        return Arrays.asList(allowedContentTypes.split(","));
    }
}
