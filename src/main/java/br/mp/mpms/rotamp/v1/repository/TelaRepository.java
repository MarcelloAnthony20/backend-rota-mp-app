package br.mp.mpms.rotamp.v1.repository;

import br.mp.mpms.rotamp.v1.model.entity.Tela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TelaRepository extends JpaRepository<Tela, UUID> {
}
