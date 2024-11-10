package jway.gmao.dao;

import jway.gmao.model.Equipement;
import jway.gmao.model.Installation;
import jway.gmao.model.Installation_Detail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InstallationDetailRepository extends JpaRepository<Installation_Detail, Long> {

    @Query("select p from Installation_Detail p where p.installation = ?1")
    List<Installation_Detail> findAllByInstallation(Installation in);

    @Query("select p from Installation_Detail p where p.id = ?1")
    Installation_Detail findInstallation_DetailByID(long etatSupp);

    @Query("select p from Installation_Detail p where p.equipement = ?1")
    List<Installation_Detail> findAllInstallationsDetailsByEquipement(Equipement e);


}
