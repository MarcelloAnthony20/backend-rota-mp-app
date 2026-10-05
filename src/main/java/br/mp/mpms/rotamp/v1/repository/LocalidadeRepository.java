package br.mp.mpms.rotamp.v1.repository;

import br.mp.mpms.rotamp.v1.model.entity.Localidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocalidadeRepository extends JpaRepository<Localidade, UUID> {

    boolean existsByFkCidadeIdAndFkTipoLocalidadeIdAndNomeIgnoreCase(UUID cidadeId, UUID tipoId, String nome);

    boolean existsByFkCidadeIdAndFkTipoLocalidadeIdAndNomeIgnoreCaseAndIdNot(UUID cidadeId, UUID tipoId, String nome, UUID id);

    @Query("SELECT l FROM Localidade l LEFT JOIN FETCH l.endereco LEFT JOIN FETCH l.fkCidade LEFT JOIN FETCH l.fkTipoLocalidade WHERE l.id = :id")
    Optional<Localidade> findByIdComDetalhes(@Param("id") UUID id);

    @Query(
        value = "SELECT l FROM Localidade l LEFT JOIN FETCH l.endereco LEFT JOIN FETCH l.fkCidade LEFT JOIN FETCH l.fkTipoLocalidade",
        countQuery = "SELECT COUNT(l) FROM Localidade l"
    )
    Page<Localidade> findAllComDetalhes(Pageable pageable);
}
