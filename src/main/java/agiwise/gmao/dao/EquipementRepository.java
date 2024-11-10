package jway.gmao.dao;

import jway.gmao.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface EquipementRepository extends JpaRepository<Equipement, Long> {

    @Query("select p from Equipement p where p.etatSupp = ?1")
    List<Equipement> findAllEquipements(boolean etatSupp);

    @Query("select p.code from Equipement p ")
    List<String> findAllRef();

    @Query("select p from Equipement p where p.etatSupp = ?1 and p not in (select d.equipement from Installation_Detail d where d.installation =?2 and d.etatSupp is false)")
    List<Equipement> findAllEquipementsNotInInstallation(boolean etatSupp, Installation in);

     @Query("select p from Equipement p where p.etatSupp = ?1 and p not in (select d.equipement from Contrat_Maintenance_Detail d where d.contrat =?2 and d.etatSupp is false)")
    List<Equipement> findAllEquipementsNotContrat(boolean etatSupp, Contrat_Maintenance in);

    @Query("select p from Equipement p where (p.emplacement=?1 or p.installation in (select i from Installation i where i.emplacement=?1)) and p.etatSupp = ?2")
    List<Equipement> findAllEquipementsByEmplacement(Emplacement e , boolean etatSupp);

    @Query("select p from Equipement p where p.type=?1 and p.etatSupp = ?2")
    List<Equipement> findAllEquipementsByType(Equipement_Type e , boolean etatSupp);

    @Query("select p from Equipement p where p.marque=?1 and p.etatSupp = ?2")
    List<Equipement> findAllEquipementsByMarque(Marque e , boolean etatSupp);

    @Query("select p from Equipement p where p.modele=?1 and p.etatSupp = ?2")
    List<Equipement> findAllEquipementsByModele(Modele e , boolean etatSupp);

    @Query("select p from Equipement p where p.status=?1 and p.etatSupp = ?2")
    List<Equipement> findAllEquipementsByStatus(Equipement_Status e , boolean etatSupp);

    @Query("select p from Equipement p where p.categorie=?1 and p.etatSupp = ?2")
    List<Equipement> findAllEquipementsByCategorie(Categorie e , boolean etatSupp);

    @Query("select p from Equipement p where p.installation=?1 and p.etatSupp = ?2")
    List<Equipement> findAllEquipementsByInstallation(Installation e , boolean etatSupp);

    @Query("select p from Equipement p where p.id = ?1")
    Equipement findEquipementByID(long etatSupp);

    @Query("select p from Equipement p where p.etatSupp = ?1 and p.type_compteur='nombre' and (p.seuil_compteur-10) <= p.valeur_actuelle_compteur")
    List<Equipement> findEquipementsKMToChange(boolean etatSupp);

    @Query(value = "select p.* from equipement p where p.etat_supp = ?1 and p.type_compteur='duree' and p.date_fin_compteur> ?2 AND p.date_fin_compteur <= DATE_ADD(?2, interval 10 day)",nativeQuery = true)
    List<Equipement> findEquipementsDureeToChange(boolean etatSupp, Date d);


    @Query("select s.libelle,count(p) from Equipement p inner join Equipement_Status s on (s=p.status) where p.etatSupp = ?1 group by s.libelle")
    List<Object[]> findCountEquipementsByStatus(boolean etatSupp);


}
