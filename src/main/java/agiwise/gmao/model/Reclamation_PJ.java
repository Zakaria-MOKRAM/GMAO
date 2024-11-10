package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "reclamation_pj")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reclamation_PJ implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="reclamation_id")
    private Reclamation reclamation;

    @Column(name = "nom_pj")
    private String nom_pj;

    @Column(name = "date_ajout")
    private Date date_ajout;

    @Column(name = "taille_pj")
    private double taille_pj;

    @Column(name = "auteur")
    private String auteur;
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    @Transient
    private boolean canDelete;
    @Transient
    private boolean canUpdate;
}
