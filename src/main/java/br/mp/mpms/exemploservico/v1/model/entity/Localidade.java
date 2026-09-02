package br.mp.mpms.exemploservico.v1.model.entity;

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
}
