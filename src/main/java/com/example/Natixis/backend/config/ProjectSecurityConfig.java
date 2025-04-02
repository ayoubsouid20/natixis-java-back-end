package com.example.Natixis.backend.config;

import com.example.Natixis.backend.exceptionhandling.CustomAccessDeniedHandler;
import com.example.Natixis.backend.filter.JWTTTokenValidatorFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class ProjectSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .sessionManagement(sessionManagementConfig ->
                        sessionManagementConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/products/delete/**", "/products/addProduct",
                                "/products/updateProduct/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                ).cors(corsConfig -> corsConfig.configurationSource(request -> {
                    // for the sake of the assignment I've allowed the the origins
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(Collections.singletonList("*"));
                    configuration.setAllowedMethods(Collections.singletonList("*"));
                    configuration.setAllowedHeaders(Collections.singletonList("*"));
                    configuration.setExposedHeaders(Arrays.asList("Authorization"));
                    configuration.setMaxAge(3600L);
                    return configuration;
                }))
                .addFilterBefore(new JWTTTokenValidatorFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable);

        //handling if the role mismatch with our api call

        httpSecurity.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
            httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(new CustomAccessDeniedHandler());
        });

        // H2 Database localhost refuse to connect
        httpSecurity.headers(headers -> headers.frameOptions(
                HeadersConfigurer.FrameOptionsConfig::disable
        ));

        return httpSecurity.build();
    }


    @Bean
    UserDetailsService userDetailsService() {
        // for the sake of the test I created 2 users one of it have Admin role and the other one have Normal User role
        // to demonstrate authorities later {noop} for simple password and not using any encryption

        UserDetails admin = User.withUsername("admin@email.com")
                .roles("ADMIN")
                .password("AYOUB123")
                .build();

        UserDetails user = User.withUsername("user@email.com")
                .roles("USER")
                .password("AYOUB123")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    // Creating our Authentication Manager Bean in order to used it for our authentication
    // NoOpPasswordEncoder is depricated I have to use Bcrypt but for testing purpose is OK
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());

        return new ProviderManager(authProvider);
    }

}
