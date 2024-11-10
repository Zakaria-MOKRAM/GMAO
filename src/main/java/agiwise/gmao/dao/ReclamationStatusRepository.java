package jway.gmao.dao;

import jway.gmao.model.Reclamation_Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReclamationStatusRepository extends JpaRepository<Reclamation_Statut, Long> {

    @Query("select p from Reclamation_Statut p")
    List<Reclamation_Statut> finAllStatus();


    @Query("select p from Reclamation_Statut p where p.id = ?1")
    Reclamation_Statut findStatusByID(long etatSupp);
}
