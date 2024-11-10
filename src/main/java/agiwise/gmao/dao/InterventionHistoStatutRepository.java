package jway.gmao.dao;

import jway.gmao.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface InterventionHistoStatutRepository extends JpaRepository<Intervention_Historique_Statut, Long> {

    @Query("select p from Intervention_Historique_Statut p where p.intervention = ?1  order by p.date desc")
    List<Intervention_Historique_Statut> findAllByIntervention(Intervention installation);

    @Modifying
    @Transactional
    @Query(value = "update Intervention_Historique_Statut set actual = false where intervention=?1", nativeQuery = false)
    void updateOldStatusByIntervention(Intervention st);
}
