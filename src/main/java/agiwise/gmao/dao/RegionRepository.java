package jway.gmao.dao;

import jway.gmao.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {

    @Query("select p from Region p where p.etatSupp = ?1  order by p.nom asc")
    List<Region> findAllRegions(boolean etatSupp);
    
    @Query("select p from Region p where p.etatSupp = ?1 and p.fk_pays=?2 order by p.nom asc")
    List<Region> findAllRegionsByPays(boolean etatSupp,int fk_pays);

    @Query("select p from Region p where p.id = ?1")
    Region findRegionByID(long etatSupp);
}
