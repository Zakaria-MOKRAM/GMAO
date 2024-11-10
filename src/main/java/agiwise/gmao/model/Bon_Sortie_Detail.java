package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "bon_sortie_detail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bon_Sortie_Detail implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double qte;
    @ManyToOne
    private Bon_Sortie bs;
    @ManyToOne
    private Article article;
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    @Transient
    private double cump;
}
