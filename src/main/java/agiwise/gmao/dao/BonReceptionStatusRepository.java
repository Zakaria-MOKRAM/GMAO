package jway.gmao.dao;

import jway.gmao.model.Bon_Reception_Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BonReceptionStatusRepository extends JpaRepository<Bon_Reception_Status, Long> {

    @Query("select p from Bon_Reception_Status p")
    List<Bon_Reception_Status> finAllStatus();


    @Query("select p from Bon_Reception_Status p where p.id = ?1")
    Bon_Reception_Status findStatusByID(long etatSupp);
}
