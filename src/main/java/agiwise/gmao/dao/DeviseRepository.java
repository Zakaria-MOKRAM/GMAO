package jway.gmao.dao;

import jway.gmao.model.Devise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeviseRepository extends JpaRepository<Devise, String> {

    @Query("select p from Devise p where p.etatSupp = ?1 and p.active is true order by p.label asc")
    List<Devise> findAllDevises(boolean etatSupp);
    
    @Query("select p from Devise p where p.code_iso = ?1")
    Devise findDeviseByID(String etatSupp);
}
