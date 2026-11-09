package com.epam.rd.autocode.assessment.appliances.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/styles/**", "/css/**", "/js/**").permitAll()

                        .requestMatchers("/clients/**", "/add-client").hasRole("EMPLOYEE")
                        .requestMatchers("/employees/**", "/add-employee").hasRole("EMPLOYEE")
                        .requestMatchers("/manufacturers/**", "/add-manufacturer").hasRole("EMPLOYEE")
                        .requestMatchers("/appliances/**", "/add-appliance").hasRole("EMPLOYEE")

                        .requestMatchers("/orders/{id}/approved", "/orders/{id}/unapproved").hasRole("EMPLOYEE")

                        .requestMatchers("/orders/**", "/add-order", "/add-into-order").hasAnyRole("EMPLOYEE", "CLIENT")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/orders", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
