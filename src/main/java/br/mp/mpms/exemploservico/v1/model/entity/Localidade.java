package br.mp.mpms.exemploservico.v1.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.util.UUID;

@Entity
@Table(name = "localidade")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Localidade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "fk_tipo_localidade",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_localidade_tipo_localidade")
    )
    private TipoLocalidade fkTipoLocalidade;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "fk_cidade",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_localidade_cidade")
    )
    private Cidade fkCidade;

    @Column(nullable = false, length = 200)
    private String nome;

    @Builder.Default
    @Column(name = "local_sigiloso", nullable = false)
    private boolean localSigiloso = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;
}
