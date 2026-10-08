package br.mp.mpms.rotamp.infra.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Converte as roles contidas nas claims do token JWT emitido pelo Keycloak
 * para objetos {@link GrantedAuthority} compreensíveis pelo Spring Security (com prefixo ROLE_).
 *
 * Mapeia tanto roles em 'realm_access.roles' quanto em 'resource_access.{clientId}.roles'.
 */
public class KeycloakJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String REALM_ACCESS = "realm_access";
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLES = "roles";
    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Set<String> roles = new HashSet<>();

        // 1. Extrair roles de realm_access.roles
        Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS);
        if (realmAccess != null && realmAccess.containsKey(ROLES)) {
            Object rolesObj = realmAccess.get(ROLES);
            if (rolesObj instanceof Collection<?> rolesColl) {
                rolesColl.forEach(r -> {
                    if (r != null) {
                        roles.add(r.toString());
                    }
                });
            }
        }

        // 2. Extrair roles de resource_access.*.roles
        Map<String, Object> resourceAccess = jwt.getClaim(RESOURCE_ACCESS);
        if (resourceAccess != null) {
            resourceAccess.values().forEach(clientObj -> {
                if (clientObj instanceof Map<?, ?> clientMap && clientMap.containsKey(ROLES)) {
                    Object clientRolesObj = clientMap.get(ROLES);
                    if (clientRolesObj instanceof Collection<?> clientRolesColl) {
                        clientRolesColl.forEach(r -> {
                            if (r != null) {
                                roles.add(r.toString());
                            }
                        });
                    }
                }
            });
        }

        // 3. Fallback: claim direta 'roles' se configurado no Keycloak Token Mapper
        Object directRoles = jwt.getClaim(ROLES);
        if (directRoles instanceof Collection<?> rolesColl) {
            rolesColl.forEach(r -> {
                if (r != null) {
                    roles.add(r.toString());
                }
            });
        }

        if (roles.isEmpty()) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(role -> role.startsWith(ROLE_PREFIX) ? role : ROLE_PREFIX + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}

