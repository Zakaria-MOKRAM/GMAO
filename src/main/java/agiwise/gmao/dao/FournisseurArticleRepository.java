package jway.gmao.dao;

import jway.gmao.model.Fournisseur_Article;
import jway.gmao.model.Fournisseur_Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FournisseurArticleRepository extends JpaRepository<Fournisseur_Article, Long> {

    @Query("select p from Fournisseur_Article p where p.etatSupp = ?1")
    List<Fournisseur_Article> findAllSocietes(boolean etatSupp);

    @Query("select p from Fournisseur_Article p where p.etatSupp = ?1 and p.fournisseur=1")
    List<Fournisseur_Article> findAllFournisseurs(boolean etatSupp);

    @Query(value="select p.* from fournisseur_article p inner join jw_categorie_fournisseur_article cf on (cf.fk_soc = p.id) where p.etat_supp = ?1 and p.fournisseur=1 and cf.fk_categorie = ?2",nativeQuery = true)
    List<Fournisseur_Article> findAllSoustraitant(boolean etatSupp, Fournisseur_Categorie cat);

    @Query("select p from Fournisseur_Article p where p.id = ?1")
    Fournisseur_Article findSocieteByID(long etatSupp);


}
