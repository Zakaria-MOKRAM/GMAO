package jway.gmao.dao;

import jway.gmao.model.RoleUtilisateur;
import jway.gmao.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UtilisateurRepo extends JpaRepository<Utilisateur, Long> {

    Utilisateur findUtilisateurById(long id);

    Utilisateur findByUsername(String username);

    Utilisateur findByToken(String token);

    @Query(value = "select t from RoleUtilisateur t where t.etatSupp=false and  t.id = 2")
    RoleUtilisateur getRoleAdmin();

    @Query(value = "select distinct c.username from Utilisateur c",nativeQuery = false)
    List<String> findUsernames();


    Utilisateur findById(long id);

    @Query(value = "select u from Utilisateur u where u.etatSupp= ?1 ")
    List<Utilisateur> findByEtat_supp(boolean b);

    @Query(value = "select u.username from Utilisateur u")
    List<String> findUSernames();

    @Query(value = "select t.* from utilisateur t where t.etat_supp = false and t.username = ?1 and t.id != ?2 ", nativeQuery = true)
    Utilisateur findOthersByUsername(String username, Long id);

    @Query("select token from Utilisateur where token =?1")
    String findToken(String token);


    List<Utilisateur> findByEtatSuppFalse();
}
