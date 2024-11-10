package jway.gmao.dao;

import jway.gmao.model.Article;
import jway.gmao.model.Bon_Sortie;
import jway.gmao.model.Equipement;
import jway.gmao.model.Equipement_Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EquipementArticleRepository extends JpaRepository<Equipement_Article, Long> {

    @Query("select p from Equipement_Article p where p.id =?1")
    Equipement_Article findByID(long id);

     @Query("select p from Equipement_Article p where p.article =?1")
    List<Equipement_Article> findAllByArticle(Article article);

    @Query("select p from Equipement_Article p where p.equipement =?1")
    List<Equipement_Article> findAllByEquipement(Equipement e);

    @Query("select p.article from Equipement_Article p where p.equipement = ?1 and p.article not in (select a.article from Bon_Sortie_Detail a where a.bs =?2 and a.etatSupp is false)")
    List<Article> findArticlesNotAffectBS(Equipement equ, Bon_Sortie e);
}
