package com.demo.shopapp.domain.payment.config;


import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PayPalConfig {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PayPalConfig.class);
    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    @Bean
    public Map<String, String> paypalSdkConfig() {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", mode);
        logger.info("PayPal SDK Config: mode={}", mode);
        return configMap;
    }

    @Bean
    public OAuthTokenCredential oAuthTokenCredential() {
        return new OAuthTokenCredential(clientId, clientSecret, paypalSdkConfig());
    }

    @Bean
    public APIContext apiContext() {
        try {
            String accessToken = oAuthTokenCredential().getAccessToken();
            logger.info("PayPal Access Token: {}", accessToken);
            APIContext context = new APIContext(accessToken);
            context.setConfigurationMap(paypalSdkConfig());
            return context;
        } catch (PayPalRESTException e) {
            logger.error("Failed to initialize PayPal APIContext: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize PayPal APIContext", e);
        }
    }
}
