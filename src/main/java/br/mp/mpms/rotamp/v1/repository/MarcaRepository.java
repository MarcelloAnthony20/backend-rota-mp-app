package br.mp.mpms.rotamp.v1.repository;

import br.mp.mpms.rotamp.v1.model.entity.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, UUID> {

    Optional<Marca> findByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCase(String nome);
}

