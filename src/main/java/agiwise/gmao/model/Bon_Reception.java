package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "bon_reception")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bon_Reception implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private Date date;
    @Column(name = "total_ttc",columnDefinition = "double default 0")
    private double total_ttc;
    @Column(name = "total_ht",columnDefinition = "double default '0'")
    private double total_ht;
    @ManyToOne
    private Fournisseur_Article fournisseur;
    @ManyToOne
    private Bon_Reception_Status status;
    @ManyToOne
    private Entrepot entrepot;
    @Column(name = "etat_supp",columnDefinition = "tinyint default 0")
    private boolean etatSupp = false;
    @Transient
    private boolean canDelete;
    @Transient
    private boolean canUpdate;
}
