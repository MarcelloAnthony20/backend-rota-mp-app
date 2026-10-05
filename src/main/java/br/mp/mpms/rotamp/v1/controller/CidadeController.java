package br.mp.mpms.rotamp.v1.controller;

import br.mp.mpms.rotamp.v1.model.dto.AtualizarCidadeRequest;
import br.mp.mpms.rotamp.v1.model.dto.CidadeResponse;
import br.mp.mpms.rotamp.v1.model.dto.CriarCidadeRequest;
import br.mp.mpms.rotamp.v1.service.CidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cidades")
@RequiredArgsConstructor
@Tag(name = "Cidades", description = "Endpoints para gerenciamento do catálogo de cidades da frota do MPMS")
// @SecurityRequirement(name = "bearer-jwt")
public class CidadeController {

    private final CidadeService cidadeService;

    @PostMapping
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Cadastrar nova cidade",
            description = "Cadastra uma nova cidade no catálogo de cidades da frota do MPMS. "
                    + "Exige perfil com permissão GESTOR (`ROLE_GESTOR`). O código IBGE deve ser único."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Cidade cadastrada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CidadeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida ou violação de regra de negócio",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado - Token JWT ausente ou inválido",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado - Operação restrita ao perfil GESTOR",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito - Código IBGE já cadastrado no sistema",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Erro de validação nos campos enviados (RFC 7807)",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<CidadeResponse> cadastrar(@Valid @RequestBody CriarCidadeRequest request) {
        CidadeResponse response = cidadeService.cadastrar(request);
        java.net.URI location = org.springframework.web.servlet.support.ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar cidade por ID",
            description = "Recupera os detalhes cadastrais de uma cidade a partir de seu identificador UUID. "
                    + "Acesso permitido a todos os perfis autenticados."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cidade localizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CidadeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado - Token JWT ausente ou inválido",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cidade não encontrada para o ID informado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<CidadeResponse> buscarPorId(
            @Parameter(description = "Identificador único (UUID) da cidade", example = "c1d00001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(cidadeService.buscarPorId(id));
    }

    @GetMapping
    @Operation(
            summary = "Listar cidades",
            description = "Lista as cidades cadastradas no catálogo da frota com suporte a paginação e ordenação. "
                    + "Caso o parâmetro 'nome' seja informado, realiza consulta textual filtrada por parte do nome (case-insensitive). "
                    + "Acesso permitido a todos os perfis autenticados."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de cidades retornada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado - Token JWT ausente ou inválido",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Nenhuma cidade encontrada para o critério informado quando filtrado por nome",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<Page<CidadeResponse>> listar(
            @Parameter(description = "Termo para busca textual aproximada por nome da cidade (opcional, case-insensitive)", example = "Campo")
            @RequestParam(required = false) String nome,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        if (nome != null && !nome.isBlank()) {
            return ResponseEntity.ok(cidadeService.buscarPorNome(nome, pageable));
        }
        return ResponseEntity.ok(cidadeService.listar(pageable));
    }

    @PutMapping("/{id}")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Alterar dados de uma cidade",
            description = "Atualiza os dados cadastrais (nome, código e status de ativação) de uma cidade existente identificada pelo UUID. "
                    + "Exige perfil com permissão GESTOR (`ROLE_GESTOR`). "
                    + "O código IBGE informado não pode conflitar com outra cidade cadastrada."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cidade alterada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CidadeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida ou violação de regra de negócio",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado - Token JWT ausente ou inválido",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado - Operação restrita ao perfil GESTOR",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cidade não encontrada para o ID informado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito - Código IBGE já em uso por outra cidade",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Erro de validação nos campos enviados (RFC 7807)",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<CidadeResponse> alterar(
            @Parameter(description = "Identificador único (UUID) da cidade a ser alterada", example = "c1d00001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarCidadeRequest request) {
        return ResponseEntity.ok(cidadeService.alterar(id, request));
    }

    @DeleteMapping("/{id}")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Inativar cidade (exclusão lógica)",
            description = "Executa a inativação lógica de uma cidade no catálogo da frota a partir do seu UUID. "
                    + "A cidade é mantida no banco com status inativo, preservando o histórico de viagens e atendimentos. "
                    + "Exige perfil com permissão GESTOR (`ROLE_GESTOR`)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Cidade inativada com sucesso (sem corpo de resposta)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado - Token JWT ausente ou inválido",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado - Operação restrita ao perfil GESTOR",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cidade não encontrada para o ID informado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<Void> inativar(
            @Parameter(description = "Identificador único (UUID) da cidade a ser inativada", example = "c1d00001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id) {
        cidadeService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Ativar cidade",
            description = "Reativa uma cidade inativa no catálogo operacional da frota a partir de seu UUID. "
                    + "Exige perfil com permissão GESTOR (`ROLE_GESTOR`)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cidade reativada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CidadeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado - Token JWT ausente ou expirado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado - Operação restrita ao perfil GESTOR",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cidade não encontrada para o ID fornecido",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<CidadeResponse> ativar(
            @Parameter(description = "Identificador único (UUID) da cidade a ser ativada", example = "c1d00001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(cidadeService.ativar(id));
    }
}
