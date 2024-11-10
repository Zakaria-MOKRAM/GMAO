package jway.gmao.dao;

import jway.gmao.model.Bon_Sortie_Status;
import jway.gmao.model.Contrat_Maintenance;
import jway.gmao.model.Contrat_Maintenance_Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContratMaintenanceStatusRepository extends JpaRepository<Contrat_Maintenance_Statut, Long> {

    @Query("select p from Contrat_Maintenance_Statut p")
    List<Contrat_Maintenance_Statut> finAllStatus();


    @Query("select p from Contrat_Maintenance_Statut p where p.id = ?1")
    Contrat_Maintenance_Statut findStatusByID(long etatSupp);
}
