package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "jw_c_currencies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Devise implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String code_iso;
    private String label;
    private String unicode;
    @Column(name = "etat_supp",columnDefinition = "tinyint default '0'")
    private boolean etatSupp = false;
    @Column(name = "active",columnDefinition = "tinyint(4) default '1'")
    private boolean active = false;
}
