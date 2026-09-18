package br.mp.mpms.exemploservico.infra.seed;

import br.mp.mpms.exemploservico.v1.model.entity.Cidade;
import br.mp.mpms.exemploservico.v1.repository.CidadeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CidadeDataInitializerTest {

    @Mock
    private CidadeRepository cidadeRepository;

    @InjectMocks
    private CidadeDataInitializer cidadeDataInitializer;

    private static final UUID ID_CAMPO_GRANDE = UUID.fromString("c1d00001-0000-0000-0000-000000000001");
    private static final UUID ID_DOURADOS = UUID.fromString("c1d00002-0000-0000-0000-000000000002");
    private static final UUID ID_TRES_LAGOAS = UUID.fromString("c1d00003-0000-0000-0000-000000000003");
    private static final UUID ID_CORUMBA = UUID.fromString("c1d00004-0000-0000-0000-000000000004");

    @Test
    @DisplayName("deve carregar todas as seeds quando nenhuma existir no banco")
    void deve_carregarTodasAsSeeds_quando_nenhumaExistirNoBanco() {
        when(cidadeRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        when(cidadeRepository.existsByCodigo(any(String.class))).thenReturn(false);

        cidadeDataInitializer.run();

        ArgumentCaptor<Cidade> captor = ArgumentCaptor.forClass(Cidade.class);
        verify(cidadeRepository, times(4)).save(captor.capture());

        List<Cidade> savedCidades = captor.getAllValues();

        Cidade c1 = savedCidades.stream().filter(c -> c.getId().equals(ID_CAMPO_GRANDE)).findFirst().orElseThrow();
        assertThat(c1.getNome()).isEqualTo("Campo Grande");
        assertThat(c1.getCodigo()).isEqualTo("5002704");
        assertThat(c1.isAtivo()).isTrue();

        Cidade c2 = savedCidades.stream().filter(c -> c.getId().equals(ID_DOURADOS)).findFirst().orElseThrow();
        assertThat(c2.getNome()).isEqualTo("Dourados");
        assertThat(c2.getCodigo()).isEqualTo("5003702");
        assertThat(c2.isAtivo()).isTrue();

        Cidade c3 = savedCidades.stream().filter(c -> c.getId().equals(ID_TRES_LAGOAS)).findFirst().orElseThrow();
        assertThat(c3.getNome()).isEqualTo("Três Lagoas");
        assertThat(c3.getCodigo()).isEqualTo("5008306");
        assertThat(c3.isAtivo()).isTrue();

        Cidade c4 = savedCidades.stream().filter(c -> c.getId().equals(ID_CORUMBA)).findFirst().orElseThrow();
        assertThat(c4.getNome()).isEqualTo("Corumbá");
        assertThat(c4.getCodigo()).isEqualTo("5003207");
        assertThat(c4.isAtivo()).isFalse();
    }

    @Test
    @DisplayName("deve ignorar carga quando seed ja existir por id")
    void deve_ignorarCarga_quando_seedJaExistirPorId() {
        Cidade cidadeExistente = Cidade.builder()
                .id(ID_CAMPO_GRANDE)
                .nome("Campo Grande")
                .codigo("5002704")
                .ativo(true)
                .build();

        when(cidadeRepository.findById(ID_CAMPO_GRANDE)).thenReturn(Optional.of(cidadeExistente));
        when(cidadeRepository.findById(ID_DOURADOS)).thenReturn(Optional.empty());
        when(cidadeRepository.findById(ID_TRES_LAGOAS)).thenReturn(Optional.empty());
        when(cidadeRepository.findById(ID_CORUMBA)).thenReturn(Optional.empty());
        when(cidadeRepository.existsByCodigo(any(String.class))).thenReturn(false);

        cidadeDataInitializer.run();

        ArgumentCaptor<Cidade> captor = ArgumentCaptor.forClass(Cidade.class);
        verify(cidadeRepository, times(3)).save(captor.capture());

        List<Cidade> saved = captor.getAllValues();
        assertThat(saved).noneMatch(c -> c.getId().equals(ID_CAMPO_GRANDE));
    }

    @Test
    @DisplayName("deve ignorar carga quando seed ja existir por codigo")
    void deve_ignorarCarga_quando_seedJaExistirPorCodigo() {
        when(cidadeRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        when(cidadeRepository.existsByCodigo("5003207")).thenReturn(true);
        when(cidadeRepository.existsByCodigo("5002704")).thenReturn(false);
        when(cidadeRepository.existsByCodigo("5003702")).thenReturn(false);
        when(cidadeRepository.existsByCodigo("5008306")).thenReturn(false);

        cidadeDataInitializer.run();

        ArgumentCaptor<Cidade> captor = ArgumentCaptor.forClass(Cidade.class);
        verify(cidadeRepository, times(3)).save(captor.capture());

        List<Cidade> saved = captor.getAllValues();
        assertThat(saved).noneMatch(c -> c.getCodigo().equals("5003207"));
    }

    @Test
    @DisplayName("deve garantir idempotencia quando todas as seeds ja existirem")
    void deve_garantirIdempotencia_quando_todasAsSeedsJaExistirem() {
        Cidade c1 = Cidade.builder().id(ID_CAMPO_GRANDE).build();
        Cidade c2 = Cidade.builder().id(ID_DOURADOS).build();
        Cidade c3 = Cidade.builder().id(ID_TRES_LAGOAS).build();
        Cidade c4 = Cidade.builder().id(ID_CORUMBA).build();

        when(cidadeRepository.findById(ID_CAMPO_GRANDE)).thenReturn(Optional.of(c1));
        when(cidadeRepository.findById(ID_DOURADOS)).thenReturn(Optional.of(c2));
        when(cidadeRepository.findById(ID_TRES_LAGOAS)).thenReturn(Optional.of(c3));
        when(cidadeRepository.findById(ID_CORUMBA)).thenReturn(Optional.of(c4));

        cidadeDataInitializer.run();

        verify(cidadeRepository, never()).save(any());
    }
}
