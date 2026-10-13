package br.mp.mpms.rotamp.v1.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuario")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "fk_tema",
        referencedColumnName = "id",
        foreignKey = @ForeignKey(name= "fk_usuario_tema")
    )
    private Tema fkTema;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "fk_perfil",
        referencedColumnName = "id",
        foreignKey = @ForeignKey(name= "fk_usuario_perfil")
    )
    private Perfil fkPerfil;

    @Column(name = "mail_nickname",nullable = false,unique = true,length = 100)
    private String mailNickname;
    
    @Builder.Default
    @Column(name ="ativo", nullable = false)
    private boolean ativo = true;

    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    public void atualizarDados(String nome, String email, Tema tema, Perfil perfil, String mailNickname) {
        this.nome = nome;
        this.email = email;
        this.fkTema = tema;
        this.fkPerfil = perfil;
        this.mailNickname = mailNickname;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void inativar() {
        this.ativo = false;
    }

    public void alterarTema(Tema tema) {
        this.fkTema = tema;
    }

    public void vincularPerfil(Perfil perfil) {
        this.fkPerfil = perfil;
    }
}
