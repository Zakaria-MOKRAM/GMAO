package jway.gmao.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "menus")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private int level;
    private int ordre;
    private String url;
    private String icon;
    private String icon_mobile;
    private String code;
    private long parent_id;
    @Transient
    private List<Menu> children;
    @Transient
    private UserPrivilege privilege;
    @Transient
    private Privilege priv;
}
