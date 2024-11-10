package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "article_stock")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Article_Stock implements Serializable {

    private static final long serialVersionUID = 1332815146727469462L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private Entrepot entrepot;
    @ManyToOne
    private Article article;
    private Date last_date;
    @Column(name = "stock_reel")
    private double stock_reel;
    @Column(name = "stock_theorique")
    private double stock_theorique;
    @Column(name = "Prix_Unitaire")
    private double prix_unitaire;

}
