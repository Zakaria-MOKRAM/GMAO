package jway.gmao.dao;

import jway.gmao.model.Intervention;
import jway.gmao.model.Intervention_Tache;
import jway.gmao.model.Ouvrier;
import jway.gmao.model.Tache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InterventionTacheRepository extends JpaRepository<Intervention_Tache, Long> {

    @Query("select p from Intervention_Tache p where p.etatSupp is ?1 and p.intervention=?2")
    List<Intervention_Tache> findIntervention_TacheByIntervention(boolean etat, Intervention i);

    @Query("select p from Intervention_Tache p where p.id=?1")
    Intervention_Tache findIntervention_TacheByID(long i);

    @Query("select p from Intervention_Tache p where p.ouvrier=?1")
    List<Intervention_Tache> findIntervention_TacheByOuvrier(Ouvrier i);

    @Query("select p from Intervention_Tache p where p.tache=?1")
    List<Intervention_Tache> findIntervention_TacheByTache(Tache i);


    @Query("select sum(s.duree*s.ouvrier.thm) from Intervention_Tache s where s.etatSupp = ?1 and s.intervention <> null")
    Double findCoutTotal(boolean etat);

}
