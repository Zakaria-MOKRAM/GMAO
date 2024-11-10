package jway.gmao.dao;

import jway.gmao.model.Equipement_Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EquipementStatutRepository extends JpaRepository<Equipement_Status, Long> {

    @Query("select p from Equipement_Status p where p.etatSupp = ?1")
    List<Equipement_Status> findAllStatus(boolean status);

    @Query("select p from Equipement_Status p where p.id = ?1")
    Equipement_Status findEquipement_StatusByID(long id);
}
