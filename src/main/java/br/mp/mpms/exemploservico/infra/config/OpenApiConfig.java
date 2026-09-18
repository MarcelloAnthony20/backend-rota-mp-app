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

                                ### Arquitetura e Diretrizes Institucionais
                                - **Padrão Arquitetural:** Microsserviço stateless estruturado em camadas (Controller → Service → Repository), com DTOs desacoplados de entidades JPA e versionamento de API (`/api/v1/`).
                                - **Tratamento de Erros:** Padronização RFC 7807 (`application/problem+json`) via `@ControllerAdvice` global.
                                - **Privacidade e LGPD:** Logs e payloads isentos de PII (dados pessoais sensíveis, CPF sem máscara, senhas, tokens ou IPs internos).

                                ### Autenticação e Segurança (AppSec & Keycloak SSO)
                                - **Provedor de Identidade:** Autenticação centralizada exclusivamente via Keycloak SSO institucional do MPMS (`skill-seguranca`).
                                - **Validação de Token:** Validação automática de tokens JWT pelo Resource Server / Sidecar institucional.
                                - **Formato do Cabeçalho:** Todas as requisições protegidas exigem o cabeçalho `Authorization: Bearer <token>`.
                                - **Políticas de Rede e Cabeçalhos:** CORS restrito a domínios `*.mpms.mp.br` e headers de segurança ativos (`X-Content-Type-Options`, `X-Frame-Options`, `HSTS`, `CSP`).

                                ### Matriz de Perfis de Acesso e Permissões (RBAC)
                                O sistema adota controle de acesso baseado em perfis (Role-Based Access Control) integrado ao Keycloak:
                                1. `ADMINISTRADOR`:
                                   - Gestão de acessos, usuários, atribuição de perfis e parametrizações institucionais.
                                   - Configuração de permissões de telas e módulos para a navegação adaptativa (RN-3).
                                   - Gerenciamento e sincronização de identidades com o provedor SSO.
                                2. `GESTOR`:
                                   - Gestão operacional completa do catálogo da frota (Cidades, Localidades, Tipos de Localidade, Veículos, Modelos, Fotos e Motoristas).
                                   - Gestão do despacho logístico: análise, aprovação, reprovação justificada de solicitações, atribuição de motorista/veículo e conclusão de viagens.
                                3. `MEMBRO`:
                                   - Membros do Ministério Público (Procuradores e Promotores de Justiça).
                                   - Solicitação de viagens e atendimentos institucionais com **Alta Prioridade** automática (HU-11.8).
                                4. `CLIENTE` (ou `SOLICITANTE`):
                                   - Servidores, assessores e colaboradores do MPMS.
                                   - Solicitação de atendimentos com **Prioridade Regular** e acompanhamento em tempo real de seus próprios pedidos.
                                5. `MOTORISTA`:
                                   - Condutores oficiais vinculados à frota institucional.
                                   - Consulta de escalas, execução de viagens despachadas e atualização do status das viagens em tempo real (em trânsito, finalizada, etc.).
                                6. `USUARIO`:
                                   - Papel base atribuído a qualquer usuário autenticado via SSO Keycloak.
                                   - Acesso à navegação adaptativa básica e persistência de preferências de tema (Dark/Light).

                                ### Modelo de Prioridades de Atendimento (HU-11.8)
                                - **Alta Prioridade (`MEMBRO`):** Solicitações emitidas por Procuradores e Promotores de Justiça recebem prioridade máxima na fila de análise e despacho logístico, garantindo suporte célere às atividades finalísticas do MPMS.
                                - **Prioridade Regular (`CLIENTE` / `SOLICITANTE`):** Solicitações emitidas por servidores e colaboradores tramitam pelo fluxo regular por ordem cronológica e disponibilidade de frota.

                                ### Navegação Adaptativa (RN-3)
                                Telas, rotas e opções de menu são adaptadas dinamicamente no frontend com base nos perfis do usuário autenticado, com validação estrita de segurança e RBAC no backend em cada operação (retornando `403 Forbidden` em tentativas de acesso não autorizado).""")
                        .version("1.0.0")
                        .license(new License()
                                .name("Uso institucional restrito - MPMS")))
                .tags(List.of(
                        new Tag()
                                .name("Cidades")
                                .description("Épico 7 (HU-08): Operações de gerenciamento do catálogo de cidades atendidas pela frota do MPMS (cadastro, consulta paginada, alteração e inativação lógica)."),
                        new Tag()
                                .name("Localidades")
                                .description("Épico 9 (HU-10): Operações de gerenciamento de localidades, lotações, polos regionais e sigilo de endereços institucionais do MPMS."),
                        new Tag()
                                .name("Tipos de Localidades")
                                .description("Épico 8 (HU-09): Operações de catálogo e parametrização dos tipos e classificações de localidades da instituição."),
                        new Tag()
                                .name("Veículos")
                                .description("Épico 4 (HU-04): Gestão de veículos oficiais da frota, vinculação à localidade de origem, especificações e inativação."),
                        new Tag()
                                .name("Modelos de Veículos")
                                .description("Épico 6 (HU-07): Gestão de marcas e modelos de veículos homologados na frota do MPMS."),
                        new Tag()
                                .name("Imagens de Veículos")
                                .description("Épico 12 (HU-06): Gestão, upload e consulta de fotografias oficiais dos veículos da frota."),
                        new Tag()
                                .name("Motoristas")
                                .description("Épico 5 (HU-05): Cadastro, lotação em localidade, disponibilidade e gestão de condutores oficiais."),
                        new Tag()
                                .name("Solicitações de Atendimento")
                                .description("Épico 10 (HU-11): Solicitação de viagens por Membros (Alta Prioridade) e Clientes (Prioridade Regular), cancelamento e acompanhamento."),
                        new Tag()
                                .name("Despacho Logístico")
                                .description("Épico 11 (HU-12): Triagem, aprovação, reprovação justificada, atribuição de frota/motorista e conclusão de viagens pelos Gestores."),
                        new Tag()
                                .name("Usuários")
                                .description("Épico 1 (HU-01): Gestão de usuários institucionais, concessão, consulta, revogação de acessos e auditoria."),
                        new Tag()
                                .name("Perfis e Permissões")
                                .description("Épico 2 (HU-02): Vínculo de perfis RBAC, permissões por tela (RN-3) e sincronização com Keycloak SSO."),
                        new Tag()
                                .name("Configuração de Tema")
                                .description("Épico 3 (HU-03): Persistência e aplicação de preferências de interface do usuário (Dark/Light)."),
                        new Tag()
                                .name("Health & Métricas")
                                .description("Observabilidade, telemetria, liveness, readiness e métricas de integridade via Spring Boot Actuator.")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                .schemaRequirement("bearer-jwt", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
    }
}
