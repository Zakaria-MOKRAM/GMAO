package jway.gmao.dao;

import jway.gmao.model.Emplacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmplacementRepository extends JpaRepository<Emplacement, Long> {

    @Query("select p from Emplacement p where p.etatSupp = ?1 ORDER BY p.id DESC")
    List<Emplacement> findAllEmplacemets(boolean etatSupp);

    @Query("select code from Emplacement where etatSupp = ?1")
    List<String> findCodeEmplacements(boolean etatSupp);

    @Query("select p from Emplacement p where p.id = ?1")
    Emplacement findEmplacementByID(long etatSupp);
}
