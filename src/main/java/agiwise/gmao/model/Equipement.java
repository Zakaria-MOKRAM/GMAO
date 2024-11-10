package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "equipement")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Equipement implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String libelle;
    private Date date_service;
    private Date date_acquisition;
    private String num_serie;
    private  String num_inventaire;
    private  String prix_achat;
    @Column(name = "description",columnDefinition = "longtext")
    private String description;
    @ManyToOne
    private Emplacement emplacement;

    @ManyToOne
    private Installation installation;

    @ManyToOne
    private Equipement_Status status;
    @ManyToOne
    private Marque marque;
    @ManyToOne
    private Modele modele;
    @ManyToOne
    private Categorie categorie;
    @ManyToOne
    private Fournisseur_Service fournisseur;
    @ManyToOne
    private Equipement_Type type;
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    @Column(name = "has_compteur",columnDefinition = "tinyint default '0'")
    private boolean has_compteur ;
    private String type_compteur ;
    private Long valeur_initiale_compteur;
    private Long valeur_actuelle_compteur;
    private Date date_initiale_compteur;
    private Date date_fin_compteur;
    private Long seuil_compteur;
    @Transient
    private boolean canDelete;
    @Transient
    private boolean canUpdate;
}
