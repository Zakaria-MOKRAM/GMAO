package jway.gmao.dao;

import jway.gmao.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface ReclamationHistoStatutRepository extends JpaRepository<Reclamation_Historique_Statut, Long> {

    @Query("select p from Reclamation_Historique_Statut p where p.reclamation = ?1  order by p.date desc")
    List<Reclamation_Historique_Statut> findAllByReclamation(Reclamation equipement);

    @Modifying
    @Transactional
    @Query(value = "update Reclamation_Historique_Statut set actual = false where reclamation=?1", nativeQuery = false)
    void updateOldStatusByReclamation(Reclamation st);
}
