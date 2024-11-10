package jway.gmao.dao;

import jway.gmao.model.Fournisseur_Service;
import jway.gmao.model.Intervention;
import jway.gmao.model.Sous_Traitance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SousTraitanceRepository extends JpaRepository<Sous_Traitance, Long> {

    @Query("select p from Sous_Traitance p where p.etatSupp = ?1")
    List<Sous_Traitance> findAllSous_Traitances(boolean etatSupp);

    @Query("select p from Sous_Traitance p where p.etatSupp = ?1 and p.intervention =?2")
    List<Sous_Traitance> findAllSous_TraitancesByIntervention(boolean etatSupp, Intervention intervention);

    @Query("select p from Sous_Traitance p where p.etatSupp = ?1 and p.sous_traitant =?2 order by p.date desc")
    List<Sous_Traitance> findAllSous_TraitancesByFournisseur(boolean etatSupp, Fournisseur_Service soc);

    @Query("select p from Sous_Traitance p where p.id = ?1")
    Sous_Traitance findSous_TraitanceByID(long etatSupp);

    @Query("select sum(s.montant) from Sous_Traitance s where s.etatSupp = ?1 and s.intervention <> null")
    Double findCoutTotalST(boolean etat);
}
