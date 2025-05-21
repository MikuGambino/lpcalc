package com.mg.lpcalc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/html/main.html");
        registry.addViewController("/simplex").setViewName("forward:/html/simplex-method.html");
        registry.addViewController("/graphical").setViewName("forward:/html/graph-method.html");
        registry.addViewController("/main").setViewName("forward:/html/main.html");
        registry.addViewController("/theory").setViewName("forward:/html/theory.html");
        registry.addViewController("/simplex-theory").setViewName("forward:/html/simplex-method-theory.html");
        registry.addViewController("/graphical-theory").setViewName("forward:/html/graph-method-theory.html");
    }
}