package jway.gmao.dao;

import jway.gmao.model.Entrepot;
import jway.gmao.model.SousTraitance_Historique_Statut;
import jway.gmao.model.Sous_Traitance;
import jway.gmao.model.Sous_Traitance_Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface SousTraitanceStatusHistoRepository extends JpaRepository<SousTraitance_Historique_Statut, Long> {

    @Query("select p from SousTraitance_Historique_Statut p")
    List<SousTraitance_Historique_Statut> findAllStatus();

    @Query("select p from SousTraitance_Historique_Statut p where p.sous_traitance = ?1")
    List<SousTraitance_Historique_Statut> findStatusByST(Sous_Traitance id);


    @Modifying
    @Transactional
    @Query(value = "update SousTraitance_Historique_Statut set actual = false where sous_traitance=?1", nativeQuery = false)
    void updateOldStatusByST(Sous_Traitance st);

}
