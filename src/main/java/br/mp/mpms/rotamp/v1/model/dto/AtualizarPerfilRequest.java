package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload para atualizacao dos dados cadastrais do perfil institucional")
public record AtualizarPerfilRequest(
        @Schema(description = "Nome do perfil institucional", example = "GESTOR", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O nome do perfil e obrigatorio")
        @Size(max = 255, message = "O nome do perfil deve ter no maximo 255 caracteres")
        String nome,

        @Schema(description = "Descricao detalhada das responsabilidades", example = "Gestao de frotas e despacho operacional", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "A descricao do perfil e obrigatoria")
        @Size(max = 255, message = "A descricao do perfil deve ter no maximo 255 caracteres")
        String descricao
) {
}
