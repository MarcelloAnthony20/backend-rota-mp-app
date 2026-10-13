package br.mp.mpms.rotamp.infra.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakJwtGrantedAuthoritiesConverterTest {

    private final KeycloakJwtGrantedAuthoritiesConverter converter = new KeycloakJwtGrantedAuthoritiesConverter();

    @Test
    @DisplayName("deve converter roles de realm_access para GrantedAuthorities com prefixo ROLE_")
    void deve_converter_roles_realm_access() {
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("realm_access", Map.of("roles", List.of("ADMINISTRADOR", "USER")))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities)
                .extracting(a -> a.getAuthority())
                .containsExactlyInAnyOrder("ROLE_ADMINISTRADOR", "ROLE_USER");
    }

    @Test
    @DisplayName("deve converter roles de resource_access para GrantedAuthorities com prefixo ROLE_")
    void deve_converter_roles_resource_access() {
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("resource_access", Map.of(
                        "rota-mp-client", Map.of("roles", List.of("GESTOR", "MOTORISTA"))
                ))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities)
                .extracting(a -> a.getAuthority())
                .containsExactlyInAnyOrder("ROLE_GESTOR", "ROLE_MOTORISTA");
    }

    @Test
    @DisplayName("deve retornar lista vazia se token nao tiver roles")
    void deve_retornar_vazio_se_sem_roles() {
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("sub", "user-uuid")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities).isEmpty();
    }
}

