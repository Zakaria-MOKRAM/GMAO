package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "jw_activite_client")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Activite implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String code;
    private String label;
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
}
