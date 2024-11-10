package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "jw_c_forme_juridique")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Forme_Juridique implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String code;
    private String libelle;
    @Column(name = "fk_pays",columnDefinition = "int(11) default 0")
    private int fk_pays;
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    @Column(name = "active",columnDefinition = "tinyint(4) default '1'")
    private boolean active = false;
}
