package jway.gmao.dao;

import jway.gmao.model.Marque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MarqueRepository extends JpaRepository<Marque, Long> {

    @Query("select p from Marque p where p.etatSupp = ?1  order by p.libelle asc")
    List<Marque> findAllMarques(boolean etatSupp);
    
    @Query("select p from Marque p where p.id = ?1")
    Marque findMarqueByID(long etatSupp);
}
