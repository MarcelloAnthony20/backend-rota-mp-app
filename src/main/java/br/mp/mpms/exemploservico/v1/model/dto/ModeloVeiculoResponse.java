package br.mp.mpms.exemploservico.v1.model.dto;

import java.util.UUID;

public record ModeloVeiculoResponse(
        UUID id,
        String marca,
        String modelo,
        Integer numPassageiro,
        String logo,
        boolean ativo
) {
}
