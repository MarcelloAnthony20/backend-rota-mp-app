package br.mp.mpms.exemploservico.v1.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "tipo_localidade")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoLocalidade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String codigo;

    @Column(name = "tipo_localidade", nullable = false, length = 100)
    private String tipoLocalidade;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;

}
