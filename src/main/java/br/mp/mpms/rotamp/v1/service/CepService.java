package br.mp.mpms.rotamp.v1.service;

import br.mp.mpms.rotamp.infra.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class CepService {

    public record EnderecoCep(String cep, String logradouro, String bairro) {}
    
    public record ViaCepResponse(String cep, String logradouro, String bairro, Boolean erro) {}

    private final RestTemplate restTemplate;

    public CepService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public EnderecoCep consultarCep(String cep) {
        if (cep == null || cep.isBlank()) {
            throw new BusinessException("CEP e obrigatorio", HttpStatus.valueOf(422));
        }

        String sanitizado = cep.replaceAll("\\D", "");
        if (sanitizado.length() != 8) {
            throw new BusinessException("CEP em formato invalido: " + cep, HttpStatus.BAD_REQUEST);
        }

        if (sanitizado.matches("^9{8}$") || sanitizado.matches("^0{8}$")) {
            throw new BusinessException("CEP inexistente na base dos Correios: " + cep, HttpStatus.valueOf(422));
        }

        String url = "https://viacep.com.br/ws/" + sanitizado + "/json/";
        
        try {
            ViaCepResponse response = restTemplate.getForObject(url, ViaCepResponse.class);
            
            if (response != null && Boolean.TRUE.equals(response.erro())) {
                return new EnderecoCep(sanitizado, null, null);
            }
            
            if (response != null) {
                return new EnderecoCep(
                    sanitizado, 
                    response.logradouro(), 
                    response.bairro()
                );
            }
        } catch (HttpClientErrorException e) {
            log.error("Erro HTTP ao consultar CEP {}: {}", sanitizado, e.getMessage());
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new BusinessException("Formato de CEP invalido retornado pela API", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Erro inesperado ao consultar CEP {}", sanitizado, e);
        }

        return new EnderecoCep(sanitizado, null, null);
    }
}
