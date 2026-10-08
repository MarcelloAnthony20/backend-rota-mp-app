package br.mp.mpms.rotamp.v1.controller;

import br.mp.mpms.rotamp.v1.model.dto.AtualizarModeloVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarModeloVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.ModeloVeiculoResponse;
import br.mp.mpms.rotamp.v1.service.ModeloVeiculoService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/veiculos/modelo", "/api/v1/veiculos/modelos", "/api/v1/modelos-veiculo"})
@RequiredArgsConstructor
@Tag(name = "Modelos de Veículos", description = "Endpoints para gerenciamento do catálogo de modelos de veículos da frota do MPMS")
public class ModeloVeiculoController {

    private final ModeloVeiculoService modeloVeiculoService;

    @PostMapping
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Cadastrar novo modelo de veículo",
            description = "Cadastra um novo modelo de veículo no catálogo da frota oficial. "
                    + "Exige perfil com permissão GESTOR (`ROLE_GESTOR`). O modelo é criado como ativo por padrão."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Modelo de veículo cadastrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ModeloVeiculoResponse.class))
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
                    description = "Conflito - Já existe um modelo cadastrado com esta marca e modelo",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Erro de validação nos campos enviados (RFC 7807)",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<ModeloVeiculoResponse> cadastrar(@Valid @RequestBody CriarModeloVeiculoRequest request) {
        ModeloVeiculoResponse response = modeloVeiculoService.cadastrar(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar modelo de veículo por ID",
            description = "Recupera os detalhes completos de um modelo de veículo pelo identificador UUID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Modelo de veículo localizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ModeloVeiculoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado - Token JWT ausente ou inválido",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Modelo de veículo não encontrado para o ID informado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<ModeloVeiculoResponse> buscarPorId(
            @Parameter(description = "Identificador único (UUID) do modelo de veículo", example = "m1d00001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(modeloVeiculoService.buscarPorId(id));
    }

    @GetMapping
    @Operation(
            summary = "Listar modelos de veículos",
            description = "Lista os modelos de veículos da frota com paginação e suporte a filtros por situação (ativo/inativo) ou busca por termo."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de modelos de veículo retornada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado - Token JWT ausente ou inválido",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<Page<ModeloVeiculoResponse>> listar(
            @Parameter(description = "Filtro opcional por status ativo")
            @RequestParam(required = false) Boolean ativo,
            @Parameter(description = "Termo para busca textual por marca ou modelo (opcional, case-insensitive)", example = "Corolla")
            @RequestParam(required = false) String termo,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(modeloVeiculoService.listar(pageable, ativo, termo));
    }

    @PutMapping("/{id}")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Alterar modelo de veículo",
            description = "Atualiza os dados de um modelo de veículo existente. Exige perfil com permissão GESTOR (`ROLE_GESTOR`)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Modelo de veículo atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ModeloVeiculoResponse.class))
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
                    description = "Modelo de veículo não encontrado para o ID informado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito - Outro modelo já utiliza a mesma marca e modelo",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Erro de validação nos campos enviados (RFC 7807)",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<ModeloVeiculoResponse> alterar(
            @Parameter(description = "Identificador único (UUID) do modelo de veículo a ser alterado", example = "m1d00001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarModeloVeiculoRequest request) {
        return ResponseEntity.ok(modeloVeiculoService.alterar(id, request));
    }

    @DeleteMapping("/{id}")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Inativar modelo de veículo (soft delete)",
            description = "Executa a inativação lógica do modelo de veículo a partir do seu UUID. "
                    + "Modelos inativados deixam de ser disponibilizados para novas associações de veículos, preservando vínculos já existentes. "
                    + "Retorno HTTP 204 No Content sem corpo. Exige perfil com permissão GESTOR (`ROLE_GESTOR`)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Modelo de veículo inativado com sucesso (sem corpo)",
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
                    description = "Modelo de veículo não encontrado para o ID informado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<Void> inativar(
            @Parameter(description = "Identificador único (UUID) do modelo de veículo a ser inativado", example = "m1d00001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id) {
        modeloVeiculoService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Ativar modelo de veículo",
            description = "Reativa um modelo de veículo inativo no catálogo da frota. Exige perfil com permissão GESTOR (`ROLE_GESTOR`)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Modelo de veículo reativado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ModeloVeiculoResponse.class))
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
                    description = "Modelo de veículo não encontrado para o ID informado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<ModeloVeiculoResponse> ativar(
            @Parameter(description = "Identificador único (UUID) do modelo de veículo a ser reativado", example = "m1d00001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(modeloVeiculoService.ativar(id));
    }

    @PutMapping(path = "/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Substituir logotipo da marca do veículo (Multipart)",
            description = "Envia um novo arquivo binário de imagem para o Azure Blob Storage / Azurite, atualizando o logotipo da marca do veículo."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Logotipo da marca atualizado com sucesso no storage",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ModeloVeiculoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Modelo de veículo não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Arquivo inválido ou formato não suportado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<ModeloVeiculoResponse> atualizarLogo(
            @Parameter(description = "Identificador único (UUID) do modelo de veículo", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Arquivo binário da imagem do logotipo (JPEG, PNG ou WebP - máx. 10MB)", required = true)
            @RequestParam("arquivo") MultipartFile arquivo) {
        return ResponseEntity.ok(modeloVeiculoService.atualizarLogo(id, arquivo));
    }

    @PutMapping(path = "/{id}/logo/url", consumes = MediaType.APPLICATION_JSON_VALUE)
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Atualizar logotipo da marca via URL",
            description = "Atualiza a URL do logotipo da marca vinculada ao modelo."
    )
    public ResponseEntity<ModeloVeiculoResponse> atualizarLogoPorUrl(
            @PathVariable UUID id,
            @RequestBody String url) {
        return ResponseEntity.ok(modeloVeiculoService.atualizarLogoPorUrl(id, url));
    }
}
