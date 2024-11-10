package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "sous_traitance")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sous_Traitance implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String observation;
    private Date date;
    @Column(name = "montant",columnDefinition = "double default '0'")
    private double montant;
    @ManyToOne
    private Fournisseur_Service sous_traitant;
    @ManyToOne
    private Sous_Traitance_Status status;
    @ManyToOne
    private Intervention intervention;
    @Column(name = "etat_supp",columnDefinition = "tinyint default 0")
    private boolean etatSupp = false;
    @Transient
    private boolean canDelete;
    @Transient
    private boolean canUpdate;
}
