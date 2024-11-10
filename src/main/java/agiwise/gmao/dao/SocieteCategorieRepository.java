package jway.gmao.dao;

import jway.gmao.model.Fournisseur_Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface SocieteCategorieRepository extends JpaRepository<Fournisseur_Categorie, Long> {

    @Query("select p from Fournisseur_Categorie p order by p.libelle")
    List<Fournisseur_Categorie> findAll(boolean etatSupp);

    @Query("select p from Fournisseur_Categorie p where p.id = ?1 ")
    Fournisseur_Categorie findByID(long id);

    @Modifying
    @Transactional
    @Query(value="delete from jw_categorie_fournisseur  where fk_soc = ?1",nativeQuery = true)
    void deleteCatsForFournisseur(long societe);

}