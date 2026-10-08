package br.mp.mpms.rotamp.infra.storage;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface StorageService {

    /**
     * Armazena uma fotografia vinculada a um veículo no serviço de storage e retorna sua URL pública/acessível.
     *
     * @param veiculoId Identificador único do veículo
     * @param arquivo Arquivo binário enviado na requisição
     * @return URL do arquivo armazenado
     */
    String armazenarFoto(UUID veiculoId, MultipartFile arquivo);

    /**
     * Remove fisicamente o arquivo do serviço de storage a partir de sua URL.
     *
     * @param url URL da fotografia armazenada
     */
    void removerFoto(String url);

    /**
     * Armazena a imagem de logotipo de uma marca de veículo no serviço de storage e retorna sua URL pública.
     *
     * @param marca Nome da marca do veículo
     * @param arquivo Arquivo binário da imagem do logotipo
     * @return URL do arquivo armazenado
     */
    String armazenarLogo(String marca, MultipartFile arquivo);

    /**
     * Remove fisicamente a imagem de logotipo da marca a partir de sua URL.
     *
     * @param url URL da imagem do logotipo armazenada
     */
    void removerLogo(String url);
}

