package br.mp.mpms.exemploservico.v1.service;

import br.mp.mpms.exemploservico.infra.exception.BusinessException;
import br.mp.mpms.exemploservico.infra.exception.ConflictException;
import br.mp.mpms.exemploservico.infra.exception.ResourceNotFoundException;
import br.mp.mpms.exemploservico.v1.model.dto.AtualizarLocalidadeRequest;
import br.mp.mpms.exemploservico.v1.model.dto.ClassificarLocalidadeRequest;
import br.mp.mpms.exemploservico.v1.model.dto.ConfigurarSigiloRequest;
import br.mp.mpms.exemploservico.v1.model.dto.CriarLocalidadeRequest;
import br.mp.mpms.exemploservico.v1.model.dto.LocalidadeResponse;
import br.mp.mpms.exemploservico.v1.model.entity.Cidade;
import br.mp.mpms.exemploservico.v1.model.entity.EnderecoLocalidade;
import br.mp.mpms.exemploservico.v1.model.entity.Localidade;
import br.mp.mpms.exemploservico.v1.model.entity.TipoLocalidade;
import br.mp.mpms.exemploservico.v1.repository.CidadeRepository;
import br.mp.mpms.exemploservico.v1.repository.LocalidadeRepository;
import br.mp.mpms.exemploservico.v1.repository.TipoLocalidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalidadeService {

    private final LocalidadeRepository localidadeRepository;
    private final CidadeRepository cidadeRepository;
    private final TipoLocalidadeRepository tipoLocalidadeRepository;
    private final CepService cepService;

    @Transactional
    public LocalidadeResponse cadastrar(CriarLocalidadeRequest request) {
        Cidade cidade = cidadeRepository.findById(request.cidadeId())
                .orElseThrow(() -> new ResourceNotFoundException("Cidade nao encontrada"));
        if (!cidade.isAtivo()) {
            throw new BusinessException("A cidade selecionada deve estar ativa", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        TipoLocalidade tipo = tipoLocalidadeRepository.findById(request.tipoLocalidadeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de localidade nao encontrado"));
        if (!tipo.isAtivo()) {
            throw new BusinessException("O tipo de localidade selecionado deve estar ativo", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (localidadeRepository.existsByFkCidadeIdAndFkTipoLocalidadeIdAndNomeIgnoreCase(
                request.cidadeId(), request.tipoLocalidadeId(), request.nome())) {
            throw new ConflictException("Ja existe uma localidade cadastrada com este nome para a mesma cidade e tipo");
        }

        boolean sigiloso = Boolean.TRUE.equals(request.localSigiloso());
        EnderecoLocalidade endereco = null;

        if (!sigiloso) {
            if (request.cep() == null || request.cep().isBlank()
                    || request.numero() == null || request.numero().isBlank()) {
                throw new BusinessException(
                        "Endereco completo e obrigatorio para localidades nao sigilosas",
                        HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        if (request.cep() != null && !request.cep().isBlank()) {
            CepService.EnderecoCep cepInfo = cepService.consultarCep(request.cep());
            String logradouro = cepInfo.logradouro() != null ? cepInfo.logradouro() : request.logradouro();
            String bairro = cepInfo.bairro() != null ? cepInfo.bairro() : request.bairro();

            if (!sigiloso) {
                if (logradouro == null || logradouro.isBlank() || bairro == null || bairro.isBlank()) {
                    throw new BusinessException(
                            "Logradouro e bairro sao obrigatorios para o endereco",
                            HttpStatus.UNPROCESSABLE_ENTITY);
                }
            }

            endereco = EnderecoLocalidade.builder()
                    .cep(request.cep().replaceAll("\\D", ""))
                    .logradouro(logradouro)
                    .numero(request.numero())
                    .bairro(bairro)
                    .complemento(request.complemento())
                    .fkCidade(cidade)
                    .build();
        }

        Localidade localidade = Localidade.builder()
                .nome(request.nome())
                .fkCidade(cidade)
                .fkTipoLocalidade(tipo)
                .localSigiloso(sigiloso)
                .ativo(request.ativo() != null ? request.ativo() : true)
                .build();

        if (endereco != null) {
            localidade.atribuirEndereco(endereco);
        }

        Localidade salva = localidadeRepository.save(localidade);
        return toResponse(salva);
    }

    @Transactional
    public LocalidadeResponse alterar(UUID id, AtualizarLocalidadeRequest request) {
        Localidade localidade = localidadeRepository.findByIdComDetalhes(id)
                .orElseThrow(() -> new ResourceNotFoundException("Localidade nao encontrada"));

        Cidade cidade = cidadeRepository.findById(request.cidadeId())
                .orElseThrow(() -> new ResourceNotFoundException("Cidade nao encontrada"));
        if (!cidade.isAtivo()) {
            throw new BusinessException("A cidade vinculada deve estar ativa", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        TipoLocalidade tipo = tipoLocalidadeRepository.findById(request.tipoLocalidadeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de localidade nao encontrado"));
        if (!tipo.isAtivo()) {
            throw new BusinessException("O tipo de localidade informado deve estar ativo", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (localidadeRepository.existsByFkCidadeIdAndFkTipoLocalidadeIdAndNomeIgnoreCaseAndIdNot(
                request.cidadeId(), request.tipoLocalidadeId(), request.nome(), id)) {
            throw new ConflictException("Ja existe outra localidade com este nome para a mesma cidade e tipo");
        }

        boolean sigiloso = Boolean.TRUE.equals(request.localSigiloso());
        if (!sigiloso) {
            if (request.cep() == null || request.cep().isBlank()
                    || request.numero() == null || request.numero().isBlank()) {
                throw new BusinessException(
                        "Endereco completo e obrigatorio para localidades nao sigilosas",
                        HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        if (request.cep() != null && !request.cep().isBlank()) {
            CepService.EnderecoCep cepInfo = cepService.consultarCep(request.cep());
            String logradouro = cepInfo.logradouro() != null ? cepInfo.logradouro() : request.logradouro();
            String bairro = cepInfo.bairro() != null ? cepInfo.bairro() : request.bairro();

            if (!sigiloso && (logradouro == null || logradouro.isBlank() || bairro == null || bairro.isBlank())) {
                throw new BusinessException(
                        "Logradouro e bairro sao obrigatorios para o endereco",
                        HttpStatus.UNPROCESSABLE_ENTITY);
            }

            EnderecoLocalidade endereco = localidade.getEndereco();
            if (endereco == null) {
                endereco = EnderecoLocalidade.builder().build();
                localidade.atribuirEndereco(endereco);
            }
            endereco.setCep(request.cep().replaceAll("\\D", ""));
            endereco.setLogradouro(logradouro);
            endereco.setNumero(request.numero());
            endereco.setBairro(bairro);
            endereco.setComplemento(request.complemento());
            endereco.setFkCidade(cidade);
        }

        localidade.setNome(request.nome());
        localidade.setFkCidade(cidade);
        localidade.setFkTipoLocalidade(tipo);
        localidade.setLocalSigiloso(sigiloso);
        if (request.ativo() != null) {
            localidade.setAtivo(request.ativo());
        }

        Localidade salva = localidadeRepository.save(localidade);
        return toResponse(salva);
    }

    @Transactional
    public void inativar(UUID id) {
        Localidade localidade = localidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Localidade nao encontrada"));
        localidade.setAtivo(false);
        localidadeRepository.save(localidade);
    }

    @Transactional
    public LocalidadeResponse ativar(UUID id) {
        Localidade localidade = localidadeRepository.findByIdComDetalhes(id)
                .orElseThrow(() -> new ResourceNotFoundException("Localidade nao encontrada"));

        if (!localidade.getFkCidade().isAtivo()) {
            throw new BusinessException(
                    "Nao e possivel ativar a localidade: a cidade vinculada esta inativa",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!localidade.getFkTipoLocalidade().isAtivo()) {
            throw new BusinessException(
                    "Nao e possivel ativar a localidade: o tipo de localidade vinculado esta inativo",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        localidade.setAtivo(true);
        Localidade salva = localidadeRepository.save(localidade);
        return toResponse(salva);
    }

    @Transactional
    public LocalidadeResponse classificar(UUID id, ClassificarLocalidadeRequest request) {
        Localidade localidade = localidadeRepository.findByIdComDetalhes(id)
                .orElseThrow(() -> new ResourceNotFoundException("Localidade nao encontrada"));

        TipoLocalidade tipo = tipoLocalidadeRepository.findById(request.tipoLocalidadeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de localidade nao encontrado"));
        if (!tipo.isAtivo()) {
            throw new BusinessException(
                    "O tipo de localidade informado deve estar ativo",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        localidade.setFkTipoLocalidade(tipo);
        Localidade salva = localidadeRepository.save(localidade);
        return toResponse(salva);
    }

    @Transactional
    public LocalidadeResponse configurarSigilo(UUID id, ConfigurarSigiloRequest request) {
        Localidade localidade = localidadeRepository.findByIdComDetalhes(id)
                .orElseThrow(() -> new ResourceNotFoundException("Localidade nao encontrada"));

        boolean novoSigilo = Boolean.TRUE.equals(request.localSigiloso());
        if (!novoSigilo && localidade.getEndereco() == null) {
            throw new BusinessException(
                    "Nao e possivel remover o sigilo: a localidade nao possui endereco cadastrado",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        localidade.setLocalSigiloso(novoSigilo);
        Localidade salva = localidadeRepository.save(localidade);
        return toResponse(salva);
    }

    @Transactional(readOnly = true)
    public LocalidadeResponse buscarPorId(UUID id) {
        Localidade localidade = localidadeRepository.findByIdComDetalhes(id)
                .orElseThrow(() -> new ResourceNotFoundException("Localidade nao encontrada"));
        return toResponse(localidade);
    }

    @Transactional(readOnly = true)
    public Page<LocalidadeResponse> listar(Pageable pageable) {
        return localidadeRepository.findAllComDetalhes(pageable).map(this::toResponse);
    }

    private boolean temPermissaoSigilo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> "LOCALIDADE_SIGILO_READ".equals(a.getAuthority())
                        || "ROLE_LOCALIDADE_SIGILO_READ".equals(a.getAuthority())
                        || "ROLE_GESTOR_SEGURANCA".equals(a.getAuthority()));
    }

    private LocalidadeResponse toResponse(Localidade localidade) {
        boolean podeVerSigilo = !localidade.isLocalSigiloso() || temPermissaoSigilo();
        EnderecoLocalidade end = localidade.getEndereco();

        String cep = null;
        String logradouro = null;
        String numero = null;
        String bairro = null;
        String complemento = null;

        if (end != null) {
            if (podeVerSigilo) {
                cep = end.getCep();
                logradouro = end.getLogradouro();
                numero = end.getNumero();
                bairro = end.getBairro();
                complemento = end.getComplemento();
            } else {
                cep = "***";
                logradouro = "[SIGILOSO]";
                numero = "***";
                bairro = "[SIGILOSO]";
                complemento = end.getComplemento() != null ? "[SIGILOSO]" : null;
            }
        }

        return new LocalidadeResponse(
                localidade.getId(),
                localidade.getFkTipoLocalidade() != null ? localidade.getFkTipoLocalidade().getId() : null,
                localidade.getFkTipoLocalidade() != null ? localidade.getFkTipoLocalidade().getTipoLocalidade() : null,
                localidade.getFkCidade() != null ? localidade.getFkCidade().getId() : null,
                localidade.getFkCidade() != null ? localidade.getFkCidade().getNome() : null,
                localidade.getNome(),
                localidade.isLocalSigiloso(),
                localidade.isAtivo(),
                cep,
                logradouro,
                numero,
                bairro,
                complemento
        );
    }
}