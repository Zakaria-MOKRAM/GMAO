package jway.gmao.dao;

import jway.gmao.model.Bon_Reception;
import jway.gmao.model.Bon_Reception_Detail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BonReceptionDetailRepository extends JpaRepository<Bon_Reception_Detail, Long> {

    @Query("select p from Bon_Reception_Detail p where p.etatSupp is ?1")
    List<Bon_Reception_Detail> findAllBon_Receptions(boolean etatSupp);


    @Query("select p from Bon_Reception_Detail p where p.etatSupp is ?1 and p.br =?2")
    List<Bon_Reception_Detail> findAllBon_ReceptionsByBR(boolean s,Bon_Reception br);


    @Query("select p from Bon_Reception_Detail p where p.id = ?1")
    Bon_Reception_Detail findBon_ReceptionByID(long etatSupp);
}
