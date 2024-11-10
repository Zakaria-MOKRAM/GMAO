package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "contrat_equipement")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contrat_Equipement implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String libelle;
    private Date debut;
    private Date fin;
    @ManyToOne
    private Equipement equipement;
    @ManyToOne
    private Fournisseur_Service societe;
    @ManyToOne
    private Contrat_Type contrat_type;
    @ManyToOne
    private Contrat_Status contrat_status;
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    @Transient
    private boolean canDelete;
    @Transient
    private boolean canUpdate;
}
