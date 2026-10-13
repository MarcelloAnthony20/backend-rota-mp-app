package br.mp.mpms.rotamp.v1.controller;

import br.mp.mpms.rotamp.v1.model.dto.AtualizarVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.VeiculoDetalhesResponse;
import br.mp.mpms.rotamp.v1.model.dto.VeiculoResponse;
import br.mp.mpms.rotamp.v1.model.dto.VincularLocalidadeVeiculoRequest;
import br.mp.mpms.rotamp.v1.service.VeiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/veiculos")
@RequiredArgsConstructor
@Tag(name = "Veículos", description = "Endpoints para gerenciamento da frota de veículos oficiais do MPMS")
public class VeiculoController {

    private final VeiculoService veiculoService;

    @PostMapping
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Cadastrar novo veículo",
            description = "Cadastra um novo veículo na frota oficial do MPMS vinculado a um modelo ativo e localidade ativa. "
                    + "A placa informada deve ser única (RN-5). Exige perfil com permissão GESTOR (`ROLE_GESTOR`)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Veículo cadastrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VeiculoResponse.class))
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
                    description = "Modelo ou Localidade informada não encontrada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito - Placa do veículo já cadastrada (RN-5)",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Erro de validação nos campos enviados ou modelo/localidade inativo (RFC 7807)",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<VeiculoResponse> cadastrar(@Valid @RequestBody CriarVeiculoRequest request) {
        VeiculoResponse response = veiculoService.cadastrar(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar veículo por ID",
            description = "Recupera os dados cadastrais básicos de um veículo a partir de seu identificador UUID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Veículo localizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VeiculoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado - Token JWT ausente ou inválido",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Veículo não encontrado para o ID informado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<VeiculoResponse> buscarPorId(
            @Parameter(description = "Identificador único (UUID) do veículo", example = "v1d00001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(veiculoService.buscarPorId(id));
    }

    @GetMapping("/{id}/detalhes")
    @Operation(
            summary = "Consultar detalhes completos do veículo",
            description = "Recupera informações completas do veículo com dados agregados de modelo e localidade de origem (HU-04.6)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Detalhes do veículo recuperados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VeiculoDetalhesResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado - Token JWT ausente ou inválido",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Veículo não encontrado para o ID informado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<VeiculoDetalhesResponse> buscarDetalhesPorId(
            @Parameter(description = "Identificador único (UUID) do veículo", example = "v1d00001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(veiculoService.buscarDetalhesPorId(id));
    }

    @GetMapping
    @Operation(
            summary = "Listar veículos da frota",
            description = "Lista os veículos cadastrados na frota com suporte a paginação e filtros opcionais por situação (ativo), localidade de origem, modelo ou placa."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de veículos retornada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado - Token JWT ausente ou inválido",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<Page<VeiculoResponse>> listar(
            @ParameterObject br.mp.mpms.rotamp.v1.model.dto.VeiculoFiltroRequest filtro,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(veiculoService.listar(pageable, filtro));
    }

    @PutMapping("/{id}")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Atualizar veículo",
            description = "Atualiza os dados cadastrais de um veículo existente. "
                    + "A placa informada não pode pertencer a outro veículo (RN-5). Exige perfil com permissão GESTOR (`ROLE_GESTOR`)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Veículo atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VeiculoResponse.class))
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
                    description = "Veículo, Modelo ou Localidade não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito - Nova placa informada já em uso por outro veículo",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Erro de validação nos campos enviados ou modelo/localidade inativo",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<VeiculoResponse> alterar(
            @Parameter(description = "Identificador único (UUID) do veículo a ser alterado", example = "v1d00001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarVeiculoRequest request) {
        return ResponseEntity.ok(veiculoService.alterar(id, request));
    }

    @DeleteMapping("/{id}")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Inativar veículo (exclusão lógica)",
            description = "Executa a inativação lógica do veículo da frota. "
                    + "Veículos inativados ficam imediatamente impedidos de seleção para novas viagens e atendimentos operacionais (HU-04.4). "
                    + "Retorno HTTP 204 No Content sem corpo. Exige perfil com permissão GESTOR (`ROLE_GESTOR`)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Veículo inativado com sucesso (sem corpo)",
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
                    description = "Veículo não encontrado para o ID informado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<Void> inativar(
            @Parameter(description = "Identificador único (UUID) do veículo a ser inativado", example = "v1d00001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id) {
        veiculoService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Ativar veículo",
            description = "Reativa um veículo inativo na frota oficial, desde que o modelo e a localidade de origem estejam ativos. "
                    + "Exige perfil com permissão GESTOR (`ROLE_GESTOR`)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Veículo reativado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VeiculoResponse.class))
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
                    description = "Veículo não encontrado para o ID informado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Não é possível ativar o veículo pois o modelo ou a localidade vinculada está inativa",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<VeiculoResponse> ativar(
            @Parameter(description = "Identificador único (UUID) do veículo a ser reativado", example = "v1d00001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(veiculoService.ativar(id));
    }

    @PutMapping("/{id}/localidade")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Vincular veículo à localidade de origem",
            description = "Associa o veículo a uma localidade de origem existente e ativa (HU-04.5). "
                    + "Exige perfil com permissão GESTOR (`ROLE_GESTOR`)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Veículo vinculado à nova localidade de origem com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VeiculoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida ou identificador malformado",
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
                    description = "Veículo ou Localidade não encontrada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Localidade informada está inativa",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<VeiculoResponse> vincularLocalidade(
            @Parameter(description = "Identificador único (UUID) do veículo", example = "v1d00001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody VincularLocalidadeVeiculoRequest request) {
        return ResponseEntity.ok(veiculoService.vincularLocalidade(id, request.localidadeId()));
    }
}

