package br.mp.mpms.rotamp.v1.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "solicitacao_atendimento")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "fk_tipo",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_solicitacao_atendimento_tipo")
    )
    private Tipo fkTipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "fk_usuario",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_solicitacao_atendimento_usuario")
    )
    private Usuario fkUsuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "fk_localidade_origem",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_solicitacao_atendimento_origem")
    )
    private Localidade fkLocalidadeOrigem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "fk_localidade_destino",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_solicitacao_atendimento_destino")
    )
    private Localidade fkLocalidadeDestino;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSolicitacao status;

    @Column(nullable = false, length = 30)
    private String prioridade;

    @Column(name = "data_analise")
    private LocalDateTime dataAnalise;

    @Column(name = "num_passageiro", nullable = false)
    private Integer numPassageiro;

    @Column(nullable = false, length = 1000)
    private String justificativa;

    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime dataPedido;
}
