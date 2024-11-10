package jway.gmao.dao;

import jway.gmao.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContratMaintenanceRepository extends JpaRepository<Contrat_Maintenance, Long> {

    @Query("select p from Contrat_Maintenance p where p.etatSupp = ?1 order by p.date_debut desc")
    List<Contrat_Maintenance> findAllContrat_Maintenances(boolean etatSupp);

    @Query("select p.code from Contrat_Maintenance p ")
    List<String> findRefs();

    @Query("select p from Contrat_Maintenance p where p.etatSupp = ?1 and p.fournisseur=?2  order by p.date_debut desc")
    List<Contrat_Maintenance> findAllContrat_MaintenancesByFournisseur(boolean etatSupp, Fournisseur_Service soc);

    @Query("select p from Contrat_Maintenance p where p.id = ?1")
    Contrat_Maintenance findContrat_MaintenanceByID(long etatSupp);

    @Query("select s.libelle,count(p) from Contrat_Maintenance p inner join Contrat_Maintenance_Statut s on (s=p.status) where p.etatSupp = ?1 group by s.libelle")
    List<Object[]> findByStatus(boolean etatSupp);
}
