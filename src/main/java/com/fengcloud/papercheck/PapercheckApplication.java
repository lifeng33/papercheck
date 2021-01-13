package com.fengcloud.papercheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;

@ServletComponentScan
@SpringBootApplication
@EnableAsync
public class PapercheckApplication extends SpringBootServletInitializer {
    @Override
    public SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(PapercheckApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(PapercheckApplication.class, args);
    }

}
