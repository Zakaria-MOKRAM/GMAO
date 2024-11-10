package jway.gmao.dao;

import jway.gmao.model.Activite;
import jway.gmao.model.Equipement_Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EquipementsTypeRepository extends JpaRepository<Equipement_Type, Long> {

    @Query("select p from Equipement_Type p where p.etatSupp = ?1")
    List<Equipement_Type> findAllTypes(boolean etatSupp);
    
    @Query("select p from Equipement_Type p where p.id = ?1")
    Equipement_Type findTypeByID(long id);
}
