package br.mp.mpms.rotamp.v1.controller;

import br.mp.mpms.rotamp.v1.model.dto.AlterarStatusPerfilRequest;
import br.mp.mpms.rotamp.v1.model.dto.AtualizarPerfilRequest;
import br.mp.mpms.rotamp.v1.model.dto.ConfigurarPermissoesPerfilRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarPerfilRequest;
import br.mp.mpms.rotamp.v1.model.dto.PerfilResponse;
import br.mp.mpms.rotamp.v1.model.dto.TelaResponse;
import br.mp.mpms.rotamp.v1.model.dto.UsuarioResponse;
import br.mp.mpms.rotamp.v1.service.PerfilService;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/perfis")
@RequiredArgsConstructor
@Tag(name = "Perfis e Permissões", description = "Endpoints para gerenciamento de perfis institucionais e permissoes (HU-02)")
// TODO: Reativar @PreAuthorize("hasRole('ADMINISTRADOR')") quando o Keycloak estiver integrado
// @PreAuthorize("hasRole('ADMINISTRADOR')")
public class PerfilController {

    private final PerfilService perfilService;

    @PostMapping
    @Operation(
            summary = "Cadastrar perfil",
            description = "Cria um novo perfil institucional. Exige permissao ADMINISTRADOR (`ROLE_ADMINISTRADOR`)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Perfil cadastrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PerfilResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado - Operacao restrita ao perfil Administrador",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Nome duplicado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Dados invalidos",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<PerfilResponse> cadastrar(@Valid @RequestBody CriarPerfilRequest request) {
        PerfilResponse response = perfilService.cadastrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(
            summary = "Listar perfis",
            description = "Lista perfis com filtros opcionais. Exige permissao ADMINISTRADOR (`ROLE_ADMINISTRADOR`)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada de perfis"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Page<PerfilResponse>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Boolean ativo,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(perfilService.listar(nome, ativo, pageable));
    }

    @GetMapping("/telas-disponiveis")
    @Operation(
            summary = "Listar telas do sistema",
            description = "Lista catalogo de telas. Exige permissao ADMINISTRADOR (`ROLE_ADMINISTRADOR`)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de telas"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<List<TelaResponse>> listarTelasDisponiveis() {
        return ResponseEntity.ok(perfilService.listarTelasDisponiveis());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar perfil por ID",
            description = "Retorna detalhes do perfil. Exige permissao ADMINISTRADOR (`ROLE_ADMINISTRADOR`)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Perfil nao encontrado")
    })
    public ResponseEntity<PerfilResponse> buscarPorId(@PathVariable @Parameter(description = "ID do perfil") UUID id) {
        return ResponseEntity.ok(perfilService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar dados cadastrais do perfil",
            description = "Atualiza nome e descricao do perfil. A ativacao/inativacao e feita exclusivamente via PATCH /{id}/status. Exige permissao ADMINISTRADOR (`ROLE_ADMINISTRADOR`)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil atualizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Perfil nao encontrado"),
            @ApiResponse(responseCode = "409", description = "Nome de perfil ja utilizado")
    })
    public ResponseEntity<PerfilResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarPerfilRequest request) {
        return ResponseEntity.ok(perfilService.atualizar(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Alterar status do perfil",
            description = "Ativa ou inativa o perfil. Exige permissao ADMINISTRADOR (`ROLE_ADMINISTRADOR`)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status alterado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Impossivel inativar perfil com usuarios ativos")
    })
    public ResponseEntity<PerfilResponse> alterarStatus(
            @PathVariable UUID id,
            @Valid @RequestBody AlterarStatusPerfilRequest request) {
        return ResponseEntity.ok(perfilService.alterarStatus(id, request.ativo()));
    }

    @GetMapping("/{id}/telas")
    @Operation(
            summary = "Consultar telas do perfil",
            description = "Lista telas concedidas ao perfil. Exige permissao ADMINISTRADOR (`ROLE_ADMINISTRADOR`)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Telas concedidas"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<List<TelaResponse>> consultarTelasDoPerfil(@PathVariable UUID id) {
        return ResponseEntity.ok(perfilService.consultarTelasDoPerfil(id));
    }

    @PutMapping("/{id}/permissoes")
    @Operation(
            summary = "Configurar permissoes",
            description = "Substitui telas liberadas. Exige permissao ADMINISTRADOR (`ROLE_ADMINISTRADOR`)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permissoes atualizadas"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Tela informada nao encontrada")
    })
    public ResponseEntity<PerfilResponse> configurarPermissoes(
            @PathVariable UUID id,
            @Valid @RequestBody ConfigurarPermissoesPerfilRequest request) {
        return ResponseEntity.ok(perfilService.configurarPermissoes(id, request));
    }

    @PostMapping("/{id}/usuarios/{usuarioId}")
    @Operation(
            summary = "Vincular perfil a usuario",
            description = "Atribui perfil institucional ao usuario. Exige permissao ADMINISTRADOR (`ROLE_ADMINISTRADOR`)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil vinculado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Usuario ja possui este perfil"),
            @ApiResponse(responseCode = "422", description = "Perfil inativo")
    })
    public ResponseEntity<UsuarioResponse> vincularUsuario(
            @PathVariable UUID id,
            @PathVariable UUID usuarioId) {
        return ResponseEntity.ok(perfilService.vincularUsuario(id, usuarioId));
    }

    @PutMapping("/{id}/usuarios/{usuarioId}")
    @Operation(
            summary = "Alterar perfil de usuario",
            description = "Altera perfil institucional atribuido. Exige permissao ADMINISTRADOR (`ROLE_ADMINISTRADOR`)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil alterado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Perfil inativo")
    })
    public ResponseEntity<UsuarioResponse> alterarPerfilUsuario(
            @PathVariable UUID id,
            @PathVariable UUID usuarioId) {
        return ResponseEntity.ok(perfilService.alterarPerfilUsuario(id, usuarioId));
    }

    @GetMapping("/{id}/usuarios")
    @Operation(
            summary = "Listar usuarios do perfil",
            description = "Lista paginada de usuarios do perfil. Exige permissao ADMINISTRADOR (`ROLE_ADMINISTRADOR`)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuarios vinculados"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Page<UsuarioResponse>> listarUsuariosDoPerfil(
            @PathVariable UUID id,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(perfilService.listarUsuariosDoPerfil(id, pageable));
    }
}
