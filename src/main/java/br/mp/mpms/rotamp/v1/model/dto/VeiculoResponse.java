package br.mp.mpms.rotamp.v1.model.dto;

import java.util.UUID;

public record VeiculoResponse(
        UUID id,
        UUID localidadeId,
        UUID modeloId,
        String placa,
        Integer ano,
        boolean ativo
) {
}
