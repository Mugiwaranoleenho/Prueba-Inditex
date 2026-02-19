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

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").hasRole("ADMIN")
                        .requestMatchers("/tiendas/precio").hasAnyRole("CLIENTE", "EMPLEADO", "EMPLEADO_JEFE", "ADMIN")
                        .requestMatchers("/pedidos/**", "/devoluciones/**", "/cambios-talla/**")
                        .hasAnyRole("CLIENTE", "EMPLEADO", "EMPLEADO_JEFE", "ADMIN")
                        .requestMatchers("/tiendas/**").hasAnyRole("EMPLEADO", "EMPLEADO_JEFE", "ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails cliente = User.builder()
                .username("cliente")
                .password(passwordEncoder.encode("cliente123"))
                .roles("CLIENTE")
                .build();

        UserDetails clienteBloqueado = User.builder()
                .username("cliente_bloqueado")
                .password(passwordEncoder.encode("bloqueado123"))
                .roles("CLIENTE_BLOQUEADO")
                .build();

        UserDetails empleado = User.builder()
                .username("empleado")
                .password(passwordEncoder.encode("empleado123"))
                .roles("EMPLEADO")
                .build();

        UserDetails empleadoJefe = User.builder()
                .username("empleado_jefe")
                .password(passwordEncoder.encode("jefe123"))
                .roles("EMPLEADO_JEFE")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(cliente, clienteBloqueado, empleado, empleadoJefe, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
