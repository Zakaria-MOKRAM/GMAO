package jway.gmao.dao;

import jway.gmao.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface EquipementHistoEmplacementRepository extends JpaRepository<Equipement_Historique_Emplacement, Long> {

    @Query("select p from Equipement_Historique_Emplacement p where p.equipement = ?1 order by p.date desc")
    List<Equipement_Historique_Emplacement> findAllByEquipement(Equipement equipement);

    @Modifying
    @Transactional
    @Query(value = "update Equipement_Historique_Emplacement set actual = false where equipement=?1", nativeQuery = false)
    void updateOldHistoByEquipement(Equipement st);
}
