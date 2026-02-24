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

import static com.manolinho.infrastructure.util.AppConstants.Rutas.CAMBIOS_TALLA_TODOS;
import static com.manolinho.infrastructure.util.AppConstants.Rutas.CONSOLA_H2;
import static com.manolinho.infrastructure.util.AppConstants.Rutas.DEVOLUCIONES_TODAS;
import static com.manolinho.infrastructure.util.AppConstants.Rutas.PEDIDOS_TODOS;
import static com.manolinho.infrastructure.util.AppConstants.Rutas.TIENDAS_PRECIO;
import static com.manolinho.infrastructure.util.AppConstants.Rutas.TIENDAS_TODAS;
import static com.manolinho.infrastructure.util.AppConstants.Seguridad.CLAVE_ADMIN;
import static com.manolinho.infrastructure.util.AppConstants.Seguridad.CLAVE_CLIENTE;
import static com.manolinho.infrastructure.util.AppConstants.Seguridad.CLAVE_CLIENTE_BLOQUEADO;
import static com.manolinho.infrastructure.util.AppConstants.Seguridad.CLAVE_EMPLEADO;
import static com.manolinho.infrastructure.util.AppConstants.Seguridad.CLAVE_EMPLEADO_JEFE;
import static com.manolinho.infrastructure.util.AppConstants.Seguridad.ROL_ADMIN;
import static com.manolinho.infrastructure.util.AppConstants.Seguridad.ROL_CLIENTE;
import static com.manolinho.infrastructure.util.AppConstants.Seguridad.ROL_CLIENTE_BLOQUEADO;
import static com.manolinho.infrastructure.util.AppConstants.Seguridad.ROL_EMPLEADO;
import static com.manolinho.infrastructure.util.AppConstants.Seguridad.ROL_EMPLEADO_JEFE;
import static com.manolinho.infrastructure.util.AppConstants.Seguridad.USUARIO_ADMIN;
import static com.manolinho.infrastructure.util.AppConstants.Seguridad.USUARIO_CLIENTE;
import static com.manolinho.infrastructure.util.AppConstants.Seguridad.USUARIO_CLIENTE_BLOQUEADO;
import static com.manolinho.infrastructure.util.AppConstants.Seguridad.USUARIO_EMPLEADO;
import static com.manolinho.infrastructure.util.AppConstants.Seguridad.USUARIO_EMPLEADO_JEFE;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filtroSeguridad(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(CONSOLA_H2).hasRole(ROL_ADMIN)
                        .requestMatchers(TIENDAS_PRECIO).hasAnyRole(ROL_CLIENTE, ROL_EMPLEADO, ROL_EMPLEADO_JEFE, ROL_ADMIN)
                        .requestMatchers(PEDIDOS_TODOS, DEVOLUCIONES_TODAS, CAMBIOS_TALLA_TODOS)
                        .hasAnyRole(ROL_CLIENTE, ROL_EMPLEADO, ROL_EMPLEADO_JEFE, ROL_ADMIN)
                        .requestMatchers(TIENDAS_TODAS).hasAnyRole(ROL_EMPLEADO, ROL_EMPLEADO_JEFE, ROL_ADMIN)
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        return http.build();
    }

    @Bean
    public UserDetailsService servicioUsuarios(PasswordEncoder codificadorClave) {
        UserDetails cliente = User.builder()
                .username(USUARIO_CLIENTE)
                .password(codificadorClave.encode(CLAVE_CLIENTE))
                .roles(ROL_CLIENTE)
                .build();

        UserDetails clienteBloqueado = User.builder()
                .username(USUARIO_CLIENTE_BLOQUEADO)
                .password(codificadorClave.encode(CLAVE_CLIENTE_BLOQUEADO))
                .roles(ROL_CLIENTE_BLOQUEADO)
                .build();

        UserDetails empleado = User.builder()
                .username(USUARIO_EMPLEADO)
                .password(codificadorClave.encode(CLAVE_EMPLEADO))
                .roles(ROL_EMPLEADO)
                .build();

        UserDetails empleadoJefe = User.builder()
                .username(USUARIO_EMPLEADO_JEFE)
                .password(codificadorClave.encode(CLAVE_EMPLEADO_JEFE))
                .roles(ROL_EMPLEADO_JEFE)
                .build();

        UserDetails admin = User.builder()
                .username(USUARIO_ADMIN)
                .password(codificadorClave.encode(CLAVE_ADMIN))
                .roles(ROL_ADMIN)
                .build();

        return new InMemoryUserDetailsManager(cliente, clienteBloqueado, empleado, empleadoJefe, admin);
    }

    @Bean
    public PasswordEncoder codificadorClave() {
        return new BCryptPasswordEncoder();
    }
}
