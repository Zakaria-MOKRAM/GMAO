package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "intervention_histo_statut")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Intervention_Historique_Statut implements Serializable {
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
    private Intervention intervention;
    @ManyToOne
    private Intervention_Status status;
}
