package jway.gmao.dao;

import jway.gmao.model.Article;
import jway.gmao.model.Article_Stock;
import jway.gmao.model.Entrepot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ArticleStockRepository extends JpaRepository<Article_Stock, Long> {

    @Query(value = "select s from Article_Stock s where s.entrepot = ?1 and s.article =?2", nativeQuery = false)
    Article_Stock findStockByEntrepotAndArticle(Entrepot e, Article a);

    @Query(value = "select s from Article_Stock s where (s.entrepot = ?1 or ?1 is null) ", nativeQuery = false)
    List<Article_Stock> findStockByEntrepot(Entrepot e);

    @Query(value = "select s from Article_Stock s where (s.entrepot = ?1 or ?1 is null) and (s.article = ?2 or ?2 is null)", nativeQuery = false)
    List<Article_Stock> findStockByEntrepot(Entrepot e, Article a);

    @Modifying
    @Query(value = "DELETE FROM Article_Stock where entrepot = ?1", nativeQuery = false)
    @Transactional
    void viderTablebyEntrepot(Entrepot e);


    @Query(value = "select distinct s.article.id from Article_Stock s", nativeQuery = false)
    public List<Long> findProducts();


    @Query(value = "select distinct s.article.libelle from Article_Stock s", nativeQuery = false)
    public List<String> findProductsNames();


    @Query(value = "select sum(s.stock_reel * a.pmp) from Article_Stock s left join Article a on a.id=s.article.id where s.entrepot.id=?1 and s.article.id = ?2", nativeQuery = false)
    Double findValeurStockByProductAndEntrepot(long id,long product_id);


}
