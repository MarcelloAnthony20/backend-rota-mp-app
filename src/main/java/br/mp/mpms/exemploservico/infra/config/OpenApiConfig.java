package br.mp.mpms.exemploservico.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rota MP API - Sistema de Gestão de Frotas e Despacho Logístico")
                        .description("""
                                API RESTful do Rota MP APP para gerenciamento de frotas, viagens, cidades, localidades, motoristas e veículos do Ministério Público do Estado de Mato Grosso do Sul (MPMS).

                                ### Autenticação
                                Todas as requisições protegidas exigem token JWT emitido pelo Keycloak institucional do MPMS via cabeçalho `Authorization: Bearer <token>`.

                                ### Perfis e Permissões (RBAC)
                                - `GESTOR`: Permissão completa para criação, alteração e inativação de entidades.
                                - `MOTORISTA` / `SOLICITANTE`: Acesso de consulta e fluxos específicos operacionais.""")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Departamento de Informática e Desenvolvimento - DID/MPMS")
                                .email("suporte.ti@mpms.mp.br")
                                .url("https://www.mpms.mp.br"))
                        .license(new License()
                                .name("Uso institucional restrito - MPMS")))
                .tags(List.of(
                        new Tag()
                                .name("Cidades")
                                .description("Operações de gerenciamento do catálogo de cidades atendidas pela frota do MPMS (cadastro, consulta, alteração e inativação lógica)."),
                        new Tag()
                                .name("Localidades")
                                .description("Operações de gerenciamento de localidades, lotações e endereços institucionais do MPMS."),
                        new Tag()
                                .name("Health & Métricas")
                                .description("Endpoints de observabilidade e integridade via Spring Boot Actuator.")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                .schemaRequirement("bearer-jwt", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
    }
}
