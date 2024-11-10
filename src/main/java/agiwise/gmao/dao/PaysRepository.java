package jway.gmao.dao;

import jway.gmao.model.Pays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaysRepository extends JpaRepository<Pays, Integer> {

    @Query("select p from Pays p where p.etatSupp = ?1 and p.active is true order by p.label asc")
    List<Pays> findAllPayss(boolean etatSupp);
    
    @Query("select p from Pays p where p.id = ?1")
    Pays findPaysByID(int etatSupp);
}
