package jway.gmao.dao;

import jway.gmao.model.Equipement_Type;
import jway.gmao.model.Maintenance_Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MaintenanceTypeRepository extends JpaRepository<Maintenance_Type, Long> {

    @Query("select p from Maintenance_Type p where p.etatSupp = ?1")
    List<Maintenance_Type> findAllTypes(boolean etatSupp);
    
    @Query("select p from Maintenance_Type p where p.id = ?1")
    Maintenance_Type findTypeByID(long id);
}
