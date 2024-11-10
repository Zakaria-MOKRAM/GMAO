package jway.gmao.dao;

import jway.gmao.model.Intervention_Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InterventionStatutRepository extends JpaRepository<Intervention_Status, Long> {

    @Query("select p from Intervention_Status p")
    List<Intervention_Status> findAllStatus();

    @Query("select p from Intervention_Status p where p.id = ?1")
    Intervention_Status findIntervention_StatusByID(long id);
}
