package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "fournisseur_article")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Fournisseur_Article implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String rs;
    private String phone;
    private String fax;
    private String url;
    private String email;
    private String ziptown;
    private String code_fournisseur;
    private String address;
    private String town;
    private String siren;
    private String siret;
    private String ape;
    private String idprof4;
    private String idprof5;
    private String capital;
    @ManyToOne
    @JoinColumn(name="id_activite", nullable=true)
    private Activite activite;
    @ManyToOne
    @JoinColumn(name="fk_departement", nullable=true)
    private Province province;
    @ManyToOne
    @JoinColumn(name="fk_pays", nullable=true)
    private Pays pays;
    @ManyToOne
    @JoinColumn(name="fk_forme_juridique", nullable=true)
    private Forme_Juridique forme;
    @ManyToOne
    @JoinColumn(name="fk_currency", nullable=true)
    private Devise devise;
    private int fournisseur;
    private int client;
    @ManyToMany
    @JoinTable(name = "jw_categorie_fournisseur_article", joinColumns = @JoinColumn(name = "fk_soc"), inverseJoinColumns = @JoinColumn(name = "fk_categorie"))
    private Collection<Fournisseur_Categorie> categories = new ArrayList<>();
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    @Transient
    private boolean canDelete;
    @Transient
    private boolean canUpdate;
}
