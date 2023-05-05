package com.example.discoveryservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${eureka.username}")
    private String username;
    @Value("${eureka.password}")
    private String password;

//    @Bean
//    public AuthenticationManager webSecurityCustomizer(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//        authenticationManagerBuilder.inMemoryAuthentication()
//                .withUser("eureka")
//                .password("password")
//                .authorities("USER");
//        return authenticationManagerBuilder.build();
//    }
//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity serverHttpSecurity){
//        serverHttpSecurity.csrf()
//                .disable()
//                .authorizeExchange(exchange -> exchange.pathMatchers("/eureka/**").permitAll()
//                        .anyExchange().authenticated())
//
//                .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt)
//                .httpBasic();
//
//
//        return serverHttpSecurity.build();
//    };
}
