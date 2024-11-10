package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "reclamation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reclamation implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "libelle", columnDefinition= "longtext")
    private String libelle;
    @Column(name = "response", columnDefinition= "longtext")
    private String response;
    @Column(name = "non_valide", columnDefinition= "longtext")
    private String non_valide;
    @Column(name = "titre", columnDefinition= "longtext")
    private String titre;
    private Date date;
    private Date date_modification;
    private Date date_traitement;
    @ManyToOne
    private Utilisateur user;
    @ManyToOne
    private Utilisateur responsable;
    @ManyToOne
    private Service_Travaux service;
    @ManyToOne
    private Emplacement emplacement;
     @ManyToOne
    private Equipement equipement;
    @ManyToOne
    private Reclamation_Statut status;
    @Transient
    public List<String> fileNames;
    @Column(name = "urgent",columnDefinition = "tinyint default '0'")
    private boolean urgent;
    @Column(name = "visited",columnDefinition = "tinyint default '0'")
    private boolean visited;
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    @Transient
    private boolean canDelete;
    @Transient
    private boolean canUpdate;
}
