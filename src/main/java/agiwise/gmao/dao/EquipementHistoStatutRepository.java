package jway.gmao.dao;

import jway.gmao.model.Equipement;
import jway.gmao.model.Equipement_Historique_Installation;
import jway.gmao.model.Equipement_Historique_Statut;
import jway.gmao.model.Sous_Traitance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface EquipementHistoStatutRepository extends JpaRepository<Equipement_Historique_Statut, Long> {

    @Query("select p from Equipement_Historique_Statut p where p.equipement = ?1  order by p.date desc")
    List<Equipement_Historique_Statut> findAllByEquipement(Equipement equipement);

    @Modifying
    @Transactional
    @Query(value = "update Equipement_Historique_Statut set actual = false where equipement=?1", nativeQuery = false)
    void updateOldStatusByEquipement(Equipement st);
}
