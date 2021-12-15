package com.mt.access.application.cors_profile.command;

import lombok.Data;

import java.util.Set;

@Data
public class CORSProfileUpdateCommand {
    private Boolean allowCredentials;
    private Set<String> allowedHeaders;
    private Set<String> allowOrigin;
    private Set<String> exposedHeaders;
    private Long maxAge;
    private String name;
    private String description;
}
