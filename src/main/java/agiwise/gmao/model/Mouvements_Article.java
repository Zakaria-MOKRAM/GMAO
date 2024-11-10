package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "mouvements_article")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Mouvements_Article implements Serializable {

    private static final long serialVersionUID = 1332815146727469462L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    private Entrepot entrepot;
    @ManyToOne
    private Article article;
    @Column(name = "quantite")
    private double quantite;
    @Column(name = "date")
    private Date date;
    @Column(name = "stock")
    private double stock;
    @Column(name = "description")
    private String description;
    @Column(name = "num_piece")
    private String numpiece;
    @Column(name = "ip")
    private long ip;
    @Column(name = "cump")
    private double cump;
    @Column(name = "prix_achat")
    private double prix_achat; // uniquement pour les BR
    @Column(name = "is_ip",columnDefinition = "tinyint default '0'")
    private boolean is_ip;
}
