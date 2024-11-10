package jway.gmao.dao;

import jway.gmao.model.Equipement;
import jway.gmao.model.Equipement_PJ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Equipement_PJRepository extends JpaRepository<Equipement_PJ, Long> {

     @Query("select p from Equipement_PJ p where p.etatSupp = ?1 and p.equipement =?2")
    List<Equipement_PJ> findAllEquipementsByEquipement(boolean etatSupp, Equipement user);

    @Query("select p from Equipement_PJ p where p.id = ?1")
    Equipement_PJ findEquipement_PByID(long id);

    @Query(value = "select p.nom_pj from Equipement_PJ p where p.equipement = ?2 and p.etatSupp = ?1", nativeQuery = false)
    public List<String> findNamesByEquipement(boolean etatSupp,Equipement id);

    @Query("select p from Equipement_PJ p where p.etatSupp = ?1 and p.equipement =?2")
    Equipement_PJ findEquipementByEquipement(boolean etatSupp, Equipement user);
}
