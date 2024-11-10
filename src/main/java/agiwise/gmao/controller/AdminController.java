package jway.gmao.controller;


import jway.gmao.dao.*;
import jway.gmao.model.*;
import jway.gmao.service.AdministrationService;
import jway.gmao.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    private AdministrationService adminService;
    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private UtilisateurRepo utilisateurRepo;

    @Autowired
    private UserPrivilegeRepo userPrivilegeRepo;
    @Autowired
    private MenuRepo menuRepo;
    @Autowired
    private PreviligesRepo priPreviligesRepo;
    @Autowired
    private RolesRepo rolesRepo;
    @Autowired
    private HistoryRepo historyRepo;
    @Autowired
    private NotificationsRepo notificationsRepo;

    @Value("${server.servlet.context-path}")
    private String path;

    @ModelAttribute
    public void addMenus(HttpSession session, Model model, @AuthenticationPrincipal User user) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "ADMINISTRATION");
        int total_notif = 0;
        utilisateurService.manageNotificationsInterventions(currentUser);
        utilisateurService.manageNotificationsEquipements(currentUser);
        utilisateurService.manageNotificationsArticles(currentUser);
        List<Notification> notifications_reclamations = notificationsRepo.findAllNotifByUSer(currentUser.getId(), "RECLAMATION");
        List<Notification> notifications_interventions = notificationsRepo.findAllNotifByUSer(currentUser.getId(), "INTERVENTION");
        List<Notification> notifications_equipements = notificationsRepo.findAllNotifByUSer(currentUser.getId(), "EQUIPEMENT");
        List<Notification> notifications_articles = notificationsRepo.findAllNotifByUSer(currentUser.getId(), "ARTICLES");
        total_notif = notifications_interventions.size()+notifications_reclamations.size()+notifications_equipements.size()+notifications_articles.size();
        model.addAttribute("total_notif", total_notif);
        model.addAttribute("notifications_reclamations", notifications_reclamations);
        model.addAttribute("notifications_interventions", notifications_interventions);
        model.addAttribute("notifications_equipements", notifications_equipements);
        model.addAttribute("notifications_articles", notifications_articles);


        model.addAttribute("path", path);
        model.addAttribute("menus", adminService.findAllMenus(currentUser));
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("privilege", privilege);
        model.addAttribute("menuActive", "ADMINISTRATION");
    }

    @GetMapping("/utilisateurs")
    public String utilisateurs(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "ADMINISTRATION_USERS");
        List<Utilisateur> utilisateurs = utilisateurRepo.findByEtat_supp(false);
        List<String> usernames = utilisateurRepo.findUSernames();

        model.addAttribute("usernames", usernames);
        model.addAttribute("utilisateurs", utilisateurs);
        model.addAttribute("privilege", privilege);
        model.addAttribute("roles", rolesRepo.findByEtatSupp(false));
        model.addAttribute("sousMenuActive", "ADMINISTRATION_USERS");



        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("updatedPass") != null)
            model.addAttribute("updatedPass", request.getParameter("updatedPass"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        return "admin/utilisateurs";
    }

    @GetMapping("/privileges/{id}")
    public String listUsers(@PathVariable(name = "id") Long id, Model model, @AuthenticationPrincipal User user) {
        Utilisateur utilisateur = utilisateurRepo.findById(id).get();
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "ADMINISTRATION_USERS");
        List<Menu> parentMenus = menuRepo.findHeaderPrentMenu();
        for (Menu parentMenu : parentMenus) {
            List<Menu> children = menuRepo.findChildMenus(parentMenu.getId());
            parentMenu.setPrivilege(userPrivilegeRepo.findPrivilegeByUtilisateurAndMenu(utilisateur, parentMenu));
            parentMenu.setChildren(children);
            if (children != null) {
                for (Menu child : children) {
                    child.setPrivilege(userPrivilegeRepo.findPrivilegeByUtilisateurAndMenu(utilisateur, child));
                    List<Menu> grandChildren = menuRepo.findChildMenus(child.getId());
                    child.setChildren(grandChildren);
                    if (grandChildren != null) {
                        for (Menu grandChild : grandChildren) {
                            grandChild.setPrivilege(userPrivilegeRepo.findPrivilegeByUtilisateurAndMenu(utilisateur, grandChild));
                        }
                    }
                }
            }
        }
        model.addAttribute("parentMenus", parentMenus);
        model.addAttribute("privileges", priPreviligesRepo.findAll());
        model.addAttribute("privilege", privilege);
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("sousMenuActive", "ADMINISTRATION_USERS");
        model.addAttribute("roles", rolesRepo.findByEtatSupp(false));
        return "/admin/privileges";
    }

    @PostMapping(value = "/setDroitMenu")
    public RedirectView setHabilitation(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());

        long id_user = Long.parseLong(request.getParameter("id_user"));
        long id_menu = Long.parseLong(request.getParameter("id_menu"));
        long droit = Long.parseLong(request.getParameter("droit"));

        utilisateurService.setDroitMenu(droit, id_menu, id_user);

        return new RedirectView("privileges/" + id_user);
    }

    @PostMapping(value = "/editUtilisateur")
    public RedirectView editUtilisateur(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());

        String action = request.getParameter("action");
        String id = request.getParameter("id");
        String firstName = request.getParameter("prenom");
        String lastName = request.getParameter("nom");
        String username = request.getParameter("username");
        String role = request.getParameter("role");
        String password = request.getParameter("password");

        if (action.equals("add")) {
            utilisateurService.addUtilisateur(firstName, lastName, password, username, rolesRepo.findById(Long.valueOf(role)).get());
            return new RedirectView("utilisateurs?added=ok");
        } else if (action.equals("update")) {
            utilisateurService.editUtilisateur(utilisateurRepo.findUtilisateurById(Long.parseLong(id)), firstName, lastName, username, rolesRepo.findById(Long.valueOf(role)).get());
            return new RedirectView("utilisateurs?updated=ok");
        }else{
            return new RedirectView("utilisateurs");
        }


    }

    @PostMapping(value = "/editPassword")
    public RedirectView editPassword(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        String id = request.getParameter("idUser");
        String password = request.getParameter("password");
        String passwordConfirm = request.getParameter("passwordConfirm");
        String updqted="nok";
        Utilisateur utilisateur = utilisateurRepo.findUtilisateurById(Long.parseLong(id));
        if (passwordConfirm.equals(password)) {
            utilisateurService.editPassword(utilisateur, password);
            updqted="ok";
        }

        return new RedirectView("utilisateurs?updatedPass="+updqted);
    }

    @PostMapping(value = "/deleteUser")
    public RedirectView deleteUser(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        String id = request.getParameter("idUser");
        Utilisateur utilisateur = utilisateurRepo.findUtilisateurById(Long.parseLong(id));
        utilisateur.setEtatSupp(true);
        utilisateurRepo.save(utilisateur);

        return new RedirectView("utilisateurs?deleted=ok");
    }

    @GetMapping("/historiques")
    public String historiques(HttpServletRequest request,Model model, @AuthenticationPrincipal User user, HttpSession session) throws ParseException {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        List<History> histories=historyRepo.findAllHistoriques();
        model.addAttribute("sousMenuActive", "ADMINISTRATION_HISTORY");
        model.addAttribute("historiques", histories);


        return "admin/historiques";

    }

}
