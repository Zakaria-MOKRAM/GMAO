package jway.gmao.dao;

import jway.gmao.model.Sous_Traitance_Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SousTraitanceStatusRepository extends JpaRepository<Sous_Traitance_Status, Long> {

    @Query("select p from Sous_Traitance_Status p")
    List<Sous_Traitance_Status> finAllStatus();


    @Query("select p from Sous_Traitance_Status p where p.id = ?1")
    Sous_Traitance_Status findStatusByID(long etatSupp);
}
