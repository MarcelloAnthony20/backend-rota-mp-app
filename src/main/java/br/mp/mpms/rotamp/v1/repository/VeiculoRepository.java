package br.mp.mpms.rotamp.v1.repository;

import br.mp.mpms.rotamp.v1.model.entity.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, UUID> {

    boolean existsByPlacaIgnoreCase(String placa);

    boolean existsByPlacaIgnoreCaseAndIdNot(String placa, UUID id);

    Optional<Veiculo> findByPlacaIgnoreCase(String placa);

    Page<Veiculo> findByAtivo(boolean ativo, Pageable pageable);

    Page<Veiculo> findByFkLocalidadeId(UUID localidadeId, Pageable pageable);

    Page<Veiculo> findByFkModeloId(UUID modeloId, Pageable pageable);

    Page<Veiculo> findByPlacaContainingIgnoreCase(String placa, Pageable pageable);

    @Query("SELECT v FROM Veiculo v LEFT JOIN FETCH v.fkModelo m LEFT JOIN FETCH m.marca LEFT JOIN FETCH v.fkLocalidade WHERE v.id = :id")
    Optional<Veiculo> findByIdComDetalhes(@Param("id") UUID id);

    @Query(
            value = "SELECT v FROM Veiculo v LEFT JOIN FETCH v.fkModelo m LEFT JOIN FETCH m.marca LEFT JOIN FETCH v.fkLocalidade",
            countQuery = "SELECT COUNT(v) FROM Veiculo v"
    )
    Page<Veiculo> findAllComDetalhes(Pageable pageable);
}
