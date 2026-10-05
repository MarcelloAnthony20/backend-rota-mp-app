package br.mp.mpms.rotamp.v1.service;

import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.repository.FotoVeiculoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FotoVeiculoServiceTest {

    @Mock
    private FotoVeiculoRepository fotoVeiculoRepository;

    @InjectMocks
    private FotoVeiculoService fotoVeiculoService;

    @Test
    void deveLancarNotFoundQuandoIdNaoExiste() {
        UUID id = UUID.randomUUID();
        when(fotoVeiculoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fotoVeiculoService.buscarPorId(id))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}