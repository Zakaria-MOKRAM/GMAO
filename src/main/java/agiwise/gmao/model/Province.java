package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "jw_c_departements")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Province implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rowid;
    private String code_departement;
    private String nom;
    private Integer fk_region;
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    @Column(name = "active",columnDefinition = "tinyint(4) default '1'")
    private boolean active = false;
}
