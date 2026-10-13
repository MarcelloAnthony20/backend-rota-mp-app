package br.mp.mpms.rotamp.v1.service;

import br.mp.mpms.rotamp.infra.exception.BusinessException;
import br.mp.mpms.rotamp.infra.exception.ConflictException;
import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.model.dto.AtualizarLocalidadeRequest;
import br.mp.mpms.rotamp.v1.model.dto.ClassificarLocalidadeRequest;
import br.mp.mpms.rotamp.v1.model.dto.ConfigurarSigiloRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarLocalidadeRequest;
import br.mp.mpms.rotamp.v1.model.dto.LocalidadeResponse;
import br.mp.mpms.rotamp.v1.model.entity.Cidade;
import br.mp.mpms.rotamp.v1.model.entity.EnderecoLocalidade;
import br.mp.mpms.rotamp.v1.model.entity.Localidade;
import br.mp.mpms.rotamp.v1.model.entity.TipoLocalidade;
import br.mp.mpms.rotamp.v1.repository.CidadeRepository;
import br.mp.mpms.rotamp.v1.repository.LocalidadeRepository;
import br.mp.mpms.rotamp.v1.repository.TipoLocalidadeRepository;
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
            throw new BusinessException("A cidade selecionada deve estar ativa", HttpStatus.valueOf(422));
        }

        TipoLocalidade tipo = tipoLocalidadeRepository.findById(request.tipoLocalidadeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de localidade nao encontrado"));
        if (!tipo.isAtivo()) {
            throw new BusinessException("O tipo de localidade selecionado deve estar ativo", HttpStatus.valueOf(422));
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
                        HttpStatus.valueOf(422));
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
                            HttpStatus.valueOf(422));
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
            throw new BusinessException("A cidade vinculada deve estar ativa", HttpStatus.valueOf(422));
        }

        TipoLocalidade tipo = tipoLocalidadeRepository.findById(request.tipoLocalidadeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de localidade nao encontrado"));
        if (!tipo.isAtivo()) {
            throw new BusinessException("O tipo de localidade informado deve estar ativo", HttpStatus.valueOf(422));
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
                        HttpStatus.valueOf(422));
            }
        }

        if (request.cep() != null && !request.cep().isBlank()) {
            CepService.EnderecoCep cepInfo = cepService.consultarCep(request.cep());
            String logradouro = cepInfo.logradouro() != null ? cepInfo.logradouro() : request.logradouro();
            String bairro = cepInfo.bairro() != null ? cepInfo.bairro() : request.bairro();

            if (!sigiloso && (logradouro == null || logradouro.isBlank() || bairro == null || bairro.isBlank())) {
                throw new BusinessException(
                        "Logradouro e bairro sao obrigatorios para o endereco",
                        HttpStatus.valueOf(422));
            }

            EnderecoLocalidade endereco = localidade.getEndereco();
            if (endereco == null) {
                endereco = EnderecoLocalidade.builder().build();
                localidade.atribuirEndereco(endereco);
            }
            endereco.atualizarDados(
                logradouro,
                request.numero(),
                request.cep().replaceAll("\\D", ""),
                bairro,
                request.complemento(),
                cidade
            );
        }

        localidade.atualizarDados(tipo, cidade, request.nome(), sigiloso);
        Localidade salva = localidadeRepository.save(localidade);
        return toResponse(salva);
    }

    @Transactional
    public void inativar(UUID id) {
        Localidade localidade = localidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Localidade nao encontrada"));
        localidade.inativar();
        localidadeRepository.save(localidade);
    }

    @Transactional
    public LocalidadeResponse ativar(UUID id) {
        Localidade localidade = localidadeRepository.findByIdComDetalhes(id)
                .orElseThrow(() -> new ResourceNotFoundException("Localidade nao encontrada"));

        if (!localidade.getFkCidade().isAtivo()) {
            throw new BusinessException(
                    "Nao e possivel ativar a localidade: a cidade vinculada esta inativa",
                    HttpStatus.valueOf(422));
        }
        if (!localidade.getFkTipoLocalidade().isAtivo()) {
            throw new BusinessException(
                    "Nao e possivel ativar a localidade: o tipo de localidade vinculado esta inativo",
                    HttpStatus.valueOf(422));
        }

        localidade.ativar();
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
                    HttpStatus.valueOf(422));
        }

        localidade.atualizarDados(tipo, localidade.getFkCidade(), localidade.getNome(), localidade.isLocalSigiloso());
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
                    HttpStatus.valueOf(422));
        }

        localidade.atualizarDados(localidade.getFkTipoLocalidade(), localidade.getFkCidade(), localidade.getNome(), novoSigilo);
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
        // =========================================================================
        // O QUE OCULTA: Em ambiente pré-Keycloak (sem token / anônimo), retornava false,
        // acionando a substituição dos dados reais por [SIGILOSO] e *** em toResponse().
        // TODO: Reativar restrição pós-Keycloak (retornar false para usuário não autenticado):
        // if (auth == null || !auth.isAuthenticated()) {
        //     return false;
        // }
        // =========================================================================
        // Pré-Keycloak: Exibe todas as informações reais de endereço, mostrando apenas a tag de sigiloso
        if (auth == null || !auth.isAuthenticated()
                || auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken
                || "anonymousUser".equalsIgnoreCase(auth.getName())
                || auth.getAuthorities().stream().anyMatch(a -> "ROLE_ANONYMOUS".equals(a.getAuthority()))) {
            return true;
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
