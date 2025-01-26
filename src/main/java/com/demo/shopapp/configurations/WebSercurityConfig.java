package com.demo.shopapp.configurations;

import com.demo.shopapp.entities.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.demo.shopapp.filters.JwtTokenFilter;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableWebMvc
public class WebSercurityConfig implements WebMvcConfigurer {
    private final JwtTokenFilter jwtTokenFilter;
    @Value("${api.prefix}")
    private final String apiPrefix;

    public WebSercurityConfig(JwtTokenFilter jwtTokenFilter,
                              @Value("${api.prefix}") String apiPrefix) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.apiPrefix = apiPrefix;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                    .authorizeHttpRequests(requests ->
                            requests
                                    .requestMatchers("**")
                                    .permitAll()
                                    .requestMatchers(HttpMethod.POST,
                                            String.format("%s/orders/**", apiPrefix))
                                    .hasRole(Role.ADMIN)

                                    .requestMatchers(HttpMethod.GET,
                                            String.format("%s/orders/**", apiPrefix))
                                    .hasAnyRole(Role.ADMIN, Role.USER)

                                    .requestMatchers(HttpMethod.PUT,
                                            String.format("%s/orders/**", apiPrefix))
                                    .hasRole(Role.ADMIN)

                                    .requestMatchers(HttpMethod.DELETE,
                                            String.format("%s/orders/**", apiPrefix))
                                    .hasRole(Role.ADMIN)

                                    .requestMatchers(HttpMethod.POST,
                                            String.format("%s/products/**", apiPrefix))
                                    .hasRole(Role.ADMIN)

                                    .requestMatchers(HttpMethod.GET,
                                            String.format("%s/products/**", apiPrefix))
                                    .hasAnyRole(Role.ADMIN, Role.USER)

                                    .requestMatchers(HttpMethod.PUT,
                                            String.format("%s/products/**", apiPrefix))
                                    .hasRole(Role.ADMIN)

                                    .requestMatchers(HttpMethod.DELETE,
                                            String.format("%s/products/**", apiPrefix))
                                    .hasRole(Role.ADMIN)

                                    .requestMatchers(HttpMethod.POST,
                                            String.format("%s/order_details/**", apiPrefix))
                                    .hasRole(Role.ADMIN)

                                    .requestMatchers(HttpMethod.GET,
                                            String.format("%s/order_details/**", apiPrefix))
                                    .hasAnyRole(Role.ADMIN, Role.USER)

                                    .requestMatchers(HttpMethod.PUT,
                                            String.format("%s/order_details/**", apiPrefix))
                                    .hasRole(Role.ADMIN)

                                    .requestMatchers(HttpMethod.DELETE,
                                            String.format("%s/order_details/**", apiPrefix))
                                    .hasRole(Role.ADMIN)

                                    .anyRequest().authenticated()

                    );

            http.csrf(AbstractHttpConfigurer::disable);
            http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
                @Override
                public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(List.of("authorization", "content-type", "x-auth-token"));
                    corsConfiguration.setExposedHeaders(List.of("x-auth-token"));
                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", corsConfiguration);
                    httpSecurityCorsConfigurer.configurationSource(source);
                }
            });
            return http.build();
    }
}
// Spring tự động hiểu thành "ROLE_ADMIN"