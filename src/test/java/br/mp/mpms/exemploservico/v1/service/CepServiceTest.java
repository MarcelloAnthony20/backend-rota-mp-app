package br.mp.mpms.exemploservico.v1.service;

import br.mp.mpms.exemploservico.infra.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CepServiceTest {

    private final CepService cepService = new CepService();

    @Test
    @DisplayName("deve resolver CEP 79002-000 formatado e sem formatacao com Rua da Paz e Jardim dos Estados")
    void deve_resolverCep79002000() {
        CepService.EnderecoCep comTraco = cepService.consultarCep("79002-000");
        assertThat(comTraco.cep()).isEqualTo("79002000");
        assertThat(comTraco.logradouro()).isEqualTo("Rua da Paz");
        assertThat(comTraco.bairro()).isEqualTo("Jardim dos Estados");

        CepService.EnderecoCep semTraco = cepService.consultarCep("79002000");
        assertThat(semTraco.cep()).isEqualTo("79002000");
        assertThat(semTraco.logradouro()).isEqualTo("Rua da Paz");
        assertThat(semTraco.bairro()).isEqualTo("Jardim dos Estados");
    }

    @Test
    @DisplayName("deve resolver CEP 79004-311 com Rua Ceara e Santa Fe (RN-8)")
    void deve_resolverCep79004311() {
        CepService.EnderecoCep res = cepService.consultarCep("79004-311");
        assertThat(res.cep()).isEqualTo("79004311");
        assertThat(res.logradouro()).isEqualTo("Rua Ceará");
        assertThat(res.bairro()).isEqualTo("Santa Fé");
    }

    @Test
    @DisplayName("deve resolver CEP 79800-000 de Dourados com Rua Presidente Vargas e Vila Progresso")
    void deve_resolverCep79800000() {
        CepService.EnderecoCep res = cepService.consultarCep("79800-000");
        assertThat(res.cep()).isEqualTo("79800000");
        assertThat(res.logradouro()).isEqualTo("Rua Presidente Vargas");
        assertThat(res.bairro()).isEqualTo("Vila Progresso");
    }

    @Test
    @DisplayName("deve rejeitar CEP inexistente 99999-999 com BusinessException 422")
    void deve_rejeitarCepInexistente99999999() {
        assertThatThrownBy(() -> cepService.consultarCep("99999-999"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));

        assertThatThrownBy(() -> cepService.consultarCep("99999999"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @Test
    @DisplayName("deve rejeitar CEP com formato invalido com BusinessException 400")
    void deve_rejeitarCepComFormatoInvalido() {
        assertThatThrownBy(() -> cepService.consultarCep("123"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("deve rejeitar CEP nulo ou em branco com BusinessException 422")
    void deve_rejeitarCepNuloOuBranco() {
        assertThatThrownBy(() -> cepService.consultarCep(null))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));

        assertThatThrownBy(() -> cepService.consultarCep("   "))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }
}
