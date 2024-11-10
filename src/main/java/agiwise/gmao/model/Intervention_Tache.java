package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "intervention_tache")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Intervention_Tache implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String observation;
    private Date date;
    private double duree;
    @ManyToOne
    private Intervention intervention;
    @ManyToOne
    private Ouvrier ouvrier;
    @ManyToOne
    private Tache tache;
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    @Transient
    private boolean canDelete;
    @Transient
    private boolean canUpdate;
}
