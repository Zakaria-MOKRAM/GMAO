package jway.gmao.dao;

import jway.gmao.model.Bon_Sortie_Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BonSortieStatusRepository extends JpaRepository<Bon_Sortie_Status, Long> {

    @Query("select p from Bon_Sortie_Status p")
    List<Bon_Sortie_Status> finAllStatus();


    @Query("select p from Bon_Sortie_Status p where p.id = ?1")
    Bon_Sortie_Status findStatusByID(long etatSupp);
}
