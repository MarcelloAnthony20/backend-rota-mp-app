package br.mp.mpms.rotamp.v1.service;

import br.mp.mpms.rotamp.infra.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CepServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private CepService cepService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        cepService = new CepService(restTemplate);
    }

    @Test
    @DisplayName("deve resolver CEP validando dados via API Mockada (ViaCEP)")
    void deve_resolverCep79002000() {
        CepService.ViaCepResponse mockResponse = new CepService.ViaCepResponse(
                "79002-000", "Rua da Paz", "Jardim dos Estados", null
        );
        when(restTemplate.getForObject(anyString(), eq(CepService.ViaCepResponse.class)))
                .thenReturn(mockResponse);

        CepService.EnderecoCep comTraco = cepService.consultarCep("79002-000");
        assertThat(comTraco.cep()).isEqualTo("79002000");
        assertThat(comTraco.logradouro()).isEqualTo("Rua da Paz");
        assertThat(comTraco.bairro()).isEqualTo("Jardim dos Estados");

        CepService.EnderecoCep semTraco = cepService.consultarCep("79002000");
        assertThat(semTraco.cep()).isEqualTo("79002000");
    }

    @Test
    @DisplayName("deve retornar logradouro null se API ViaCEP retornar erro true")
    void deve_retornarNullSeApiRetornarErro() {
        CepService.ViaCepResponse mockResponseErro = new CepService.ViaCepResponse(
                null, null, null, true
        );
        when(restTemplate.getForObject(anyString(), eq(CepService.ViaCepResponse.class)))
                .thenReturn(mockResponseErro);

        CepService.EnderecoCep res = cepService.consultarCep("79004-311");
        assertThat(res.cep()).isEqualTo("79004311");
        assertThat(res.logradouro()).isNull();
    }
    
    @Test
    @DisplayName("deve retornar CEP basico e logradouro null se API ViaCEP falhar com exceção genérica")
    void deve_retornarFallbackSeApiFalhar() {
        when(restTemplate.getForObject(anyString(), eq(CepService.ViaCepResponse.class)))
                .thenThrow(new RuntimeException("Erro de conexão"));

        CepService.EnderecoCep res = cepService.consultarCep("79800-000");
        assertThat(res.cep()).isEqualTo("79800000");
        assertThat(res.logradouro()).isNull();
    }
    
    @Test
    @DisplayName("deve rejeitar com BAD_REQUEST se a API retornar BAD_REQUEST (formato invalido nativo)")
    void deve_rejeitarCepSeApiRetornarBadRequest() {
        when(restTemplate.getForObject(anyString(), eq(CepService.ViaCepResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> cepService.consultarCep("12345678"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("deve rejeitar CEP inexistente 99999-999 com BusinessException 422 na validacao local")
    void deve_rejeitarCepInexistente99999999() {
        assertThatThrownBy(() -> cepService.consultarCep("99999-999"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getStatus()).isEqualTo(HttpStatus.valueOf(422)));
    }

    @Test
    @DisplayName("deve rejeitar CEP com formato invalido com BusinessException 400 na validacao local")
    void deve_rejeitarCepComFormatoInvalido() {
        assertThatThrownBy(() -> cepService.consultarCep("123"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("deve rejeitar CEP nulo ou em branco com BusinessException 422 na validacao local")
    void deve_rejeitarCepNuloOuBranco() {
        assertThatThrownBy(() -> cepService.consultarCep(null))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getStatus()).isEqualTo(HttpStatus.valueOf(422)));

        assertThatThrownBy(() -> cepService.consultarCep("   "))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getStatus()).isEqualTo(HttpStatus.valueOf(422)));
    }
}
