package com.manolinho.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static com.manolinho.infrastructure.util.AppConstants.Endpoint.CAMBIOS_TALLA_ALL;
import static com.manolinho.infrastructure.util.AppConstants.Endpoint.DEVOLUCIONES_ALL;
import static com.manolinho.infrastructure.util.AppConstants.Endpoint.H2_CONSOLE;
import static com.manolinho.infrastructure.util.AppConstants.Endpoint.PEDIDOS_ALL;
import static com.manolinho.infrastructure.util.AppConstants.Endpoint.TIENDAS_ALL;
import static com.manolinho.infrastructure.util.AppConstants.Endpoint.TIENDAS_PRECIO;
import static com.manolinho.infrastructure.util.AppConstants.Security.PASSWORD_ADMIN;
import static com.manolinho.infrastructure.util.AppConstants.Security.PASSWORD_CLIENTE;
import static com.manolinho.infrastructure.util.AppConstants.Security.PASSWORD_CLIENTE_BLOQUEADO;
import static com.manolinho.infrastructure.util.AppConstants.Security.PASSWORD_EMPLEADO;
import static com.manolinho.infrastructure.util.AppConstants.Security.PASSWORD_EMPLEADO_JEFE;
import static com.manolinho.infrastructure.util.AppConstants.Security.ROLE_ADMIN;
import static com.manolinho.infrastructure.util.AppConstants.Security.ROLE_CLIENTE;
import static com.manolinho.infrastructure.util.AppConstants.Security.ROLE_CLIENTE_BLOQUEADO;
import static com.manolinho.infrastructure.util.AppConstants.Security.ROLE_EMPLEADO;
import static com.manolinho.infrastructure.util.AppConstants.Security.ROLE_EMPLEADO_JEFE;
import static com.manolinho.infrastructure.util.AppConstants.Security.USER_ADMIN;
import static com.manolinho.infrastructure.util.AppConstants.Security.USER_CLIENTE;
import static com.manolinho.infrastructure.util.AppConstants.Security.USER_CLIENTE_BLOQUEADO;
import static com.manolinho.infrastructure.util.AppConstants.Security.USER_EMPLEADO;
import static com.manolinho.infrastructure.util.AppConstants.Security.USER_EMPLEADO_JEFE;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(H2_CONSOLE).hasRole(ROLE_ADMIN)
                        .requestMatchers(TIENDAS_PRECIO).hasAnyRole(ROLE_CLIENTE, ROLE_EMPLEADO, ROLE_EMPLEADO_JEFE, ROLE_ADMIN)
                        .requestMatchers(PEDIDOS_ALL, DEVOLUCIONES_ALL, CAMBIOS_TALLA_ALL)
                        .hasAnyRole(ROLE_CLIENTE, ROLE_EMPLEADO, ROLE_EMPLEADO_JEFE, ROLE_ADMIN)
                        .requestMatchers(TIENDAS_ALL).hasAnyRole(ROLE_EMPLEADO, ROLE_EMPLEADO_JEFE, ROLE_ADMIN)
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails cliente = User.builder()
                .username(USER_CLIENTE)
                .password(passwordEncoder.encode(PASSWORD_CLIENTE))
                .roles(ROLE_CLIENTE)
                .build();

        UserDetails clienteBloqueado = User.builder()
                .username(USER_CLIENTE_BLOQUEADO)
                .password(passwordEncoder.encode(PASSWORD_CLIENTE_BLOQUEADO))
                .roles(ROLE_CLIENTE_BLOQUEADO)
                .build();

        UserDetails empleado = User.builder()
                .username(USER_EMPLEADO)
                .password(passwordEncoder.encode(PASSWORD_EMPLEADO))
                .roles(ROLE_EMPLEADO)
                .build();

        UserDetails empleadoJefe = User.builder()
                .username(USER_EMPLEADO_JEFE)
                .password(passwordEncoder.encode(PASSWORD_EMPLEADO_JEFE))
                .roles(ROLE_EMPLEADO_JEFE)
                .build();

        UserDetails admin = User.builder()
                .username(USER_ADMIN)
                .password(passwordEncoder.encode(PASSWORD_ADMIN))
                .roles(ROLE_ADMIN)
                .build();

        return new InMemoryUserDetailsManager(cliente, clienteBloqueado, empleado, empleadoJefe, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
