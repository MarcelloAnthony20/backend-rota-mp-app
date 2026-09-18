package br.mp.mpms.exemploservico.v1.controller;

import br.mp.mpms.exemploservico.v1.model.dto.AtualizarCidadeRequest;
import br.mp.mpms.exemploservico.v1.model.dto.CidadeResponse;
import br.mp.mpms.exemploservico.v1.model.dto.CriarCidadeRequest;
import br.mp.mpms.exemploservico.v1.service.CidadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
public class CidadeController {

    private final CidadeService cidadeService;

    @PostMapping
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<CidadeResponse> cadastrar(@Valid @RequestBody CriarCidadeRequest request) {
        CidadeResponse response = cidadeService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CidadeResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(cidadeService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<CidadeResponse>> listar(
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 20) Pageable pageable) {
        if (nome != null && !nome.isBlank()) {
            return ResponseEntity.ok(cidadeService.buscarPorNome(nome, pageable));
        }
        return ResponseEntity.ok(cidadeService.listar(pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<CidadeResponse> alterar(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarCidadeRequest request) {
        return ResponseEntity.ok(cidadeService.alterar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Void> inativar(@PathVariable UUID id) {
        cidadeService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<CidadeResponse> ativar(@PathVariable UUID id) {
        return ResponseEntity.ok(cidadeService.ativar(id));
    }
}
