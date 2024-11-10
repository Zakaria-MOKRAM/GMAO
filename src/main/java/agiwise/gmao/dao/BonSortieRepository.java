package jway.gmao.dao;

import jway.gmao.model.Bon_Sortie;
import jway.gmao.model.Intervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BonSortieRepository extends JpaRepository<Bon_Sortie, Long> {

    @Query("select p from Bon_Sortie p where p.etatSupp is ?1")
    List<Bon_Sortie> findAllBon_Sorties(boolean etatSupp);

@Query("select p from Bon_Sortie p where p.etatSupp is ?1 and  p.intervention=?2")
    List<Bon_Sortie> findAllBon_SortiesByIntervention(boolean etatSupp, Intervention i );


    @Query("select p from Bon_Sortie p where p.id = ?1")
    Bon_Sortie findBon_SortieByID(long etatSupp);
}
