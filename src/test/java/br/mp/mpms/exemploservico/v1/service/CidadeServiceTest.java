package br.mp.mpms.exemploservico.v1.service;

import br.mp.mpms.exemploservico.infra.exception.ResourceNotFoundException;
import br.mp.mpms.exemploservico.v1.model.dto.CidadeResponse;
import br.mp.mpms.exemploservico.v1.model.entity.Cidade;
import br.mp.mpms.exemploservico.v1.repository.CidadeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CidadeServiceTest {

    @Mock
    private CidadeRepository cidadeRepository;

    @InjectMocks
    private CidadeService cidadeService;

    @Test
    @DisplayName("deve retornar cidade quando ID existe")
    void deveRetornarCidadeQuandoIdExiste() {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111013");
        Cidade cidade = Cidade.builder()
                .id(id)
                .nome("Cidade Consulta ID")
                .codigo("0000013")
                .ativo(true)
                .build();
        when(cidadeRepository.findById(id)).thenReturn(Optional.of(cidade));

        CidadeResponse response = cidadeService.buscarPorId(id);

        assertThat(response).isEqualTo(new CidadeResponse(id, "Cidade Consulta ID", "0000013", true));
        verify(cidadeRepository).findById(id);
    }

    @Test
    @DisplayName("deve listar cidades mapeadas para resposta")
    void deveListarCidadesMapeadasParaResposta() {
        Cidade cidade = Cidade.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111002"))
                .nome("Campo Grande")
                .codigo("0000002")
                .ativo(true)
                .build();
        when(cidadeRepository.findAll(Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(cidade)));

        var response = cidadeService.listar(Pageable.unpaged());

        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent()).containsExactly(
                new CidadeResponse(cidade.getId(), "Campo Grande", "0000002", true));
        verify(cidadeRepository).findAll(Pageable.unpaged());
    }

    @Test
    @DisplayName("deve preservar estado inativo no mapeamento da cidade")
    void devePreservarEstadoInativoNoMapeamentoDaCidade() {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111008");
        Cidade cidade = Cidade.builder()
                .id(id)
                .nome("Cidade Inativa")
                .codigo("0000008")
                .ativo(false)
                .build();
        when(cidadeRepository.findById(id)).thenReturn(Optional.of(cidade));

        CidadeResponse response = cidadeService.buscarPorId(id);

        assertThat(response.ativo()).isFalse();
    }

    @Test
    void deveLancarNotFoundQuandoIdNaoExiste() {
        UUID id = UUID.randomUUID();
        when(cidadeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cidadeService.buscarPorId(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Cidade nao encontrada");
        verify(cidadeRepository).findById(id);
    }
}