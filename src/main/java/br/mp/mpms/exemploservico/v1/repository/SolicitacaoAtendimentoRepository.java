package br.mp.mpms.exemploservico.v1.repository;

import br.mp.mpms.exemploservico.v1.model.entity.SolicitacaoAtendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SolicitacaoAtendimentoRepository extends JpaRepository<SolicitacaoAtendimento, UUID> {
}
