package br.mp.mpms.rotamp.infra.storage;

import br.mp.mpms.rotamp.infra.exception.BusinessException;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class AzureBlobStorageService implements StorageService {

    private static final Set<String> FORMATOS_PERMITIDOS = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final long TAMANHO_MAXIMO_BYTES = 10L * 1024 * 1024; // 10 MB

    @Value("${azure.storage.connection-string:#{null}}")
    private String connectionString;

    @Value("${azure.storage.container-name:fotos-veiculos}")
    private String containerName;

    @Override
    public String armazenarFoto(UUID veiculoId, MultipartFile arquivo) {
        validarArquivo(arquivo);

        BlobContainerClient containerClient = obterContainerClient();
        garantirContainerExiste(containerClient);

        String extensao = obterExtensao(arquivo.getContentType());
        String nomeBlob = "veiculos/" + veiculoId + "/" + UUID.randomUUID() + extensao;

        BlobClient blobClient = containerClient.getBlobClient(nomeBlob);

        try (InputStream inputStream = arquivo.getInputStream()) {
            BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(arquivo.getContentType());
            blobClient.upload(inputStream, arquivo.getSize(), true);
            blobClient.setHttpHeaders(headers);

            String url = blobClient.getBlobUrl();
            log.info("Fotografia armazenada com sucesso no storage para o veiculo {}: {}", veiculoId, url);
            return url;
        } catch (IOException ex) {
            log.error("Falha ao ler dados binarios do arquivo de foto para o veiculo {}", veiculoId, ex);
            throw new BusinessException("Falha ao processar arquivo de imagem para armazenamento", HttpStatus.valueOf(422));
        } catch (Exception ex) {
            log.error("Falha na integracao com Azure Blob Storage / Azurite ao enviar arquivo", ex);
            throw new BusinessException("Falha de comunicacao com o servico de armazenamento de arquivos (Azure/Azurite)", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public void removerFoto(String url) {
        if (url == null || url.isBlank()) {
            return;
        }

        try {
            BlobContainerClient containerClient = obterContainerClient();
            String nomeBlob = extrairNomeBlobDaUrl(url, containerName);

            if (nomeBlob != null) {
                BlobClient blobClient = containerClient.getBlobClient(nomeBlob);
                boolean removido = blobClient.deleteIfExists();
                if (removido) {
                    log.info("Fotografia fisica removida com sucesso do storage: {}", nomeBlob);
                } else {
                    log.warn("Arquivo de foto nao localizado no storage para remocao: {}", nomeBlob);
                }
            }
        } catch (Exception ex) {
            log.warn("Nao foi possivel remover a foto fisica do storage para a URL {}: {}", url, ex.getMessage());
            throw new BusinessException("Falha ao remover arquivo físico do storage", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String armazenarLogo(String marca, MultipartFile arquivo) {
        validarArquivo(arquivo);

        BlobContainerClient containerClient = obterContainerClient();
        garantirContainerExiste(containerClient);

        String extensao = obterExtensao(arquivo.getContentType());
        String marcaSanitizada = marca != null ? marca.toLowerCase().replaceAll("[^a-z0-9]", "-") : "marca";
        String nomeBlob = "logos/" + marcaSanitizada + "-" + UUID.randomUUID() + extensao;

        BlobClient blobClient = containerClient.getBlobClient(nomeBlob);

        try (InputStream inputStream = arquivo.getInputStream()) {
            BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(arquivo.getContentType());
            blobClient.upload(inputStream, arquivo.getSize(), true);
            blobClient.setHttpHeaders(headers);

            String url = blobClient.getBlobUrl();
            log.info("Logotipo da marca '{}' armazenado com sucesso no storage: {}", marca, url);
            return url;
        } catch (IOException ex) {
            log.error("Falha ao ler dados binarios do arquivo de logotipo da marca {}", marca, ex);
            throw new BusinessException("Falha ao processar arquivo de imagem para armazenamento", HttpStatus.valueOf(422));
        } catch (Exception ex) {
            log.error("Falha na integracao com Azure Blob Storage / Azurite ao enviar logotipo da marca {}", marca, ex);
            throw new BusinessException("Falha de comunicacao com o servico de armazenamento de arquivos (Azure/Azurite)", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public void removerLogo(String url) {
        removerFoto(url);
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new BusinessException("O arquivo de imagem e obrigatorio e nao pode estar vazio", HttpStatus.valueOf(422));
        }

        if (arquivo.getSize() > TAMANHO_MAXIMO_BYTES) {
            throw new BusinessException("O tamanho do arquivo excede o limite maximo permitido de 10 MB", HttpStatus.valueOf(422));
        }

        String contentType = arquivo.getContentType();
        if (contentType == null || !FORMATOS_PERMITIDOS.contains(contentType.toLowerCase())) {
            throw new BusinessException("Formato de imagem invalido. Sao permitidos apenas JPEG, PNG e WEBP", HttpStatus.valueOf(422));
        }
    }

    private BlobContainerClient obterContainerClient() {
        if (connectionString == null || connectionString.isBlank()) {
            throw new BusinessException(
                    "Servico de armazenamento (Azure/Azurite) nao configurado. Variavel AZURE_STORAGE_CONNECTION_STRING ausente.",
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }

        BlobServiceClient serviceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        return serviceClient.getBlobContainerClient(containerName);
    }

    private void garantirContainerExiste(BlobContainerClient containerClient) {
        try {
            if (!containerClient.exists()) {
                containerClient.create();
                try {
                    containerClient.setAccessPolicy(null, null); // Alterado para null (Private) para segurança
                } catch (Exception e) {
                    log.debug("Aviso ao definir politica de acesso no container: {}", e.getMessage());
                }
            }
        } catch (Exception ex) {
            log.error("Erro ao verificar ou criar container no storage: {}", containerName, ex);
            throw new BusinessException("Falha ao inicializar container no servico de armazenamento", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private String obterExtensao(String contentType) {
        if (contentType == null) {
            return ".jpg";
        }
        return switch (contentType.toLowerCase()) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }

    private String extrairNomeBlobDaUrl(String url, String container) {
        String token = "/" + container + "/";
        int index = url.indexOf(token);
        if (index != -1) {
            return url.substring(index + token.length());
        }
        return null;
    }
}

