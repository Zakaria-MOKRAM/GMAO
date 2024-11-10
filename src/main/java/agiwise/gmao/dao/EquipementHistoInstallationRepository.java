package jway.gmao.dao;

import jway.gmao.model.Equipement;
import jway.gmao.model.Equipement_Historique_Emplacement;
import jway.gmao.model.Equipement_Historique_Installation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface EquipementHistoInstallationRepository extends JpaRepository<Equipement_Historique_Installation, Long> {

    @Query("select p from Equipement_Historique_Installation p where p.equipement = ?1  order by p.date desc")
    List<Equipement_Historique_Installation> findAllByEquipement(Equipement equipement);


    @Modifying
    @Transactional
    @Query(value = "update Equipement_Historique_Installation set actual = false where equipement=?1", nativeQuery = false)
    void updateOldHistoByEquipement(Equipement st);
}
