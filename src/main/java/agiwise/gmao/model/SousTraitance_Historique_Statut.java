package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "sous_traitance_histo_statut")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SousTraitance_Historique_Statut implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    @Column(name = "actual",columnDefinition = "tinyint default '0'")
    private boolean actual;
    @Column(name = "description",columnDefinition = "longtext")
    private String description;
    @ManyToOne
    private Sous_Traitance sous_traitance;
    @ManyToOne
    private Sous_Traitance_Status status;
}
