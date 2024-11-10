package jway.gmao.dao;


import jway.gmao.model.History;
import jway.gmao.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface HistoryRepo extends JpaRepository<History, Long> {

    @Query("SELECT  c from  History  c where c.utilisateur=?1 and c.module=?2 and c.dateCreation=?3 ")
    List<History> findByUserAndModuleAndDate(Utilisateur userSearch, String modulR, Date parse);

    @Query("SELECT  c from  History  c  ")
    List<History> findAllHistoriques();

    @Query("SELECT  c from  History  c where c.utilisateur=?1 and  c.dateCreation=?2 ")
    List<History> findByUserAndDate(Utilisateur userSearch, Date parse);
    @Query("SELECT  c from  History  c where c.module=?1 and  c.dateCreation=?2 ")
    List<History> findByModuleAndDate(String modulR, Date parse);

    @Query("SELECT  c from  History  c where   c.dateCreation=?1 ")
    List<History> findByDate(Date parse);

    @Query("SELECT  c from  History  c where c.utilisateur=?1 and  c.module=?2 ")
    List<History> findByUserAndModule(Utilisateur userSearch, String modulR);
    @Query("SELECT  c from  History  c where c.utilisateur=?1  ")
    List<History> findByUser(Utilisateur userSearch);

    @Query("SELECT  c from  History  c where c.module=?1  ")
    List<History> findByModule(String modulR);


    @Query("SELECT distinct c.module from  History  c   ")
    List<String> findModules();

    @Query("SELECT distinct c.utilisateur from  History  c   ")
    List<Utilisateur> findUsers();



    @Query("SELECT  c from  History  c where c.id=?1  ")
    History findByID(long id);


    @Query(value = "select ha.* from histores ha where "
            + " ((ha.date_creation BETWEEN ?1 and ?2)) "
            + " order by ha.date_creation desc ,ha.id desc limit 100",nativeQuery = true)
    List<History> getTOP100( Date from, Date to);

    @Query(value = "select ha.* from histores ha where "
            + " ((ha.date_creation BETWEEN ?1 and ?2)) "
            + " and (ha.utilisateur_id =?4 or ?4=-1) "
            + " and (ha.module =?3 or ?3 is null) "
            + " order by ha.date_creation desc ,ha.id desc limit 100",nativeQuery = true)
    List<History> getTOP100( Date from, Date to,String module,long user_id);


    @Query("SELECT  c from  History  c where (?1 is null or c.utilisateur=?1) and (?2 is null or c.dateCreation >= ?2) and (?3 is null or c.dateCreation<=?3)")
    List<History> findFiltredHestories(Utilisateur utilisateur,Date debut,Date fin);
}
