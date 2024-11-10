package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "intervention")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Intervention implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String observation;
    private Date date_debut;
    private Date date_fin;
    private Long contrat_detail_id;
    @ManyToOne
    private Installation installation;
     @ManyToOne
    private Reclamation reclamation;
    @ManyToOne
    private Equipement equipement;
    @ManyToOne
    private Utilisateur responsable;
    @ManyToOne
    private Fournisseur_Service fournisseur;
    @ManyToOne
    private Intervention_Type type;
    @ManyToOne
    private Intervention_Status status;
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    @Transient
    private boolean canDelete;
    @Transient
    private boolean canUpdate;
}
