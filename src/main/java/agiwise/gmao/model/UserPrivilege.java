package jway.gmao.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "user_pivileges")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPrivilege implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@ManyToOne
	private Utilisateur user;
	@ManyToOne
	private Menu menu;
	@ManyToOne
	private Privilege privilege;
	
	
	
}
