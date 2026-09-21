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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalidadeServiceTest {

    @Mock
    private LocalidadeRepository localidadeRepository;

    @Mock
    private CidadeRepository cidadeRepository;

    @Mock
    private TipoLocalidadeRepository tipoLocalidadeRepository;

    @Mock
    private CepService cepService;

    @InjectMocks
    private LocalidadeService localidadeService;

    private UUID cidadeId;
    private UUID tipoId;
    private Cidade cidadeAtiva;
    private TipoLocalidade tipoAtivo;

    @BeforeEach
    void setUp() {
        cidadeId = UUID.fromString("c1d00001-0000-0000-0000-000000000001");
        tipoId = UUID.fromString("b1000001-0000-0000-0000-000000000001");

        cidadeAtiva = Cidade.builder()
                .id(cidadeId)
                .nome("Campo Grande")
                .codigo("5002704")
                .ativo(true)
                .build();

        tipoAtivo = TipoLocalidade.builder()
                .id(tipoId)
                .codigo("PROJUS")
                .tipoLocalidade("Promotoria de Justiça")
                .ativo(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // --- CADASTRO ---

    @Test
    @DisplayName("deve cadastrar localidade completa com sucesso (RN-8 e sanitizacao de CEP)")
    void deve_cadastrarLocalidadeCompletaComSucesso() {
        CriarLocalidadeRequest request = new CriarLocalidadeRequest(
                "Promotoria da Cidadania",
                cidadeId,
                tipoId,
                "79002-000",
                "Rua Divergente",
                "140",
                "Bairro Divergente",
                "Edifício Sede",
                false,
                true
        );

        when(cidadeRepository.findById(cidadeId)).thenReturn(Optional.of(cidadeAtiva));
        when(tipoLocalidadeRepository.findById(tipoId)).thenReturn(Optional.of(tipoAtivo));
        when(localidadeRepository.existsByFkCidadeIdAndFkTipoLocalidadeIdAndNomeIgnoreCase(cidadeId, tipoId, "Promotoria da Cidadania"))
                .thenReturn(false);
        when(cepService.consultarCep("79002-000"))
                .thenReturn(new CepService.EnderecoCep("79002000", "Rua da Paz", "Jardim dos Estados"));

        UUID locId = UUID.randomUUID();
        when(localidadeRepository.save(any(Localidade.class))).thenAnswer(inv -> {
            Localidade l = inv.getArgument(0);
            l.setId(locId);
            if (l.getEndereco() != null) {
                l.getEndereco().setId(UUID.randomUUID());
            }
            return l;
        });

        LocalidadeResponse response = localidadeService.cadastrar(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(locId);
        assertThat(response.nome()).isEqualTo("Promotoria da Cidadania");
        assertThat(response.cep()).isEqualTo("79002000"); // sanitizado com 8 dígitos
        assertThat(response.logradouro()).isEqualTo("Rua da Paz"); // auto-preenchido via CepService (RN-8)
        assertThat(response.bairro()).isEqualTo("Jardim dos Estados"); // auto-preenchido via CepService (RN-8)
        assertThat(response.localSigiloso()).isFalse();
        assertThat(response.ativo()).isTrue();
    }

    @Test
    @DisplayName("deve cadastrar localidade sigilosa com sucesso sem endereco obrigatorio (RN-7)")
    void deve_cadastrarLocalidadeSigilosaSemEndereco() {
        CriarLocalidadeRequest request = new CriarLocalidadeRequest(
                "Base Sigilosa",
                cidadeId,
                tipoId,
                null,
                null,
                null,
                null,
                null,
                true,
                true
        );

        when(cidadeRepository.findById(cidadeId)).thenReturn(Optional.of(cidadeAtiva));
        when(tipoLocalidadeRepository.findById(tipoId)).thenReturn(Optional.of(tipoAtivo));
        when(localidadeRepository.existsByFkCidadeIdAndFkTipoLocalidadeIdAndNomeIgnoreCase(cidadeId, tipoId, "Base Sigilosa"))
                .thenReturn(false);

        UUID locId = UUID.randomUUID();
        when(localidadeRepository.save(any(Localidade.class))).thenAnswer(inv -> {
            Localidade l = inv.getArgument(0);
            l.setId(locId);
            return l;
        });

        LocalidadeResponse response = localidadeService.cadastrar(request);

        assertThat(response.id()).isEqualTo(locId);
        assertThat(response.localSigiloso()).isTrue();
        assertThat(response.cep()).isNull();
    }

    @Test
    @DisplayName("deve bloquear cadastro quando cidade for inexistente (404)")
    void deve_bloquearCadastro_quandoCidadeInexistente() {
        CriarLocalidadeRequest request = new CriarLocalidadeRequest(
                "Promotoria", cidadeId, tipoId, "79002-000", "Rua da Paz", "140", "Centro", null, false, true);

        when(cidadeRepository.findById(cidadeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> localidadeService.cadastrar(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cidade nao encontrada");
    }

    @Test
    @DisplayName("deve bloquear cadastro quando cidade for inativa (422)")
    void deve_bloquearCadastro_quandoCidadeInativa() {
        cidadeAtiva.setAtivo(false);
        CriarLocalidadeRequest request = new CriarLocalidadeRequest(
                "Promotoria", cidadeId, tipoId, "79002-000", "Rua da Paz", "140", "Centro", null, false, true);

        when(cidadeRepository.findById(cidadeId)).thenReturn(Optional.of(cidadeAtiva));

        assertThatThrownBy(() -> localidadeService.cadastrar(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @Test
    @DisplayName("deve bloquear cadastro quando tipo de localidade for inexistente (404)")
    void deve_bloquearCadastro_quandoTipoInexistente() {
        CriarLocalidadeRequest request = new CriarLocalidadeRequest(
                "Promotoria", cidadeId, tipoId, "79002-000", "Rua da Paz", "140", "Centro", null, false, true);

        when(cidadeRepository.findById(cidadeId)).thenReturn(Optional.of(cidadeAtiva));
        when(tipoLocalidadeRepository.findById(tipoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> localidadeService.cadastrar(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tipo de localidade nao encontrado");
    }

    @Test
    @DisplayName("deve bloquear cadastro quando tipo de localidade for inativo (422)")
    void deve_bloquearCadastro_quandoTipoInativo() {
        tipoAtivo.setAtivo(false);
        CriarLocalidadeRequest request = new CriarLocalidadeRequest(
                "Promotoria", cidadeId, tipoId, "79002-000", "Rua da Paz", "140", "Centro", null, false, true);

        when(cidadeRepository.findById(cidadeId)).thenReturn(Optional.of(cidadeAtiva));
        when(tipoLocalidadeRepository.findById(tipoId)).thenReturn(Optional.of(tipoAtivo));

        assertThatThrownBy(() -> localidadeService.cadastrar(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @Test
    @DisplayName("deve lancar ConflictException 409 quando localidade for duplicada (RN-6)")
    void deve_lancarConflict_quandoLocalidadeDuplicadaRN6() {
        CriarLocalidadeRequest request = new CriarLocalidadeRequest(
                "Promotoria da Cidadania", cidadeId, tipoId, "79002-000", "Rua da Paz", "140", "Centro", null, false, true);

        when(cidadeRepository.findById(cidadeId)).thenReturn(Optional.of(cidadeAtiva));
        when(tipoLocalidadeRepository.findById(tipoId)).thenReturn(Optional.of(tipoAtivo));
        when(localidadeRepository.existsByFkCidadeIdAndFkTipoLocalidadeIdAndNomeIgnoreCase(cidadeId, tipoId, "Promotoria da Cidadania"))
                .thenReturn(true);

        assertThatThrownBy(() -> localidadeService.cadastrar(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Ja existe uma localidade cadastrada com este nome");
    }

    @Test
    @DisplayName("deve bloquear cadastro de localidade nao sigilosa sem endereco (RN-7)")
    void deve_bloquearCadastro_quandoNaoSigilosaSemEnderecoRN7() {
        CriarLocalidadeRequest request = new CriarLocalidadeRequest(
                "Promotoria Sem Endereco", cidadeId, tipoId, null, null, null, null, null, false, true);

        when(cidadeRepository.findById(cidadeId)).thenReturn(Optional.of(cidadeAtiva));
        when(tipoLocalidadeRepository.findById(tipoId)).thenReturn(Optional.of(tipoAtivo));
        when(localidadeRepository.existsByFkCidadeIdAndFkTipoLocalidadeIdAndNomeIgnoreCase(any(), any(), any()))
                .thenReturn(false);

        assertThatThrownBy(() -> localidadeService.cadastrar(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    // --- SIGILO RN-2 ---

    @Test
    @DisplayName("deve mascarar endereco quando localidade for sigilosa e usuario nao possuir permissao (RN-2)")
    void deve_mascararEndereco_quandoSigilosaESemPermissaoRN2() {
        UUID locId = UUID.randomUUID();
        Localidade localidade = Localidade.builder()
                .id(locId)
                .nome("Base Sigilosa")
                .fkCidade(cidadeAtiva)
                .fkTipoLocalidade(tipoAtivo)
                .localSigiloso(true)
                .ativo(true)
                .build();
        EnderecoLocalidade endereco = EnderecoLocalidade.builder()
                .id(UUID.randomUUID())
                .cep("79002000")
                .logradouro("Rua da Paz")
                .numero("500")
                .bairro("Jardim dos Estados")
                .complemento("Anexo Reservado")
                .fkCidade(cidadeAtiva)
                .build();
        localidade.atribuirEndereco(endereco);

        when(localidadeRepository.findByIdComDetalhes(locId)).thenReturn(Optional.of(localidade));

        // Usuario autenticado como OPERADOR sem autorizacao de sigilo
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("operador", "pass", List.of(new SimpleGrantedAuthority("ROLE_OPERADOR")))
        );

        LocalidadeResponse response = localidadeService.buscarPorId(locId);

        assertThat(response.localSigiloso()).isTrue();
        assertThat(response.cep()).isEqualTo("***");
        assertThat(response.logradouro()).isEqualTo("[SIGILOSO]");
        assertThat(response.numero()).isEqualTo("***");
        assertThat(response.bairro()).isEqualTo("[SIGILOSO]");
        assertThat(response.complemento()).isEqualTo("[SIGILOSO]");
    }

    @Test
    @DisplayName("deve exibir endereco completo quando usuario tiver permissao LOCALIDADE_SIGILO_READ (RN-2)")
    void deve_exibirEnderecoCompleto_quandoUsuarioPossuiPermissaoSigiloRN2() {
        UUID locId = UUID.randomUUID();
        Localidade localidade = Localidade.builder()
                .id(locId)
                .nome("Base Sigilosa")
                .fkCidade(cidadeAtiva)
                .fkTipoLocalidade(tipoAtivo)
                .localSigiloso(true)
                .ativo(true)
                .build();
        EnderecoLocalidade endereco = EnderecoLocalidade.builder()
                .id(UUID.randomUUID())
                .cep("79002000")
                .logradouro("Rua da Paz")
                .numero("500")
                .bairro("Jardim dos Estados")
                .complemento("Anexo Reservado")
                .fkCidade(cidadeAtiva)
                .build();
        localidade.atribuirEndereco(endereco);

        when(localidadeRepository.findByIdComDetalhes(locId)).thenReturn(Optional.of(localidade));

        // Usuario autenticado com LOCALIDADE_SIGILO_READ
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("gestor_seg", "pass", List.of(new SimpleGrantedAuthority("LOCALIDADE_SIGILO_READ")))
        );

        LocalidadeResponse response = localidadeService.buscarPorId(locId);

        assertThat(response.localSigiloso()).isTrue();
        assertThat(response.cep()).isEqualTo("79002000");
        assertThat(response.logradouro()).isEqualTo("Rua da Paz");
        assertThat(response.numero()).isEqualTo("500");
        assertThat(response.bairro()).isEqualTo("Jardim dos Estados");
        assertThat(response.complemento()).isEqualTo("Anexo Reservado");
    }

    // --- CONSULTA E LISTAGEM ---

    @Test
    @DisplayName("deve buscar localidade por ID com sucesso")
    void deve_buscarPorIdComSucesso() {
        UUID locId = UUID.randomUUID();
        Localidade localidade = Localidade.builder()
                .id(locId)
                .nome("Promotoria")
                .fkCidade(cidadeAtiva)
                .fkTipoLocalidade(tipoAtivo)
                .localSigiloso(false)
                .ativo(true)
                .build();

        when(localidadeRepository.findByIdComDetalhes(locId)).thenReturn(Optional.of(localidade));

        LocalidadeResponse response = localidadeService.buscarPorId(locId);

        assertThat(response.id()).isEqualTo(locId);
        assertThat(response.nome()).isEqualTo("Promotoria");
    }

    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando buscar localidade por ID inexistente")
    void deve_lancarNotFound_quandoBuscarInexistente() {
        UUID id = UUID.randomUUID();
        when(localidadeRepository.findByIdComDetalhes(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> localidadeService.buscarPorId(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Localidade nao encontrada");
    }

    @Test
    @DisplayName("deve listar localidades paginadas")
    void deve_listarLocalidadesPaginadas() {
        Pageable pageable = PageRequest.of(0, 10);
        Localidade loc = Localidade.builder()
                .id(UUID.randomUUID())
                .nome("Loc 1")
                .fkCidade(cidadeAtiva)
                .fkTipoLocalidade(tipoAtivo)
                .localSigiloso(false)
                .ativo(true)
                .build();

        when(localidadeRepository.findAllComDetalhes(pageable)).thenReturn(new PageImpl<>(List.of(loc)));

        Page<LocalidadeResponse> page = localidadeService.listar(pageable);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).nome()).isEqualTo("Loc 1");
    }

    // --- ALTERAÇÃO ---

    @Test
    @DisplayName("deve alterar localidade com sucesso")
    void deve_alterarComSucesso() {
        UUID locId = UUID.randomUUID();
        Localidade existente = Localidade.builder()
                .id(locId)
                .nome("Nome Antigo")
                .fkCidade(cidadeAtiva)
                .fkTipoLocalidade(tipoAtivo)
                .localSigiloso(false)
                .ativo(true)
                .build();

        AtualizarLocalidadeRequest request = new AtualizarLocalidadeRequest(
                "Nome Novo",
                cidadeId,
                tipoId,
                "79004-311",
                "Rua Ceará",
                "800",
                "Santa Fé",
                "Bloco B",
                false,
                true
        );

        when(localidadeRepository.findByIdComDetalhes(locId)).thenReturn(Optional.of(existente));
        when(cidadeRepository.findById(cidadeId)).thenReturn(Optional.of(cidadeAtiva));
        when(tipoLocalidadeRepository.findById(tipoId)).thenReturn(Optional.of(tipoAtivo));
        when(localidadeRepository.existsByFkCidadeIdAndFkTipoLocalidadeIdAndNomeIgnoreCaseAndIdNot(cidadeId, tipoId, "Nome Novo", locId))
                .thenReturn(false);
        when(cepService.consultarCep("79004-311"))
                .thenReturn(new CepService.EnderecoCep("79004311", "Rua Ceará", "Santa Fé"));
        when(localidadeRepository.save(any(Localidade.class))).thenAnswer(inv -> inv.getArgument(0));

        LocalidadeResponse response = localidadeService.alterar(locId, request);

        assertThat(response.nome()).isEqualTo("Nome Novo");
        assertThat(response.cep()).isEqualTo("79004311");
        assertThat(response.numero()).isEqualTo("800");
    }

    @Test
    @DisplayName("deve bloquear alteracao quando cidade for inativa (422)")
    void deve_bloquearAlteracao_quandoCidadeInativa() {
        UUID locId = UUID.randomUUID();
        Localidade existente = Localidade.builder().id(locId).build();
        cidadeAtiva.setAtivo(false);

        AtualizarLocalidadeRequest request = new AtualizarLocalidadeRequest(
                "Nome Novo", cidadeId, tipoId, "79002-000", "Rua da Paz", "140", "Centro", null, false, true);

        when(localidadeRepository.findByIdComDetalhes(locId)).thenReturn(Optional.of(existente));
        when(cidadeRepository.findById(cidadeId)).thenReturn(Optional.of(cidadeAtiva));

        assertThatThrownBy(() -> localidadeService.alterar(locId, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @Test
    @DisplayName("deve bloquear alteracao quando tipo de localidade for inativo (422)")
    void deve_bloquearAlteracao_quandoTipoInativo() {
        UUID locId = UUID.randomUUID();
        Localidade existente = Localidade.builder().id(locId).build();
        tipoAtivo.setAtivo(false);

        AtualizarLocalidadeRequest request = new AtualizarLocalidadeRequest(
                "Nome Novo", cidadeId, tipoId, "79002-000", "Rua da Paz", "140", "Centro", null, false, true);

        when(localidadeRepository.findByIdComDetalhes(locId)).thenReturn(Optional.of(existente));
        when(cidadeRepository.findById(cidadeId)).thenReturn(Optional.of(cidadeAtiva));
        when(tipoLocalidadeRepository.findById(tipoId)).thenReturn(Optional.of(tipoAtivo));

        assertThatThrownBy(() -> localidadeService.alterar(locId, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @Test
    @DisplayName("deve lancar ConflictException quando alterar para nome duplicado na mesma cidade e tipo (RN-6)")
    void deve_lancarConflict_quandoAlterarParaNomeDuplicado() {
        UUID locId = UUID.randomUUID();
        Localidade existente = Localidade.builder().id(locId).build();

        AtualizarLocalidadeRequest request = new AtualizarLocalidadeRequest(
                "Nome Duplicado", cidadeId, tipoId, "79002-000", "Rua da Paz", "140", "Centro", null, false, true);

        when(localidadeRepository.findByIdComDetalhes(locId)).thenReturn(Optional.of(existente));
        when(cidadeRepository.findById(cidadeId)).thenReturn(Optional.of(cidadeAtiva));
        when(tipoLocalidadeRepository.findById(tipoId)).thenReturn(Optional.of(tipoAtivo));
        when(localidadeRepository.existsByFkCidadeIdAndFkTipoLocalidadeIdAndNomeIgnoreCaseAndIdNot(cidadeId, tipoId, "Nome Duplicado", locId))
                .thenReturn(true);

        assertThatThrownBy(() -> localidadeService.alterar(locId, request))
                .isInstanceOf(ConflictException.class);
    }

    // --- INATIVAÇÃO (SOFT DELETE) & REATIVAÇÃO ---

    @Test
    @DisplayName("deve inativar localidade com soft delete idempotente")
    void deve_inativarComSoftDelete() {
        UUID locId = UUID.randomUUID();
        Localidade loc = Localidade.builder().id(locId).ativo(true).build();

        when(localidadeRepository.findById(locId)).thenReturn(Optional.of(loc));

        localidadeService.inativar(locId);

        assertThat(loc.isAtivo()).isFalse();
        verify(localidadeRepository).save(loc);
    }

    @Test
    @DisplayName("deve reativar localidade quando cidade e tipo vinculados estiverem ativos")
    void deve_reativarLocalidade_quandoCidadeETipoAtivos() {
        UUID locId = UUID.randomUUID();
        Localidade loc = Localidade.builder()
                .id(locId)
                .nome("Promotoria")
                .fkCidade(cidadeAtiva)
                .fkTipoLocalidade(tipoAtivo)
                .ativo(false)
                .build();

        when(localidadeRepository.findByIdComDetalhes(locId)).thenReturn(Optional.of(loc));
        when(localidadeRepository.save(any(Localidade.class))).thenAnswer(inv -> inv.getArgument(0));

        LocalidadeResponse response = localidadeService.ativar(locId);

        assertThat(response.ativo()).isTrue();
        verify(localidadeRepository).save(loc);
    }

    @Test
    @DisplayName("deve bloquear reativacao quando cidade vinculada estiver inativa (422)")
    void deve_bloquearReativacao_quandoCidadeInativa() {
        UUID locId = UUID.randomUUID();
        cidadeAtiva.setAtivo(false);
        Localidade loc = Localidade.builder()
                .id(locId)
                .fkCidade(cidadeAtiva)
                .fkTipoLocalidade(tipoAtivo)
                .ativo(false)
                .build();

        when(localidadeRepository.findByIdComDetalhes(locId)).thenReturn(Optional.of(loc));

        assertThatThrownBy(() -> localidadeService.ativar(locId))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @Test
    @DisplayName("deve bloquear reativacao quando tipo vinculado estiver inativo (422)")
    void deve_bloquearReativacao_quandoTipoInativo() {
        UUID locId = UUID.randomUUID();
        tipoAtivo.setAtivo(false);
        Localidade loc = Localidade.builder()
                .id(locId)
                .fkCidade(cidadeAtiva)
                .fkTipoLocalidade(tipoAtivo)
                .ativo(false)
                .build();

        when(localidadeRepository.findByIdComDetalhes(locId)).thenReturn(Optional.of(loc));

        assertThatThrownBy(() -> localidadeService.ativar(locId))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    // --- CLASSIFICAÇÃO & CONFIGURAÇÃO DE SIGILO ---

    @Test
    @DisplayName("deve reclassificar tipo da localidade com sucesso quando tipo estiver ativo")
    void deve_classificarLocalidadeComSucesso() {
        UUID locId = UUID.randomUUID();
        UUID novoTipoId = UUID.randomUUID();
        TipoLocalidade novoTipo = TipoLocalidade.builder().id(novoTipoId).tipoLocalidade("Sede").ativo(true).build();
        Localidade loc = Localidade.builder().id(locId).fkCidade(cidadeAtiva).fkTipoLocalidade(tipoAtivo).build();

        when(localidadeRepository.findByIdComDetalhes(locId)).thenReturn(Optional.of(loc));
        when(tipoLocalidadeRepository.findById(novoTipoId)).thenReturn(Optional.of(novoTipo));
        when(localidadeRepository.save(any(Localidade.class))).thenAnswer(inv -> inv.getArgument(0));

        LocalidadeResponse response = localidadeService.classificar(locId, new ClassificarLocalidadeRequest(novoTipoId));

        assertThat(response.tipoLocalidadeId()).isEqualTo(novoTipoId);
        assertThat(loc.getFkTipoLocalidade()).isEqualTo(novoTipo);
    }

    @Test
    @DisplayName("deve bloquear reclassificacao quando tipo informado estiver inativo (422)")
    void deve_bloquearClassificacao_quandoTipoInativo() {
        UUID locId = UUID.randomUUID();
        UUID novoTipoId = UUID.randomUUID();
        TipoLocalidade novoTipo = TipoLocalidade.builder().id(novoTipoId).ativo(false).build();
        Localidade loc = Localidade.builder().id(locId).build();

        when(localidadeRepository.findByIdComDetalhes(locId)).thenReturn(Optional.of(loc));
        when(tipoLocalidadeRepository.findById(novoTipoId)).thenReturn(Optional.of(novoTipo));

        assertThatThrownBy(() -> localidadeService.classificar(locId, new ClassificarLocalidadeRequest(novoTipoId)))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @Test
    @DisplayName("deve atualizar configuracao de sigilo com sucesso")
    void deve_configurarSigiloComSucesso() {
        UUID locId = UUID.randomUUID();
        Localidade loc = Localidade.builder()
                .id(locId)
                .fkCidade(cidadeAtiva)
                .fkTipoLocalidade(tipoAtivo)
                .localSigiloso(false)
                .build();

        when(localidadeRepository.findByIdComDetalhes(locId)).thenReturn(Optional.of(loc));
        when(localidadeRepository.save(any(Localidade.class))).thenAnswer(inv -> inv.getArgument(0));

        LocalidadeResponse response = localidadeService.configurarSigilo(locId, new ConfigurarSigiloRequest(true));

        assertThat(response.localSigiloso()).isTrue();
        assertThat(loc.isLocalSigiloso()).isTrue();
    }
}