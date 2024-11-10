package jway.gmao.dao;

import jway.gmao.model.Article;
import jway.gmao.model.Entrepot;
import jway.gmao.model.Mouvements_Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface MouvementArticleRepository extends JpaRepository<Mouvements_Article, Long> {

    @Query(value = "select m from Mouvements_Article m where m.ip = 0 and m.entrepot = ?1 and m.article = ?2 and m.is_ip is false order by m.date asc, m.quantite asc", nativeQuery = false)
    List<Mouvements_Article> findSortedCurrentMouvementsByArticleAndEntrepot(Entrepot e,Article a);

    @Query(value = "select m from Mouvements_Article m where (m.entrepot = ?1 or ?1 is null) and (m.article = ?2 or ?2 is null) order by m.date asc, m.id asc", nativeQuery = false)
    List<Mouvements_Article> findMouvementsByArticleAndEntrepot(Entrepot e,Article a);

    @Query(value = "select m from Mouvements_Article m where (m.entrepot = ?1) ", nativeQuery = false)
    List<Mouvements_Article> findMouvementsByEntrepot(Entrepot e);

    @Query(value = "select m from Mouvements_Article m where (m.article = ?1) ", nativeQuery = false)
    List<Mouvements_Article> findMouvementsByArticle(Article e);

    @Modifying
    @Transactional
    @Query(value = "update Mouvements_Article set ip = 1 where ip = 0 and entrepot = ?1 and date <= ?2", nativeQuery = false)
    void updateIPByEntrepotAndDate(Entrepot entrepot , Date d);
}
