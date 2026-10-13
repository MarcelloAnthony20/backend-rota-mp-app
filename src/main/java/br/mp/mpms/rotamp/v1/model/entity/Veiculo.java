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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import br.mp.mpms.rotamp.infra.exception.BusinessException;
import org.springframework.http.HttpStatus;
import java.util.UUID;

@Entity
@Table(name = "veiculo")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "fk_localidade",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_veiculo_localidade")
    )
    private Localidade fkLocalidade;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "fk_modelo",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_veiculo_modelo")
    )
    private ModeloVeiculo fkModelo;

    @Column(nullable = false, unique = true, length = 10)
    private String placa;

    @Column(nullable = false)
    private Integer ano;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;

    public void atualizarDados(String placaNormalizada, Integer ano, ModeloVeiculo modelo, Localidade localidade) {
        this.placa = placaNormalizada;
        this.ano = ano;
        this.fkModelo = modelo;
        this.fkLocalidade = localidade;
    }

    public void inativar() {
        this.ativo = false;
    }

    public void ativar() {
        if (!this.fkModelo.isAtivo()) {
            throw new BusinessException(
                    "Nao e possivel ativar o veiculo pois o modelo vinculado esta inativo",
                    HttpStatus.valueOf(422)
            );
        }
        if (!this.fkLocalidade.isAtivo()) {
            throw new BusinessException(
                    "Nao e possivel ativar o veiculo pois a localidade vinculada esta inativa",
                    HttpStatus.valueOf(422)
            );
        }
        this.ativo = true;
    }

    public void vincularLocalidade(Localidade novaLocalidade) {
        if (!novaLocalidade.isAtivo()) {
            throw new BusinessException(
                    "Nao e possivel vincular um veiculo a uma localidade inativa",
                    HttpStatus.valueOf(422)
            );
        }
        this.fkLocalidade = novaLocalidade;
    }
}
