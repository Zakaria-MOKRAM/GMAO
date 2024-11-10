package jway.gmao.dao;

import jway.gmao.model.Equipement;
import jway.gmao.model.Equipement_Params;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EquipementParamsRepository extends JpaRepository<Equipement_Params, Long> {

    @Query("select p from Equipement_Params p where p.equipement=?1 and p.etatSupp is ?2")
    List<Equipement_Params> findAllByEquipements(Equipement e, boolean etat);

    @Query("select p from Equipement_Params p where p.id=?1 ")
    Equipement_Params findEquipementByID(long id);


}
