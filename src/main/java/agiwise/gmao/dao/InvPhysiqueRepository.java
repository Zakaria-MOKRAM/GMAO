package jway.gmao.dao;

import jway.gmao.model.Entrepot;
import jway.gmao.model.Inv_Physique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvPhysiqueRepository extends JpaRepository<Inv_Physique, Long> {

    @Query(value = "select max(i.id) from Inv_Physique i where i.valid = true and i.entrepot = ?1", nativeQuery = false)
    Long findMaxIdByEntrepot(Entrepot x);

    @Query(value = "select i from Inv_Physique i where i.id = ?1", nativeQuery = false)
    Inv_Physique findByID(long x);

    @Query("select p from Inv_Physique p where p.etatSupp is ?1")
    List<Inv_Physique> findAllInventaires(boolean etatSupp);
    @Query("select p from Inv_Physique p where p.etatSupp is ?1 and p.valid is false")
    List<Inv_Physique> findAllInventairesEncours(boolean etatSupp);

}
