package jway.gmao.dao;

import jway.gmao.model.Intervention_Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InterventionTypeRepository extends JpaRepository<Intervention_Type, Long> {

    @Query("select p from Intervention_Type p where p.etatSupp is ?1")
    List<Intervention_Type> findAllTypes(boolean etatSupp);

    @Query("select p from Intervention_Type p where p.id = ?1")
    Intervention_Type findIntervention_TypeByID(long etatSupp);
}
