package br.mp.mpms.rotamp.v1.service;

import br.mp.mpms.rotamp.infra.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CepService {

    public record EnderecoCep(String cep, String logradouro, String bairro) {}

    private static final Map<String, EnderecoCep> BASE_CORREIOS = Map.of(
            "79002000", new EnderecoCep("79002000", "Rua da Paz", "Jardim dos Estados"),
            "79004311", new EnderecoCep("79004311", "Rua Ceará", "Santa Fé"),
            "79800000", new EnderecoCep("79800000", "Rua Presidente Vargas", "Vila Progresso")
    );

    public EnderecoCep consultarCep(String cep) {
        if (cep == null || cep.isBlank()) {
            throw new BusinessException("CEP e obrigatorio", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        String sanitizado = cep.replaceAll("\\D", "");
        if (sanitizado.length() != 8) {
            throw new BusinessException("CEP em formato invalido: " + cep, HttpStatus.BAD_REQUEST);
        }

        if (sanitizado.equals("99999999") || sanitizado.matches("^9{8}$") || sanitizado.matches("^0{8}$")) {
            throw new BusinessException("CEP inexistente na base dos Correios: " + cep, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        EnderecoCep encontrado = BASE_CORREIOS.get(sanitizado);
        if (encontrado != null) {
            return encontrado;
        }

        return new EnderecoCep(sanitizado, null, null);
    }
}
