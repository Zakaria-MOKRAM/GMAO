package jway.gmao.dao;

import jway.gmao.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuRepo extends JpaRepository<Menu, Long> {
    Menu findMenuById(long id);

    @Query(value = "select c from Menu c where c.level= 0 order by c.ordre")
    List<Menu> findHeaderMenu();

    @Query("select m from Menu m where   m.parent_id = ?1 order by m.ordre")
    List<Menu> findChildMenus(long menu);

    @Query("select m from Menu m where m.level = 2 and m.parent_id = ?1 order by m.ordre")
    List<Menu> findGrandChildrenMenus(long menu);

    @Query(value = "select * from menus c where c.level= 0 order by c.ordre", nativeQuery = true)
    List<Menu> findHeaderPrentMenu();


}
