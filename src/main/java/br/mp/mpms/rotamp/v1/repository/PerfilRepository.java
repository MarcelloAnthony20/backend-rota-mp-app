package br.mp.mpms.rotamp.v1.repository;

import br.mp.mpms.rotamp.v1.model.entity.Perfil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, UUID> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, UUID id);

    Optional<Perfil> findByNomeIgnoreCase(String nome);

    Page<Perfil> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<Perfil> findByAtivo(boolean ativo, Pageable pageable);

    Page<Perfil> findByNomeContainingIgnoreCaseAndAtivo(String nome, boolean ativo, Pageable pageable);
}
