package br.mp.mpms.rotamp.v1.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
    name = "localidade",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_localidade_cidade_tipo_nome", columnNames = {"fk_cidade", "fk_tipo_localidade", "nome"})
    }
)
@Getter
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

    @OneToOne(mappedBy = "fkLocalidade", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EnderecoLocalidade endereco;

    public void atribuirEndereco(EnderecoLocalidade endereco) {
        this.endereco = endereco;
        if (endereco != null) {
            endereco.vincularLocalidadeECidade(this, this.fkCidade);
        }
    }

    public void atualizarDados(TipoLocalidade tipoLocalidade, Cidade cidade, String nome, boolean localSigiloso) {
        this.fkTipoLocalidade = tipoLocalidade;
        this.fkCidade = cidade;
        this.nome = nome;
        this.localSigiloso = localSigiloso;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void inativar() {
        this.ativo = false;
    }
}
