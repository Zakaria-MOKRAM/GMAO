package jway.gmao.dao;

import jway.gmao.model.Activite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActiviteRepository extends JpaRepository<Activite, Integer> {

    @Query("select p from Activite p where p.etatSupp = ?1  order by p.label asc")
    List<Activite> findAllActivites(boolean etatSupp);
    
    @Query("select p from Activite p where p.id = ?1")
    Activite findActiviteByID(int etatSupp);
}
