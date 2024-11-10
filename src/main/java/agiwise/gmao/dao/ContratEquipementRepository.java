package jway.gmao.dao;

import jway.gmao.model.Contrat_Equipement;
import jway.gmao.model.Contrat_Type;
import jway.gmao.model.Equipement;
import jway.gmao.model.Fournisseur_Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContratEquipementRepository extends JpaRepository<Contrat_Equipement, Long> {

    @Query("select p from Contrat_Equipement p where p.etatSupp is ?1")
    List<Contrat_Equipement> findAllContrat_Equipements(boolean etatSupp);

    @Query("select p from Contrat_Equipement p where p.etatSupp = ?1 and p.societe=?2  order by p.debut desc")
    List<Contrat_Equipement> findAllContrat_EquipementsByFournisseur(boolean etatSupp, Fournisseur_Service soc);

    @Query("select p from Contrat_Equipement p where p.etatSupp = ?1 and p.equipement=?2")
    List<Contrat_Equipement> findAllContrat_EquipementsByEquipement(boolean etatSupp,Equipement in);

    @Query("select p from Contrat_Equipement p where p.etatSupp = ?1 and p.equipement=?2 and p.societe=?3")
    List<Contrat_Equipement> findAllContrat_EquipementsByEquipement(boolean etatSupp,Equipement in,Fournisseur_Service soc);

    @Query("select p from Contrat_Equipement p where p.contrat_type=?1")
    List<Contrat_Equipement> findAllContrat_EquipementsByType(Contrat_Type type);

     @Query("select p from Contrat_Equipement p where p.societe=?1")
    List<Contrat_Equipement> findAllContrat_EquipementsBySociete(Fournisseur_Service type);

    @Query("select p from Contrat_Equipement p where p.id = ?1")
    Contrat_Equipement findContrat_EquipementByID(long etatSupp);
}
