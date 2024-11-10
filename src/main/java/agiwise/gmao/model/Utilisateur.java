package jway.gmao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "utilisateur")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Utilisateur implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    private String lastName;
    private String firstName;
    @Column(columnDefinition = "longtext")
    private String token;
    @Transient
    private List<Map<String, Long>> privileges;
    @Column(name = "etat_supp",columnDefinition = "tinyint default 0")
    private boolean etatSupp = false;
    @ManyToMany
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "roles_id"))
    private Collection<RoleUtilisateur> roles = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "user_notifications", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "notif_id"))
    private Collection<Notification> notifications = new ArrayList<>();

    @Transient
    private List<String> roles_ ;

    @Transient
    private boolean isAdmin;
    @Transient
    private List<String> codes ;
}
