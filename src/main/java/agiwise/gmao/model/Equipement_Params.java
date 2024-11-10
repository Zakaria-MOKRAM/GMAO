package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "equipement_params")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Equipement_Params implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String valeur;
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    @ManyToOne
    private Equipement equipement;
    @ManyToOne
    private Categorie_Params categorie_params;
}
