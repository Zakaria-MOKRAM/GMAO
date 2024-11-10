package jway.gmao.dao;

import jway.gmao.model.Entrepot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EntrepotRepository extends JpaRepository<Entrepot, Long> {

    @Query("select p from Entrepot p where p.etatSupp is ?1")
    List<Entrepot> findAllEntrepots(boolean etatSupp);

    @Query("select p from Entrepot p where p.id = ?1")
    Entrepot findEntrepotByID(long etatSupp);
}
