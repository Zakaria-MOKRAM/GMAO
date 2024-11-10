package jway.gmao.dao;

import jway.gmao.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface ContratHistoRepository extends JpaRepository<Contrat_Historique_Statut, Long> {

    @Query("select p from Contrat_Historique_Statut p where p.contrat = ?1  order by p.date desc")
    List<Contrat_Historique_Statut> findAllByContrat(Contrat_Maintenance equipement);

    @Modifying
    @Transactional
    @Query(value = "update Contrat_Historique_Statut set actual = false where contrat=?1", nativeQuery = false)
    void updateOldStatusByContrat(Contrat_Maintenance st);
}
