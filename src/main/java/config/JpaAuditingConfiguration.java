package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class JpaAuditingConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
        // Retorna uma implementação anônima da interface AuditorAware
        return () -> {
            // Pega a autenticação atual do contexto de segurança
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Verifica se a autenticação é nula, se não está autenticado ou se é anônima
            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
                // Se não houver usuário logado, retorna um Optional vazio ou um usuário "sistema"
                return Optional.of("SISTEMA");
            }

            // Retorna o nome do usuário logado (ou o ID, dependendo da sua implementação)
            return Optional.of(authentication.getName());
        };
    }
}

