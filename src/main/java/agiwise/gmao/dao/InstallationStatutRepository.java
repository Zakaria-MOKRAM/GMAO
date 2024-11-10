package jway.gmao.dao;

import jway.gmao.model.Equipement_Status;
import jway.gmao.model.Installation_Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InstallationStatutRepository extends JpaRepository<Installation_Status, Long> {

    @Query("select p from Installation_Status p where p.etatSupp = ?1")
    List<Installation_Status> findAllStatus(boolean status);

    @Query("select p from Installation_Status p where p.id = ?1")
    Installation_Status findStatusBy(long id);
}
