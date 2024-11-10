package jway.gmao.dao;

import jway.gmao.model.Article;
import jway.gmao.model.Inv_Physique;
import jway.gmao.model.Inv_Physique_Details;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvPhysiqueDetailRepository extends JpaRepository<Inv_Physique_Details, Long> {

    @Query(value = "select s from Inv_Physique_Details s where s.inv_physique = ?1 and s.article=?2", nativeQuery = false)
    Inv_Physique_Details getInv_Physique_DetailsbyIPAndArticle(Inv_Physique e, Article a);

    @Query("select p from Inv_Physique_Details p where p.etatSupp is ?1 and p.inv_physique = ?2")
    List<Inv_Physique_Details> findAllByInventaire(boolean etatSupp, Inv_Physique inv);

    @Query("select p from Inv_Physique_Details p where p.id =?1")
    Inv_Physique_Details findByID(long id);

    @Query(value = "select s from Inv_Physique_Details s where s.inv_physique = ?1 and s.article = ?2", nativeQuery = false)
    Inv_Physique_Details findInv_Physique_DetailsbyIPAndArticle(Inv_Physique inv,Article a);

}
