package jway.gmao.dao;

import jway.gmao.model.Emplacement;
import jway.gmao.model.Installation;
import jway.gmao.model.Installation_Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InstallationRepository extends JpaRepository<Installation, Long> {

    @Query("select p.code from Installation p ")
    List<String> findAllRef();

    @Query("select p from Installation p where p.etatSupp is ?1")
    List<Installation> findAllInstallations(boolean etatSupp);

    @Query("select p from Installation p where p.emplacement=?1 and p.etatSupp is ?2")
    List<Installation> findAllInstallationsByEmplacement(Emplacement e , boolean etatSupp);

    @Query("select p from Installation p where p.status=?1 and p.etatSupp is ?2")
    List<Installation> findAllInstallationsByStatus(Installation_Status e , boolean etatSupp);

    @Query("select p from Installation p where p.id = ?1")
    Installation findInstallationByID(long etatSupp);


    @Query("select s.libelle,count(p) from Installation p inner join Installation_Status s on (s=p.status) where p.etatSupp = ?1 group by s.libelle")
    List<Object[]> findCountInstallationsByStatus(boolean etatSupp);
}
