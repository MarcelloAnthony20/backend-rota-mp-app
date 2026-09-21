package br.mp.mpms.exemploservico.v1.controller;

import br.mp.mpms.exemploservico.v1.model.dto.AtualizarTipoLocalidadeRequest;
import br.mp.mpms.exemploservico.v1.model.dto.CriarTipoLocalidadeRequest;
import br.mp.mpms.exemploservico.v1.model.dto.TipoLocalidadeResponse;
import br.mp.mpms.exemploservico.v1.service.TipoLocalidadeService;
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
@RequestMapping("/api/v1/tipos-localidade")
@RequiredArgsConstructor
@Tag(name = "Tipos de Localidade", description = "Endpoints para gerenciamento dos tipos e classificações de localidades")
public class TipoLocalidadeController {

    private final TipoLocalidadeService tipoLocalidadeService;

    @PostMapping
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Cadastrar novo tipo de localidade",
            description = "Cadastra um novo tipo de localidade com código único e descrição institucional."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Tipo de localidade cadastrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TipoLocalidadeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito - Código de tipo de localidade já cadastrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Erro de validação nos campos enviados",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<TipoLocalidadeResponse> cadastrar(@Valid @RequestBody CriarTipoLocalidadeRequest request) {
        TipoLocalidadeResponse response = tipoLocalidadeService.cadastrar(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar tipo de localidade por ID",
            description = "Recupera os detalhes de um tipo de localidade a partir do seu UUID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tipo de localidade localizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TipoLocalidadeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tipo de localidade não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<TipoLocalidadeResponse> buscarPorId(
            @Parameter(description = "Identificador único (UUID) do tipo de localidade", example = "t1p00001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(tipoLocalidadeService.buscarPorId(id));
    }

    @GetMapping
    @Operation(
            summary = "Listar tipos de localidade",
            description = "Lista todos os tipos de localidade com paginação."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de tipos de localidade retornada com sucesso"
            )
    })
    public ResponseEntity<Page<TipoLocalidadeResponse>> listar(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(tipoLocalidadeService.listar(pageable));
    }

    @PutMapping("/{id}")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Alterar tipo de localidade",
            description = "Atualiza os dados de um tipo de localidade existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tipo de localidade alterado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TipoLocalidadeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tipo de localidade não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito - Código já em uso por outro registro",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Erro de validação nos campos enviados",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<TipoLocalidadeResponse> alterar(
            @Parameter(description = "Identificador único (UUID) do tipo de localidade", example = "t1p00001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarTipoLocalidadeRequest request) {
        return ResponseEntity.ok(tipoLocalidadeService.alterar(id, request));
    }

    @DeleteMapping("/{id}")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Inativar tipo de localidade (soft delete)",
            description = "Inativa logicamente um tipo de localidade com retorno HTTP 204 No Content."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Tipo de localidade inativado com sucesso (sem corpo)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tipo de localidade não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<Void> inativar(
            @Parameter(description = "Identificador único (UUID) do tipo de localidade", example = "t1p00001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id) {
        tipoLocalidadeService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Ativar tipo de localidade",
            description = "Reativa um tipo de localidade inativo."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tipo de localidade ativado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TipoLocalidadeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tipo de localidade não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<TipoLocalidadeResponse> ativar(
            @Parameter(description = "Identificador único (UUID) do tipo de localidade", example = "t1p00001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(tipoLocalidadeService.ativar(id));
    }
}
