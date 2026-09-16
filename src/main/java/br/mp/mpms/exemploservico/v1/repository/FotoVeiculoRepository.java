package br.mp.mpms.exemploservico.v1.repository;

import br.mp.mpms.exemploservico.v1.model.entity.FotoVeiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FotoVeiculoRepository extends JpaRepository<FotoVeiculo, UUID> {
}
