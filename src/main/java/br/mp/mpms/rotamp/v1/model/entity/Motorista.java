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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "motorista")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Motorista {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "display_nome", nullable = false, length = 200)
    private String displayNome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "fk_localidade",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_motorista_localidade")
    )
    private Localidade fkLocalidade;

    @Column(nullable = false, length = 100)
    private String matricula;

    @Column(length = 500)
    private String foto;

    @Column(name = "contato_pessoal", nullable = false, length = 30)
    private String contatoPessoal;

    @Column(name = "contatos_funcional", nullable = false, length = 30)
    private String contatoFuncional;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusMotorista status;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "fk_usuario",
        nullable = false,
        unique = true,
        foreignKey = @ForeignKey(name = "fk_motorista_usuario")
    )
    private Usuario fkUsuario;

    @PrePersist
    private void preencherDisplayNome() {
        if (displayNome == null && fkUsuario != null) {
            displayNome = fkUsuario.getNome();
        }
    }

    public void atualizarDados(Localidade localidade, String matricula, String contatoPessoal, String contatoFuncional, String displayNome) {
        this.fkLocalidade = localidade;
        this.matricula = matricula;
        this.contatoPessoal = contatoPessoal;
        this.contatoFuncional = contatoFuncional;
        if (displayNome != null && !displayNome.isBlank()) {
            this.displayNome = displayNome;
        }
    }

    public void atualizarFoto(String urlFoto) {
        this.foto = urlFoto;
    }

    public void alterarStatus(StatusMotorista novoStatus) {
        this.status = novoStatus;
    }
}
