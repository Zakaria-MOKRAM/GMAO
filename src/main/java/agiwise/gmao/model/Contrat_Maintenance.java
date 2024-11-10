package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "contrat_maintenance")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contrat_Maintenance implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String observation;
    private Date date_debut;
    private Date date_fin;
    @ManyToOne
    private Fournisseur_Service fournisseur;
    @ManyToOne
    private Contrat_Maintenance_Statut status;
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    @Transient
    private boolean canDelete;
    @Transient
    private boolean canUpdate;
}
