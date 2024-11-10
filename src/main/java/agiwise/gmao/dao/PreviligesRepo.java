package jway.gmao.dao;

import jway.gmao.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PreviligesRepo extends JpaRepository<Privilege, Long> {
    @Query("select p from Privilege p where p.id=?1")
    Privilege findPrivilegeById(long droit);
}
