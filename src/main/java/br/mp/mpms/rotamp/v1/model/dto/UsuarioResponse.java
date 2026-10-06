package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Representação dos dados do usuário institucional")
public record UsuarioResponse(
        @Schema(description = "Identificador único (UUID) do usuário", example = "u1000001-0000-0000-0000-000000000001")
        UUID id,

        @Schema(description = "Nome completo institucional do colaborador", example = "Marcello Anthony")
        String nome,

        @Schema(description = "E-mail funcional institucional", example = "marcellomartins@mpms.mp.br")
        String email,

        @Schema(description = "Identificador do tema de interface selecionado", example = "t0000001-0000-0000-0000-000000000001")
        UUID temaId,

        @Schema(description = "Identificador do perfil institucional atribuído", example = "p1000001-0000-0000-0000-000000000001")
        UUID perfilId,

        @Schema(description = "Identificador de login na rede institucional (AD/LDAP)", example = "marcellomartins")
        String mailNickname,

        @Schema(description = "Indicador de conta ativa no sistema", example = "true")
        boolean ativo,

        @Schema(description = "Carimbo de data/hora de criação do usuário")
        LocalDateTime dataCriacao,

        @Schema(description = "Carimbo de data/hora da última alteração cadastral")
        LocalDateTime dataAtualizacao
) {
}
