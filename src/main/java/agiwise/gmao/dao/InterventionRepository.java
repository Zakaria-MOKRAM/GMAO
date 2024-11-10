package jway.gmao.dao;

import jway.gmao.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface InterventionRepository extends JpaRepository<Intervention, Long> {

    @Query("select p from Intervention p where p.etatSupp = ?1")
    List<Intervention> findAllInterventions(boolean etatSupp);

    @Query("select p from Intervention p where p.etatSupp = ?1 and p.date_debut < ?2 and p.status.id =1 and p.responsable=?3")
    List<Intervention> findLastInterventions(boolean etatSupp, Date date, Utilisateur id);

    @Query(value = "SELECT p.* FROM intervention p WHERE p.etat_supp  = ?1 AND (p.date_debut > ?2 AND p.date_debut <= DATE_ADD(?2, interval 7 day)) AND p.status_id = 1 AND p.responsable_id = ?3",nativeQuery = true)
    List<Intervention> findBientotInterventions(boolean etatSupp, Date date, long id);

    @Query("select p from Intervention p where p.etatSupp = ?1 and p.date_debut =?2")
    List<Intervention> findAllInterventions(boolean etatSupp,Date date);

    @Query(value = "select DATE_FORMAT( p.date_debut,'%Y-%m-%d %H:%m')  AS DateOnly from intervention p where p.etat_supp = ?1",nativeQuery = true)
    List<String> findAllInterventionsDates(boolean etatSupp);

    @Query("select p from Intervention p where p.etatSupp = ?1 and p.status.id=2")
    List<Intervention> findAllInterventionsEncours(boolean etatSupp);

    @Query("select p from Intervention p where p.etatSupp = ?1 and p.installation=?2")
    List<Intervention> findAllInterventionsByInstallation(boolean etatSupp,Installation in);

    @Query("select p from Intervention p where p.etatSupp = ?1 and p.status=?2")
    List<Intervention> findAllInterventionsByStatut(boolean etatSupp, Intervention_Status in);

    @Query("select p from Intervention p where p.etatSupp = ?1 and p.status=?2 and p.type=?3")
    List<Intervention> findAllInterventionsByStatutAndType(boolean etatSupp, Intervention_Status in,Intervention_Type t);

    @Query("select p from Intervention p where p.type=?1 and p.etatSupp=?2")
    List<Intervention> findAllInterventionsByType(Intervention_Type type,boolean b);

    @Query("select p from Intervention p where p.id = ?1")
    Intervention findInterventionByID(long etatSupp);

    @Query("select p from Intervention p where p.contrat_detail_id = ?1")
    Intervention findInterventionByContratDetailID(long id);

    @Query(value = "select COUNT(DISTINCT p.id) from intervention p inner join intervention_histo_statut h on h.intervention_id=p.id where p.etat_supp=0 and EXISTS ( SELECT 1 FROM intervention_histo_statut h2 WHERE h2.intervention_id = p.id GROUP BY h2.intervention_id HAVING COUNT(DISTINCT h2.status_id) > 1 ) ",nativeQuery = true)
    Integer findRecurrentePanne(boolean id);

    @Query(value = "select AVG(TIMESTAMPDIFF(SECOND, p.date_debut, p.date_fin)) AS temps_moyen_reparation from intervention p where p.etat_supp = ?1 and p.status_id  =3",nativeQuery = true)
    Double findTempsMoyen(boolean etat);
    @Query(value = "select MAX(TIMESTAMPDIFF(SECOND, p.date_debut, p.date_fin)) AS temps_moyen_reparation from intervention p where p.etat_supp = ?1 and p.status_id  =3",nativeQuery = true)
    Double findTempsMax(boolean etat);




    @Query("select e.libelle,sum(t.duree*t.ouvrier.thm+s.montant+d.qte*d.article.pmp) from Equipement e " +
            "left join Intervention i on (i.equipement=e) " +
            "left join Intervention_Tache t on (i.equipement=e) " +
            "left join Sous_Traitance s on (i.equipement=e) " +
            "left join Bon_Sortie_Detail d on (i.equipement=e) " +
            "where e.etatSupp = ?1 group by e.libelle")
    List<Object[]> findCoutByEquipement(boolean etatSupp);

    @Query("select ins.libelle,sum(t.duree*t.ouvrier.thm+s.montant+d.qte*d.article.pmp) from Equipement e " +
            "left join Installation ins on (ins=e.installation) " +
            "left join Intervention i on (i.equipement=e) " +
            "left join Intervention_Tache t on (i.equipement=e) " +
            "left join Sous_Traitance s on (i.equipement=e) " +
            "left join Bon_Sortie_Detail d on (i.equipement=e) " +
            "where e.etatSupp = ?1 group by ins.libelle")
    List<Object[]> findCoutByInstallation(boolean etatSupp);


}
