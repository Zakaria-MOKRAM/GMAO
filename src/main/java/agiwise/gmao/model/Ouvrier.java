package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ouvrier")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ouvrier implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cin;
    private String nom;
    private String prenom;
    private boolean disponible;
    @Column(name = "tjm",columnDefinition = "double default 0")
    private double tjm;
    @Column(name = "thm",columnDefinition = "double default 0")
    private double thm;
    @ManyToOne
    private Service_Travaux service;
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    @Transient
    private boolean canDelete;
    @Transient
    private boolean canUpdate;
}
