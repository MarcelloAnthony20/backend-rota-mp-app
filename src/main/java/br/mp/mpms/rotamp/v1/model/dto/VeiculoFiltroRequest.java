package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.UUID;

public record VeiculoFiltroRequest(
    @Parameter(description = "Filtro opcional por situação ativa/inativa")
    Boolean ativo,
    
    @Parameter(description = "Filtro opcional por localidade de origem (UUID)")
    UUID localidadeId,
    
    @Parameter(description = "Filtro opcional por modelo de veículo (UUID)")
    UUID modeloId,
    
    @Parameter(description = "Termo para busca textual aproximada por placa (case-insensitive)", example = "RPA")
    String placa
) {}

