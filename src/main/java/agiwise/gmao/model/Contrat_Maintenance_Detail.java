package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "contrat_maintenance_detail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contrat_Maintenance_Detail implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Contrat_Maintenance contrat;
    @ManyToOne
    private Equipement equipement;
    @ManyToOne
    private Maintenance_Type type;
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    @Transient
    private boolean canDelete;
    @Transient
    private boolean canUpdate;
    @Transient
    private  long intervention_id;
}
