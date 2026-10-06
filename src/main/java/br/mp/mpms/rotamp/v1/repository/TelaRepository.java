package br.mp.mpms.rotamp.v1.repository;

import br.mp.mpms.rotamp.v1.model.entity.Tela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TelaRepository extends JpaRepository<Tela, UUID> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByRota(String rota);

    List<Tela> findByIdIn(List<UUID> ids);

    List<Tela> findAllByOrderByNomeAsc();
}
