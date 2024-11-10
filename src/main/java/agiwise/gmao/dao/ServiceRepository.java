package jway.gmao.dao;

import jway.gmao.model.Service_Travaux;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service_Travaux, Long> {

    @Query("select p from Service_Travaux p where p.etatSupp is ?1")
    List<Service_Travaux> findAllService_Travauxs(boolean etatSupp);

    @Query("select p from Service_Travaux p where p.id = ?1")
    Service_Travaux findService_TravauxByID(long etatSupp);
}
