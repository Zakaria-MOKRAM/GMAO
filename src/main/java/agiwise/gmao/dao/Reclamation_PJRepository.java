package jway.gmao.dao;

import jway.gmao.model.Reclamation;
import jway.gmao.model.Reclamation_PJ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Reclamation_PJRepository extends JpaRepository<Reclamation_PJ, Long> {

     @Query("select p from Reclamation_PJ p where p.etatSupp is ?1 and p.reclamation =?2")
    List<Reclamation_PJ> findAllReclamationsByReclamation(boolean etatSupp, Reclamation user);

    @Query("select p from Reclamation_PJ p where p.id = ?1")
    Reclamation_PJ findReclamation_PByID(long id);

    @Query(value = "select p.nom_pj from Reclamation_PJ p where p.reclamation = ?2 and p.etatSupp is ?1", nativeQuery = false)
    public List<String> findNamesByReclamation(boolean etatSupp,Reclamation id);
}
