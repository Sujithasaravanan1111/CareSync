package com.caresync.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "com.caresync.service",
        "com.caresync.repository",
        "com.caresync.security"
})
public class RootConfig {
}