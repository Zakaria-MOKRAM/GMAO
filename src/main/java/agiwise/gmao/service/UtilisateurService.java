package jway.gmao.service;

import jway.gmao.dao.UserPrivilegeRepo;
import jway.gmao.dao.*;
import jway.gmao.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class UtilisateurService {

    @Autowired
    private InterventionRepository interventionRepository;
    @Autowired
    private UtilisateurRepo utilisateurRepo;

    @Autowired
    private UserPrivilegeRepo userPrivilegeRepo;
    @Autowired
    private MenuRepo menuRepo;
    @Autowired
    private PreviligesRepo priPreviligesRepo;
    @Autowired
    private NotificationsRepo notificationsRepo;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private EquipementRepository equipementRepository;

    @Autowired
    private ArticleRepository articleRepository;

    public Utilisateur addUtilisateur(String firstName, String lastName, String password, String username, RoleUtilisateur role) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setFirstName(firstName);
        utilisateur.setLastName(lastName);
        utilisateur.setUsername(username);
        utilisateur.setPassword(bCryptPasswordEncoder.encode(password));
        Collection<RoleUtilisateur> roles = utilisateur.getRoles();
        roles.add(role);
        utilisateur.setRoles(roles);
        utilisateurRepo.save(utilisateur);

        return utilisateur;
    }

    public Utilisateur editUtilisateur(Utilisateur utilisateur, String firstName, String lastName, String username, RoleUtilisateur role) {

        utilisateur.setFirstName(firstName);
        utilisateur.setLastName(lastName);
        utilisateur.setUsername(username);
        Collection<RoleUtilisateur> roles =new ArrayList<>();
        roles.add(role);
        utilisateur.setRoles(roles);
        utilisateurRepo.save(utilisateur);
        return utilisateur;
    }

    public Utilisateur editPassword(Utilisateur utilisateur, String password) {
        utilisateur.setPassword(bCryptPasswordEncoder.encode(password));
        utilisateurRepo.save(utilisateur);
        return utilisateur;
    }

    public void setDroitMenu(long droit, long id_menu, long id_user) {

        Utilisateur user = utilisateurRepo.findById(id_user);
        Menu menu = menuRepo.findById(id_menu).get();
        Privilege privilege = priPreviligesRepo.findPrivilegeById(droit);
        List<Menu> children = menuRepo.findChildMenus(id_menu);
        // droit menu
        UserPrivilege up = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenu(user, menu);
        if (up != null) {
            up.setPrivilege(privilege);
        } else {
            up = new UserPrivilege();
            up.setMenu(menu);
            up.setPrivilege(privilege);
            up.setUser(user);
        }
        userPrivilegeRepo.save(up);

        // droits sous menus
        if (children.size() > 0) {
            for (Menu submenu : children) {
                UserPrivilege ups = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenu(user, submenu);
                if (ups != null) {
                    ups.setPrivilege(privilege);
                } else {
                    ups = new UserPrivilege();
                    ups.setMenu(submenu);
                    ups.setPrivilege(privilege);
                    ups.setUser(user);
                }
                userPrivilegeRepo.save(ups);
                List<Menu> grandChildren = menuRepo.findChildMenus(submenu.getId());
                // droits sous sous menus
                if (grandChildren.size() > 0) {
                    for (Menu grchild : grandChildren) {
                        UserPrivilege urps = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenu(user, grchild);
                        if (urps != null) {
                            urps.setPrivilege(privilege);
                        } else {
                            urps = new UserPrivilege();
                            urps.setMenu(grchild);
                            urps.setPrivilege(privilege);
                            urps.setUser(user);
                        }
                        userPrivilegeRepo.save(urps);
                    }
                }
            }
        }

    }

    public void manageNotificationsInterventions(Utilisateur currentUser){
        List<Intervention> last_interventions = interventionRepository.findLastInterventions(false,new Date(),currentUser);
        Notification last = notificationsRepo.findNotificationByTitreAndModule("Intervention(s) dépassée(s) sans démarrage","INTERVENTION");
        if(last_interventions.size() >0){
            if(last == null){
                last = new Notification();
                last.setTitre("Intervention(s) dépassée(s) sans démarrage");
                last.setModule("INTERVENTION");
                last.setUrl("/maintenances/interventions_last");
                last = notificationsRepo.save(last);
            }
            if(!currentUser.getNotifications().contains(last)){
                currentUser.getNotifications().add(last);
                utilisateurRepo.save(currentUser);
            }

        }
        else{
            if(last != null){
                currentUser.getNotifications().remove(last);
                utilisateurRepo.save(currentUser);
            }
        }


        List<Intervention> bientot_interventions = interventionRepository.findBientotInterventions(false,new Date(),currentUser.getId());
        Notification bientot = notificationsRepo.findNotificationByTitreAndModule("Intervention(s) démarrant bientôt","INTERVENTION");
        if(bientot_interventions.size() >0){
            if(bientot == null){
                bientot = new Notification();
                bientot.setTitre("Intervention(s) démarrant bientôt");
                bientot.setModule("INTERVENTION");
                bientot.setUrl("/maintenances/interventions_bientot");
                bientot = notificationsRepo.save(bientot);
            }
            if(!currentUser.getNotifications().contains(bientot)){
                currentUser.getNotifications().add(bientot);
                utilisateurRepo.save(currentUser);
            }

        }
        else{
            if(bientot != null){
                currentUser.getNotifications().remove(bientot);
                utilisateurRepo.save(currentUser);
            }
        }
    }
    public void manageNotificationsEquipements(Utilisateur currentUser){
        List<Equipement> equipementsKM = equipementRepository.findEquipementsKMToChange(false);
        List<Equipement> equipementsDuree = equipementRepository.findEquipementsDureeToChange(false,new Date());
        Notification last = notificationsRepo.findNotificationByTitreAndModule("Equipement(s) à changer","EQUIPEMENT");
        if((equipementsKM.size()+equipementsDuree.size()) >0){
            if(last == null){
                last = new Notification();
                last.setTitre("Equipement(s) à changer");
                last.setModule("EQUIPEMENT");
                last.setUrl("/equipements/equipements_expired");
                last = notificationsRepo.save(last);
            }
            if(!currentUser.getNotifications().contains(last)){
                currentUser.getNotifications().add(last);
                utilisateurRepo.save(currentUser);
            }

        }
        else{
            if(last != null){
                currentUser.getNotifications().remove(last);
                utilisateurRepo.save(currentUser);
            }
        }
    }
    public void manageNotificationsArticles(Utilisateur currentUser){
        List<Article> articlesKM = articleRepository.findArticlesKMToChange(false);
        List<Article> articlesDuree = articleRepository.findArticlesDureeToChange(false,new Date());
        Notification last = notificationsRepo.findNotificationByTitreAndModule("Article(s) à changer","ARTICLES");
        if((articlesKM.size()+articlesDuree.size()) >0){
            if(last == null){
                last = new Notification();
                last.setTitre("Article(s) à changer");
                last.setModule("ARTICLES");
                last.setUrl("/equipements/stock/articles_expired");
                last = notificationsRepo.save(last);
            }
            if(!currentUser.getNotifications().contains(last)){
                currentUser.getNotifications().add(last);
                utilisateurRepo.save(currentUser);
            }

        }
        else{
            if(last != null){
                currentUser.getNotifications().remove(last);
                utilisateurRepo.save(currentUser);
            }
        }
    }

}
