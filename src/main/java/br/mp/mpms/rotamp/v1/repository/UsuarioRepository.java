package br.mp.mpms.rotamp.v1.repository;

import br.mp.mpms.rotamp.v1.model.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    long countByFkPerfilId(UUID perfilId);

    Page<Usuario> findByFkPerfilId(UUID perfilId, Pageable pageable);

    boolean existsByFkPerfilIdAndAtivoTrue(UUID perfilId);
}
