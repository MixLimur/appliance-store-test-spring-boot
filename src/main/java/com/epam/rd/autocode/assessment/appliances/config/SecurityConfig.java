package com.epam.rd.autocode.assessment.appliances.config;

import com.epam.rd.autocode.assessment.appliances.security.CookieAuthenticationSuccessHandler;
import com.epam.rd.autocode.assessment.appliances.security.CookieLogoutHandler;
import com.epam.rd.autocode.assessment.appliances.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.NullSecurityContextRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CookieAuthenticationSuccessHandler successHandler;
    private final CookieLogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityContext(context -> context.securityContextRepository(new NullSecurityContextRepository()))
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
                        .successHandler(successHandler)
                        .failureHandler((request, response, exception) -> {
                            response.sendRedirect("/login?error");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .addLogoutHandler(logoutHandler)
                        .permitAll()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
