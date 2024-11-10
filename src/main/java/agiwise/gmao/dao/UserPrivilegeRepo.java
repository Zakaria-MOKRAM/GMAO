package jway.gmao.dao;

import jway.gmao.model.Menu;
import jway.gmao.model.UserPrivilege;
import jway.gmao.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserPrivilegeRepo extends JpaRepository<UserPrivilege, Long> {


    @Query("select up.menu.code from UserPrivilege up where up.user = ?1")
    List<String> findAssociatedCodes(Utilisateur user);

    @Query("select up.menu from UserPrivilege up where up.user = ?1")
    List<Menu> findMenusAffectedToUtilisateur(Utilisateur user);


    @Query("select up.menu from UserPrivilege up where up.user = ?1 and up.menu.level = 0")
    List<Menu> findMenusByUtilisateur(Utilisateur user);

    @Query("select up.menu.id from UserPrivilege up where up.user = ?1 and up.menu.level = 0")
    List<Integer> findMenuIdsByUtilisateur(Utilisateur user);

    @Query("select up.menu from UserPrivilege up where up.user = ?1 and up.menu.level = 1")
    List<Menu> findChildMenusByUtilisateur(Utilisateur user);

    @Query("select up from UserPrivilege up where up.user = ?1")
    List<UserPrivilege> findPrivilegesByUtilisateur(Utilisateur user);

    @Query("select up from UserPrivilege up where up.user = ?1 and up.menu = ?2")
    UserPrivilege findPrivilegeByUtilisateurAndMenu(Utilisateur user, Menu menu);

    @Query("select up from UserPrivilege up where up.user = ?1 and up.menu.parent_id = ?2")
    List<UserPrivilege> findChildMenusPriveleges(Utilisateur user, long menu);

    @Query("select up from UserPrivilege up where up.user.id = ?1 and up.menu.code = ?2")
    UserPrivilege findPrivilegeByUtilisateurAndMenuCode(Long user, String menu);

}
