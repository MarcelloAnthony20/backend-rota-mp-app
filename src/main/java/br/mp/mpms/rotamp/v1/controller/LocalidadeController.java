package br.mp.mpms.rotamp.v1.controller;

import br.mp.mpms.rotamp.v1.model.dto.AtualizarLocalidadeRequest;
import br.mp.mpms.rotamp.v1.model.dto.ClassificarLocalidadeRequest;
import br.mp.mpms.rotamp.v1.model.dto.ConfigurarSigiloRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarLocalidadeRequest;
import br.mp.mpms.rotamp.v1.model.dto.LocalidadeResponse;
import br.mp.mpms.rotamp.v1.service.LocalidadeService;
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
@RequestMapping("/api/v1/localidades")
@RequiredArgsConstructor
@Tag(name = "Localidades", description = "Endpoints para gerenciamento do cadastro operacional de localidades do MPMS")
public class LocalidadeController {

    private final LocalidadeService localidadeService;

    @PostMapping
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Cadastrar nova localidade",
            description = "Cadastra uma nova localidade vinculada a uma cidade ativa e a um tipo de localidade ativo. "
                    + "Para localidades não sigilosas, o endereço completo é obrigatório."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Localidade cadastrada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocalidadeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida ou violação de regra de negócio",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cidade ou Tipo de Localidade não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito - Já existe uma localidade com este nome para a mesma cidade e tipo (RN-6)",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Erro de validação de campos ou cidade/tipo inativo",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<LocalidadeResponse> cadastrar(@Valid @RequestBody CriarLocalidadeRequest request) {
        LocalidadeResponse response = localidadeService.cadastrar(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar localidade por ID",
            description = "Recupera os detalhes de uma localidade. "
                    + "Caso a localidade seja sigilosa e o usuário não possua privilégio de sigilo, os dados de endereço são mascarados."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Localidade localizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocalidadeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Localidade não encontrada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<LocalidadeResponse> buscarPorId(
            @Parameter(description = "Identificador único (UUID) da localidade", example = "d1000001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(localidadeService.buscarPorId(id));
    }

    @GetMapping
    @Operation(
            summary = "Listar localidades",
            description = "Lista as localidades cadastradas de forma paginada."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de localidades retornada com sucesso"
            )
    })
    public ResponseEntity<Page<LocalidadeResponse>> listar(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(localidadeService.listar(pageable));
    }

    @PutMapping("/{id}")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Alterar dados de uma localidade",
            description = "Atualiza os dados cadastrais e de endereço de uma localidade existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Localidade alterada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocalidadeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Localidade, Cidade ou Tipo não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito - Nome duplicado para a mesma cidade e tipo (RN-6)",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Erro de validação ou cidade/tipo inativo",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<LocalidadeResponse> alterar(
            @Parameter(description = "Identificador único (UUID) da localidade", example = "d1000001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarLocalidadeRequest request) {
        return ResponseEntity.ok(localidadeService.alterar(id, request));
    }

    @DeleteMapping("/{id}")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Inativar localidade (soft delete)",
            description = "Inativa logicamente uma localidade com retorno HTTP 204 No Content sem corpo."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Localidade inativada com sucesso (sem corpo)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Localidade não encontrada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<Void> inativar(
            @Parameter(description = "Identificador único (UUID) da localidade", example = "d1000001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id) {
        localidadeService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Ativar localidade",
            description = "Reativa uma localidade inativa, desde que a cidade e o tipo vinculados estejam ativos."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Localidade ativada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocalidadeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Localidade não encontrada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Não é possível ativar a localidade pois a cidade ou o tipo vinculado está inativo",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<LocalidadeResponse> ativar(
            @Parameter(description = "Identificador único (UUID) da localidade", example = "d1000001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(localidadeService.ativar(id));
    }

    @PatchMapping("/{id}/classificacao")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Classificar localidade (atualizar tipo)",
            description = "Atualiza a classificação institucional da localidade vinculando-a a um novo tipo ativo."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Classificação atualizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocalidadeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Localidade ou Tipo de Localidade não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "O tipo de localidade informado está inativo",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<LocalidadeResponse> classificar(
            @Parameter(description = "Identificador único (UUID) da localidade", example = "d1000001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody ClassificarLocalidadeRequest request) {
        return ResponseEntity.ok(localidadeService.classificar(id, request));
    }

    @PatchMapping("/{id}/sigilo")
    // TODO: Reativar @PreAuthorize("hasRole('GESTOR')") quando o Keycloak estiver integrado
    // @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Configurar sigilo da localidade",
            description = "Atualiza a flag de sigilo da localidade."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Configuração de sigilo atualizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocalidadeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida ou valor de sigilo não booleano",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Localidade não encontrada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Não é possível remover sigilo de localidade sem endereço",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<LocalidadeResponse> configurarSigilo(
            @Parameter(description = "Identificador único (UUID) da localidade", example = "d1000001-0000-0000-0000-000000000001", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody ConfigurarSigiloRequest request) {
        return ResponseEntity.ok(localidadeService.configurarSigilo(id, request));
    }
}
