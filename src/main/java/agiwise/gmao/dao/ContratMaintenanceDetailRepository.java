package jway.gmao.dao;

import jway.gmao.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContratMaintenanceDetailRepository extends JpaRepository<Contrat_Maintenance_Detail, Long> {

    @Query("select p from Contrat_Maintenance_Detail p where p.etatSupp = ?1")
    List<Contrat_Maintenance_Detail> findAllContrat_Maintenances(boolean etatSupp);

    @Query("select p from Contrat_Maintenance_Detail p where p.etatSupp = ?1 and p.equipement=?2")
    List<Contrat_Maintenance_Detail> findAllContrat_MaintenancesByEquipement(boolean etatSupp, Equipement in);

    @Query("select p from Contrat_Maintenance_Detail p where p.etatSupp = ?1 and p.contrat=?2")
    List<Contrat_Maintenance_Detail> findAllContrat_MaintenancesByContrat(boolean etatSupp, Contrat_Maintenance in);

    @Query("select c.fournisseur from Contrat_Maintenance c where c in (select p.contrat from Contrat_Maintenance_Detail p where p.etatSupp = ?1 and p.equipement=?2 and p.type=?3)")
    List<Fournisseur_Service> findAllFournisseursByEquipement(boolean etatSupp, Equipement in, Maintenance_Type type);

    @Query("select p from Contrat_Maintenance_Detail p where p.id = ?1")
    Contrat_Maintenance_Detail findContrat_MaintenanceByID(long etatSupp);
}
