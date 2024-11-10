package jway.gmao.dao;

import jway.gmao.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProvinceRepository extends JpaRepository<Province, Integer> {

    @Query("select p from Province p where p.etatSupp = ?1 and p.active is true")
    List<Province> findAllProvinces(boolean etatSupp);
    
    @Query("select p from Province p where p.etatSupp = ?1 and p.fk_region in (SELECT code_region FROM Region where fk_pays=?2) and p.active is true order by p.nom asc")
    List<Province> findAllProvincesByPays(boolean etatSupp,int fk_pays);

    @Query("select p from Province p where p.id = ?1")
    Province findProvinceByID(int etatSupp);
}
