package jway.gmao.dao;

import jway.gmao.model.Emplacement;
import jway.gmao.model.Notification;
import jway.gmao.model.RoleUtilisateur;
import jway.gmao.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationsRepo extends JpaRepository<Notification, Long> {



    @Query(value = "select n.* from notifications n where n.id in (select u.notif_id from user_notifications u where u.user_id=?1) and n.module =?2 ",nativeQuery = true)
    List<Notification> findAllNotifByUSer(long user_id,String module);

    @Query("select p from Notification p where p.obj_id = ?1 and p.module = ?2")
    List<Notification> findNotificationByObjid(long id,String module);

    @Query("select p from Notification p where p.titre = ?1 and p.module = ?2")
    Notification findNotificationByTitreAndModule(String titre,String module);



}
