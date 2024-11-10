package jway.gmao.dao;

import jway.gmao.model.Bon_Sortie;
import jway.gmao.model.Bon_Sortie_Detail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BonSortieDetailRepository extends JpaRepository<Bon_Sortie_Detail, Long> {

    @Query("select p from Bon_Sortie_Detail p where p.etatSupp is ?1")
    List<Bon_Sortie_Detail> findAllBon_Sorties(boolean etatSupp);


    @Query("select p from Bon_Sortie_Detail p where p.etatSupp is ?1 and p.bs =?2")
    List<Bon_Sortie_Detail> findAllBon_SortiesByBs(boolean s,Bon_Sortie br);


    @Query("select p from Bon_Sortie_Detail p where p.id = ?1")
    Bon_Sortie_Detail findBon_SortieByID(long etatSupp);


    @Query("select sum(s.article.pmp*s.qte) from Bon_Sortie_Detail s where s.etatSupp = ?1 and s.bs.intervention <> null")
    Double findCoutTotalBS(boolean etat);
}
