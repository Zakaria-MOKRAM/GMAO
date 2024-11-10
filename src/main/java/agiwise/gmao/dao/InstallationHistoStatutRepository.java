package jway.gmao.dao;

import jway.gmao.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface InstallationHistoStatutRepository extends JpaRepository<Installation_Historique_Statut, Long> {

    @Query("select p from Installation_Historique_Statut p where p.installation = ?1  order by p.date desc")
    List<Installation_Historique_Statut> findAllByInstallation(Installation installation);

    @Modifying
    @Transactional
    @Query(value = "update Installation_Historique_Statut set actual = false where installation=?1", nativeQuery = false)
    void updateOldStatusByInstallation(Installation st);
}
