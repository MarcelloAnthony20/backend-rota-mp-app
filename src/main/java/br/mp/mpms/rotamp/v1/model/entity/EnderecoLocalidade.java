package br.mp.mpms.rotamp.v1.model.entity;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "endereco_localidade")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoLocalidade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String logradouro;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(nullable = false, length = 8)
    private String cep;

    @Column(nullable = false, length = 100)
    private String bairro;

    @Column(length = 200)
    private String complemento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "fk_cidade",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_endereco_localidade_cidade")
    )
    private Cidade fkCidade;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "fk_localidade",
        nullable = false,
        unique = true,
        foreignKey = @ForeignKey(name = "fk_endereco_localidade_localidade")
    )
    private Localidade fkLocalidade;

    public void atualizarDados(String logradouro, String numero, String cep, String bairro, String complemento, Cidade cidade) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.cep = cep;
        this.bairro = bairro;
        this.complemento = complemento;
        this.fkCidade = cidade;
    }

    void vincularLocalidadeECidade(Localidade localidade, Cidade cidade) {
        this.fkLocalidade = localidade;
        this.fkCidade = cidade;
    }
}
