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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
    name = "perfil_tela",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_perfil_tela",
        columnNames = {"fk_perfil", "fk_tela"}
    )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilTela {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "fk_perfil",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_perfil_tela_perfil")
    )
    private Perfil fkPerfil;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "fk_tela",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_perfil_tela_tela")
    )
    private Tela fkTela;
}
