package web.gestaoeventos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import web.gestaoeventos.repository.LoginRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService(LoginRepository loginRepository) {
        return username -> loginRepository.findByEmailIgnoreCase(username)
                .filter(login -> login.getUsuario().isAtivo())
                .map(login -> User.withUsername(login.getEmail())
                        .password(login.getSenha())
                        .authorities(login.getUsuario().getPapeis().stream()
                                .map(papel -> "ROLE_" + papel.getNome())
                                .toArray(String[]::new))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas"));
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            TokenService tokenService,
            UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(tokenService, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**", "/", "/index.html", "/css/**", "/js/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/atividades/*/auxiliares/**")
                        .hasAnyRole("COLABORADOR", "AUXILIAR")
                        .requestMatchers(HttpMethod.POST, "/atividades/*/auxiliares/**")
                        .hasRole("COLABORADOR")
                        .requestMatchers(HttpMethod.DELETE, "/atividades/*/auxiliares/**")
                        .hasRole("COLABORADOR")
                        .requestMatchers("/atividades/**").hasRole("COLABORADOR")
                        .requestMatchers(HttpMethod.POST, "/instituicoes/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/instituicoes/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/instituicoes/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/papeis/**", "/usuarios/*/papel").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/solicitacoes-colaborador")
                        .authenticated()
                        .requestMatchers(HttpMethod.GET, "/solicitacoes-colaborador/**")
                        .hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/solicitacoes-colaborador/**")
                        .hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
