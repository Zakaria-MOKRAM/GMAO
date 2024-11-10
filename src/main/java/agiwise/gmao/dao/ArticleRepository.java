package jway.gmao.dao;

import jway.gmao.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("select p from Article p where p.etatSupp is ?1")
    List<Article> findAllArticles(boolean etatSupp);

    @Query("select p from Article p where p.unite = ?1")
    List<Article> findAllArticlesByUnite(Unite u);

    @Query("select p from Article p where p.etatSupp = ?1 and p not in (select a.article from Equipement_Article a where a.equipement =?2) and p.has_compteur in ('sans compteur','propre compteur')")
    List<Article> findArticlesNotAffectNotCompteur(boolean etatSupp, Equipement e);

    @Query("select p from Article p where p.etatSupp = ?1 and p not in (select a.article from Equipement_Article a where a.equipement =?2)")
    List<Article> findArticlesNotAffect(boolean etatSupp, Equipement e);

    @Query("select p from Article p where p.etatSupp = ?1 and p not in (select a.article from Bon_Reception_Detail a where a.br =?2 and a.etatSupp is false)")
    List<Article> findArticlesNotAffectBR(boolean etatSupp, Bon_Reception e);

    @Query("select p from Article p where p.etatSupp = ?1 and p not in (select a.article from Bon_Sortie_Detail a where a.bs =?2 and a.etatSupp is false)")
    List<Article> findArticlesNotAffectBS(boolean etatSupp, Bon_Sortie e);

    @Query("select p from Article p where p.etatSupp = ?1 and p not in (select a.article from Inv_Physique_Details a where a.inv_physique =?2 and a.etatSupp is false)")
    List<Article> findArticlesNotAffectINV(boolean etatSupp, Inv_Physique e);

    @Query("select p from Article p where p.id = ?1")
    Article findArticleByID(long etatSupp);


    @Query("select p from Article p where p.etatSupp = ?1 and p.type_compteur='nombre' and (p.seuil_compteur-10) <= p.valeur_actuelle_compteur")
    List<Article> findArticlesKMToChange(boolean etatSupp);

    @Query(value = "select p.* from article p where p.etat_supp = ?1 and p.type_compteur='duree' and p.date_fin_compteur> ?2 AND p.date_fin_compteur <= DATE_ADD(?2, interval 10 day)",nativeQuery = true)
    List<Article> findArticlesDureeToChange(boolean etatSupp, Date d);
}
