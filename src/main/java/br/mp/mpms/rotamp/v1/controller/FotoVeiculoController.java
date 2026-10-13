package br.mp.mpms.rotamp.v1.controller;

import br.mp.mpms.rotamp.infra.exception.BusinessException;
import br.mp.mpms.rotamp.v1.model.dto.AtualizarFotoVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarFotoVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.FotoVeiculoResponse;
import br.mp.mpms.rotamp.v1.service.FotoVeiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/veiculos/foto")
@RequiredArgsConstructor
@Tag(name = "Fotos de Veículos", description = "Endpoints para upload, armazenamento no Azure/Azurite e gerenciamento de fotos da frota")

public class FotoVeiculoController {

    private final FotoVeiculoService fotoVeiculoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Fazer upload de fotografia do veículo (Multipart)",
            description = "Envia o binário da imagem para o Azure Blob Storage / Azurite, gerando e persistindo a URL pública associada ao veículo (HU-06.1). Exige perfil GESTOR (`ROLE_GESTOR`)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Fotografia enviada para o storage e cadastrada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FotoVeiculoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador do veículo ausente ou inválido",
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
                    description = "Veículo informado não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Arquivo inválido, formato não suportado ou vazio (RFC 7807)",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Serviço de storage (Azure/Azurite) indisponível",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<FotoVeiculoResponse> upload(
            @Parameter(description = "Identificador único (UUID) do veículo", required = true)
            @RequestParam("veiculoId") UUID veiculoId,
            @Parameter(description = "Arquivo binário de imagem (JPEG, PNG ou WebP - máx. 10MB)", required = true)
            @RequestParam("arquivo") MultipartFile arquivo) {
        FotoVeiculoResponse response = fotoVeiculoService.adicionar(veiculoId, arquivo);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{fotoId}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping(path = "/url", consumes = MediaType.APPLICATION_JSON_VALUE)
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Registrar fotografia do veículo via URL",
            description = "Registra diretamente uma URL de imagem previamente hospedada no catálogo do veículo."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Fotografia cadastrada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FotoVeiculoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador do veículo não fornecido",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Veículo não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Campos inválidos ou não informados",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<FotoVeiculoResponse> adicionarPorUrl(
            @RequestParam(value = "veiculoId", required = false) UUID paramVeiculoId,
            @Valid @RequestBody CriarFotoVeiculoRequest request) {
        UUID veiculoId = request.veiculoId() != null ? request.veiculoId() : paramVeiculoId;
        if (veiculoId == null) {
            throw new BusinessException("O identificador do veiculo (veiculoId) e obrigatorio");
        }

        FotoVeiculoResponse response = fotoVeiculoService.adicionar(veiculoId, request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/veiculos/foto/{fotoId}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(
            summary = "Consultar fotografias da frota ou de um veículo específico",
            description = "Se `veiculoId` for informado, retorna as fotos vinculadas ao veículo. Caso contrário, lista as fotos paginadas (HU-06.2)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de fotografias retornada com sucesso",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FotoVeiculoResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Veículo não encontrado (quando veiculoId fornecido)",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<?> listar( // Changed to ResponseEntity<?> to accommodate both List and Page
            @Parameter(description = "Identificador único (UUID) do veículo (opcional)")
            @RequestParam(value = "veiculoId", required = false) UUID veiculoId,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        if (veiculoId != null) {
            return ResponseEntity.ok(fotoVeiculoService.listarPorVeiculo(veiculoId));
        }
        return ResponseEntity.ok(fotoVeiculoService.listar(pageable)); // Retorna a página completa com metadados
    }

    @GetMapping("/{fotoId}")
    @Operation(
            summary = "Consultar fotografia específica por ID",
            description = "Recupera os detalhes de uma fotografia pelo seu identificador UUID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Fotografia localizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FotoVeiculoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Fotografia não encontrada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<FotoVeiculoResponse> buscarPorId(
            @Parameter(description = "Identificador único (UUID) da fotografia", required = true)
            @PathVariable UUID fotoId) {
        return ResponseEntity.ok(fotoVeiculoService.buscarPorId(fotoId));
    }

    @PutMapping(path = "/{fotoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Substituir fotografia do veículo (Multipart)",
            description = "Substitui a imagem no Azure Blob / Azurite, apagando o arquivo antigo e atualizando a URL (HU-06.3)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Fotografia substituída com sucesso no storage",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FotoVeiculoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Fotografia não encontrada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Arquivo inválido ou formato não suportado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<FotoVeiculoResponse> substituir(
            @PathVariable UUID fotoId,
            @RequestParam("arquivo") MultipartFile arquivo) {
        return ResponseEntity.ok(fotoVeiculoService.alterar(fotoId, arquivo));
    }

    @PutMapping(path = "/{fotoId}/url", consumes = MediaType.APPLICATION_JSON_VALUE)
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Alterar fotografia do veículo via URL",
            description = "Atualiza a URL de uma fotografia existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Fotografia atualizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FotoVeiculoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Fotografia não encontrada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "URL inválida ou ausente",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<FotoVeiculoResponse> alterarPorUrl(
            @PathVariable UUID fotoId,
            @Valid @RequestBody AtualizarFotoVeiculoRequest request) {
        return ResponseEntity.ok(fotoVeiculoService.alterar(fotoId, request));
    }

    @DeleteMapping("/{fotoId}")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Remover fotografia do veículo",
            description = "Exclui a fotografia do banco de dados e remove fisicamente o arquivo do Azure Storage / Azurite (HU-06.4). Exige perfil GESTOR (`ROLE_GESTOR`)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Fotografia removida com sucesso do banco e do storage (sem corpo)",
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
                    description = "Fotografia não encontrada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<Void> remover(
            @Parameter(description = "Identificador único (UUID) da fotografia a ser removida", required = true)
            @PathVariable UUID fotoId) {
        fotoVeiculoService.remover(fotoId);
        return ResponseEntity.noContent().build();
    }
}
