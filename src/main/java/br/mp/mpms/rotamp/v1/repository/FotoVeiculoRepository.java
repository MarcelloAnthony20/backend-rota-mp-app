package br.mp.mpms.rotamp.v1.repository;

import br.mp.mpms.rotamp.v1.model.entity.FotoVeiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FotoVeiculoRepository extends JpaRepository<FotoVeiculo, UUID> {

    List<FotoVeiculo> findByFkVeiculoId(UUID veiculoId);

    Page<FotoVeiculo> findByFkVeiculoId(UUID veiculoId, Pageable pageable);

    Optional<FotoVeiculo> findByIdAndFkVeiculoId(UUID id, UUID veiculoId);

    boolean existsByIdAndFkVeiculoId(UUID id, UUID veiculoId);

    void deleteByIdAndFkVeiculoId(UUID id, UUID veiculoId);
}
