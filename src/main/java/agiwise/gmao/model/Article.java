package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "article")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Article implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String libelle;
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    private double pmp;
    @Column(name = "has_compteur",columnDefinition = "longtext")
    private String has_compteur ;
    private String type_compteur ;
    private Long valeur_initiale_compteur;
    private Long valeur_actuelle_compteur;
    private Date date_initiale_compteur;
    private Date date_fin_compteur;
    private Long seuil_compteur;
    @ManyToOne
    private Unite unite;
    @Transient
    private boolean canDelete;
    @Transient
    private boolean canUpdate;
}
