package jway.gmao.dao;

import jway.gmao.model.Fournisseur_Service;
import jway.gmao.model.Fournisseur_Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FournisseurServiceRepository extends JpaRepository<Fournisseur_Service, Long> {

    @Query("select p from Fournisseur_Service p where p.etatSupp = ?1")
    List<Fournisseur_Service> findAllSocietes(boolean etatSupp);

    @Query("select p from Fournisseur_Service p where p.etatSupp = ?1 and p.fournisseur=1")
    List<Fournisseur_Service> findAllFournisseurs(boolean etatSupp);

   @Query(value="select p.* from fournisseur_service p inner join jw_categorie_fournisseur_service cf on (cf.fk_soc = p.id) where p.etat_supp = ?1 and p.fournisseur=1 and cf.fk_categorie = ?2",nativeQuery = true)
    List<Fournisseur_Service> findAllSoustraitant(boolean etatSupp, Fournisseur_Categorie cat);

    @Query("select p from Fournisseur_Service p where p.id = ?1")
    Fournisseur_Service findSocieteByID(long etatSupp);


}
