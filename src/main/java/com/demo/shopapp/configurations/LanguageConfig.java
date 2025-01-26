package com.demo.shopapp.configurations;


import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.ResourceBundle;

@Configuration
public class LanguageConfig {
    // nơi chứa tệp đa ngôn ngữ
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n.messages"); // tên cơ sở các tệp đa ngôn ngữ
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
