package jway.gmao.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "inv_physique")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Inv_Physique implements Serializable {

    private static final long serialVersionUID = 1332815146727469462L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Entrepot entrepot;

    @Column(name = "date")
    private Date date;

    @Column(name = "date_validation")
    private Date date_validation;

    @Column(name = "valid")
    private boolean valid;

    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    @Transient
    private boolean canDelete;
    @Transient
    private boolean canUpdate;


}
