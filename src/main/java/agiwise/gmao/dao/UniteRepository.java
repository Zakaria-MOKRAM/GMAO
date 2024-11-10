package jway.gmao.dao;

import jway.gmao.model.Unite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UniteRepository extends JpaRepository<Unite, Long> {

    @Query("select p from Unite p where p.etatSupp is ?1")
    List<Unite> findAllUnites(boolean etatSupp);

    @Query("select p from Unite p where p.id = ?1")
    Unite findUniteByID(long etatSupp);
}
