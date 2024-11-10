package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "categorie_params")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Categorie_Params implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String libelle;
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    @ManyToOne
    @JoinColumn(name="categorie_id", nullable=false)
    private Categorie categorie;
    @ManyToOne
    @JoinColumn(name="attributte_id", nullable=false)
    private Attribute_Dynamique attributte;
}
