package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "equipement_article")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Equipement_Article implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Article article;
    @ManyToOne
    private Equipement equipement;
    @Transient
    private boolean canDelete;
    @Transient
    private boolean canUpdate;
}
