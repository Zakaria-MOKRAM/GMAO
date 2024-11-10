package jway.gmao.dao;

import jway.gmao.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

    @Query("select p from Zone p where p.etatSupp = ?1  order by p.libelle asc")
    List<Zone> findAllZones(boolean etatSupp);
    
    @Query("select p from Zone p where p.id = ?1")
    Zone findZoneByID(long etatSupp);
}
