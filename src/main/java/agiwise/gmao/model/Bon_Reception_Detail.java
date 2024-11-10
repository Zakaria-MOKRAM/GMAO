package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "bon_reception_detail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bon_Reception_Detail implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double qte;
    private int tva;
    private double ht;
    @Column(name = "total_ttc",columnDefinition = "double default 0")
    private double total_ttc;
    @Column(name = "total_ht",columnDefinition = "double default 0")
    private double total_ht;
    @ManyToOne
    private Bon_Reception br;
    @ManyToOne
    private Article article;
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
}
