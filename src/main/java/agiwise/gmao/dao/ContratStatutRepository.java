package jway.gmao.dao;

import jway.gmao.model.Contrat_Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContratStatutRepository extends JpaRepository<Contrat_Status, Long> {

    @Query("select p from Contrat_Status p")
    List<Contrat_Status> findAllStatus();

    @Query("select p from Contrat_Status p where p.id = ?1")
    Contrat_Status findContrat_StatusByID(long id);
}
