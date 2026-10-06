package br.mp.mpms.rotamp.v1.repository;

import br.mp.mpms.rotamp.v1.model.entity.PerfilTela;
import br.mp.mpms.rotamp.v1.model.entity.Tela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PerfilTelaRepository extends JpaRepository<PerfilTela, UUID> {

    List<PerfilTela> findByFkPerfilId(UUID perfilId);

    @Modifying
    @Query("DELETE FROM PerfilTela pt WHERE pt.fkPerfil.id = :perfilId")
    void deleteByFkPerfilId(@Param("perfilId") UUID perfilId);

    boolean existsByFkPerfilIdAndFkTelaId(UUID perfilId, UUID telaId);

    @Query("SELECT pt.fkTela FROM PerfilTela pt WHERE pt.fkPerfil.id = :perfilId ORDER BY pt.fkTela.nome ASC")
    List<Tela> findTelasByPerfilId(@Param("perfilId") UUID perfilId);
}
