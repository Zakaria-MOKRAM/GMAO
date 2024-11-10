package jway.gmao.dao;


import jway.gmao.model.RoleUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface RolesRepo extends JpaRepository<RoleUtilisateur, Long>{



    RoleUtilisateur findByName(String rolename);

    List<RoleUtilisateur> findByEtatSupp(boolean b);

    @Query("select ru from RoleUtilisateur  ru where  ru.etatSupp=false and ru.id=?1")
    RoleUtilisateur findRoleUtilisateurById(Long roleId);

    @Query("select ru from RoleUtilisateur  ru where  ru.etatSupp=false and ru.name='ADMIN'")
    RoleUtilisateur finRoleAdmin();
    @Query("select ru from RoleUtilisateur  ru where  ru.etatSupp=false and ru.id=?1")
    RoleUtilisateur findRoleById(long roleId);
}
