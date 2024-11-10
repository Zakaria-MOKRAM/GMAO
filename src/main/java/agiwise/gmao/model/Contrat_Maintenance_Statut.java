package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "contrat_maintenance_statut")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contrat_Maintenance_Statut implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String libelle;
    @Transient
    private boolean canDelete;
    @Transient
    private boolean canUpdate;
}
