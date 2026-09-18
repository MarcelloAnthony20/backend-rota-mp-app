package br.mp.mpms.exemploservico.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
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
                                API para gestão de frotas e viagens do MPMS. Autenticação via Keycloak (Bearer JWT).

                                ### Perfis de Usuário:
                                - **ADMINISTRADOR**: Gerencia acessos, permissões e usuários.
                                - **GESTOR**: Gerencia a frota e despacha as viagens.
                                - **MEMBRO**: Procuradores e Promotores (solicitações com Alta Prioridade).
                                - **CLIENTE**: Servidores e assessores (solicitações com Prioridade Regular).
                                - **MOTORISTA**: Condutores oficiais (executa viagens e atualiza status).
                                - **USUARIO**: Usuário básico autenticado (preferências de tema).""")
                        .version("1.0.0")
                        .license(new License()
                                .name("Uso institucional restrito - MPMS")))
                .tags(List.of(
                        new Tag()
                                .name("Cidades")
                                .description("Gestão do catálogo de cidades atendidas pela frota."),
                        new Tag()
                                .name("Localidades")
                                .description("Gestão de localidades, lotações e polos regionais."),
                        new Tag()
                                .name("Tipos de Localidades")
                                .description("Catálogo e parametrização dos tipos de localidades."),
                        new Tag()
                                .name("Veículos")
                                .description("Gestão de veículos oficiais da frota."),
                        new Tag()
                                .name("Modelos de Veículos")
                                .description("Catálogo de marcas e modelos de veículos homologados."),
                        new Tag()
                                .name("Imagens de Veículos")
                                .description("Upload e consulta de fotografias oficiais dos veículos."),
                        new Tag()
                                .name("Motoristas")
                                .description("Cadastro e disponibilidade de condutores oficiais."),
                        new Tag()
                                .name("Solicitações de Atendimento")
                                .description("Solicitação, acompanhamento e cancelamento de viagens."),
                        new Tag()
                                .name("Despacho Logístico")
                                .description("Triagem, aprovação e despacho de viagens."),
                        new Tag()
                                .name("Usuários")
                                .description("Gestão de usuários institucionais e auditoria de acessos."),
                        new Tag()
                                .name("Perfis e Permissões")
                                .description("Vínculo de perfis RBAC e permissões por tela."),
                        new Tag()
                                .name("Configuração de Tema")
                                .description("Preferências de interface do usuário (Dark/Light)."),
                        new Tag()
                                .name("Health & Métricas")
                                .description("Observabilidade e métricas de integridade via Spring Boot Actuator.")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                .schemaRequirement("bearer-jwt", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
    }
}
