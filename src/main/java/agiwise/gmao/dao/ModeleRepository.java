package jway.gmao.dao;

import jway.gmao.model.Modele;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ModeleRepository extends JpaRepository<Modele, Long> {

    @Query("select p from Modele p where p.etatSupp = ?1  order by p.libelle asc")
    List<Modele> findAllModeles(boolean etatSupp);
    
    @Query("select p from Modele p where p.id = ?1")
    Modele findModeleByID(long etatSupp);
}
