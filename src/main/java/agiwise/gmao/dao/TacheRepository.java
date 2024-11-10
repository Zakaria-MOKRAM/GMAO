package jway.gmao.dao;

import jway.gmao.model.Intervention;
import jway.gmao.model.Tache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TacheRepository extends JpaRepository<Tache, Long> {

    @Query("select p from Tache p where p.etatSupp is ?1")
    List<Tache> findAllTaches(boolean etat);

    @Query("select p from Tache p where p.etatSupp is ?1 and p not in (select t.tache from Intervention_Tache t where t.intervention =?2 and t.etatSupp is false)")
    List<Tache> findAllTachesNotYet(boolean etat, Intervention intervention);

    @Query("select p from Tache p where p.id = ?1")
    Tache findTacheByID(long id);
}
