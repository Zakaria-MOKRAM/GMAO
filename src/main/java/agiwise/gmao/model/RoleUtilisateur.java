package jway.gmao.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="roles")
public class RoleUtilisateur implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private boolean etatSupp=false;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public boolean isEtatSupp() {
		return etatSupp;
	}

	public void setEtatSupp(boolean etatSupp) {
		this.etatSupp = etatSupp;
	}
}
