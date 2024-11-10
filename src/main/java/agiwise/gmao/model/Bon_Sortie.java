package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "bon_sortie")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bon_Sortie implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private Date date;
    @ManyToOne
    private Intervention intervention;
    @ManyToOne
    private Bon_Sortie_Status status;
    @ManyToOne
    private Entrepot entrepot;
    @Column(name = "etat_supp",columnDefinition = "tinyint default 0")
    private boolean etatSupp = false;
    @Transient
    private boolean canDelete;
    @Transient
    private boolean canUpdate;
}
