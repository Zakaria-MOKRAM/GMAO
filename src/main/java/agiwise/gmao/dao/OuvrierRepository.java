package jway.gmao.dao;

import jway.gmao.model.Intervention;
import jway.gmao.model.Ouvrier;
import jway.gmao.model.Service_Travaux;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OuvrierRepository extends JpaRepository<Ouvrier, Long> {

    @Query("select p from Ouvrier p where p.etatSupp is ?1")
    List<Ouvrier> findAllOuvriers(boolean etatSupp);

    @Query("select p.cin from Ouvrier p where p.etatSupp is ?1")
    List<String> findAllCIN(boolean etatSupp);

    @Query("select p from Ouvrier p where p.service = ?1")
    List<Ouvrier> findAllOuvriersByService(Service_Travaux s);

    @Query("select p from Ouvrier p where p.disponible is true and p.etatSupp = ?1 and p not in (select a.ouvrier from Intervention_Tache a where a.intervention =?2 and a.etatSupp is false)")
    List<Ouvrier> findOuvriersNotAffect(boolean etatSupp, Intervention e);

    @Query("select p from Ouvrier p where p.etatSupp = ?1")
    List<Ouvrier> findOuvriers(boolean etatSupp);

    @Query("select p from Ouvrier p where p.id = ?1")
    Ouvrier findOuvrierByID(long etatSupp);
}
