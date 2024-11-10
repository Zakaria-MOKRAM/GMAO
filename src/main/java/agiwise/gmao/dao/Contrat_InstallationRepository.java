package jway.gmao.dao;

import jway.gmao.model.Contrat_Installation;
import jway.gmao.model.Contrat_Type;
import jway.gmao.model.Fournisseur_Service;
import jway.gmao.model.Installation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Contrat_InstallationRepository extends JpaRepository<Contrat_Installation, Long> {

    @Query("select p from Contrat_Installation p where p.etatSupp = ?1")
    List<Contrat_Installation> findAllContrat_Installations(boolean etatSupp);

    @Query("select p from Contrat_Installation p where p.etatSupp = ?1 and p.societe=?2  order by p.debut desc")
    List<Contrat_Installation> findAllContrat_InstallationsByFournisseur(boolean etatSupp, Fournisseur_Service soc);

    @Query("select p from Contrat_Installation p where p.etatSupp = ?1 and p.installation=?2")
    List<Contrat_Installation> findAllContrat_InstallationsByInstallation(boolean etatSupp,Installation in);

    @Query("select p from Contrat_Installation p where p.etatSupp = ?1 and p.installation=?2 and p.societe=?3")
    List<Contrat_Installation> findAllContrat_InstallationsByInstallation(boolean etatSupp,Installation in,Fournisseur_Service soc);

    @Query("select p from Contrat_Installation p where p.contrat_type=?1")
    List<Contrat_Installation> findAllContrat_InstallationsByType(Contrat_Type type);

     @Query("select p from Contrat_Installation p where p.societe=?1")
    List<Contrat_Installation> findAllContrat_InstallationsBySociete(Fournisseur_Service type);

    @Query("select p from Contrat_Installation p where p.id = ?1")
    Contrat_Installation findContrat_InstallationByID(long etatSupp);
}
