package br.mp.mpms.rotamp.v1.repository;

import br.mp.mpms.rotamp.v1.model.entity.ModeloVeiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ModeloVeiculoRepository extends JpaRepository<ModeloVeiculo, UUID> {

    boolean existsByMarcaNomeIgnoreCaseAndModeloIgnoreCase(String marcaNome, String modelo);

    boolean existsByMarcaNomeIgnoreCaseAndModeloIgnoreCaseAndIdNot(String marcaNome, String modelo, UUID id);

    boolean existsByMarcaId(UUID marcaId);

    Page<ModeloVeiculo> findByAtivo(boolean ativo, Pageable pageable);

    @Query("""
        SELECT m FROM ModeloVeiculo m
        JOIN m.marca ma
        WHERE LOWER(m.modelo) LIKE LOWER(CONCAT('%', :termo, '%'))
           OR LOWER(ma.nome) LIKE LOWER(CONCAT('%', :termo, '%'))
    """)
    Page<ModeloVeiculo> findByTermo(@Param("termo") String termo, Pageable pageable);

    @Query("""
        SELECT m FROM ModeloVeiculo m
        JOIN m.marca ma
        WHERE m.ativo = :ativo
          AND (LOWER(m.modelo) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(ma.nome) LIKE LOWER(CONCAT('%', :termo, '%')))
    """)
    Page<ModeloVeiculo> findByAtivoAndTermo(@Param("ativo") boolean ativo, @Param("termo") String termo, Pageable pageable);
}
