package jway.gmao.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "inv_physique_details")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Inv_Physique_Details implements Serializable {

    private static final long serialVersionUID = 1332815146727469462L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Article article;

    @Column(name = "quantite")
    private double quantite;

    @Column(name = "prix_unitaire")
    private double prix_unitaire;

    @ManyToOne
    private Inv_Physique inv_physique;

    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    @Transient
    private boolean canDelete;
    @Transient
    private boolean canUpdate;

}

