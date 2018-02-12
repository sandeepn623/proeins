package com.proeins.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.proeins.service.ShoeService;

@Configuration
public class TestContext {

    @Bean
    public ShoeService shoeService() {
        return Mockito.mock(ShoeService.class);
    }
}
