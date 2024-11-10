package jway.gmao.controller;


import jway.gmao.dao.*;
import jway.gmao.model.*;
import jway.gmao.service.AdministrationService;
import jway.gmao.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.Date;
import java.util.List;


@Controller
public class WebPageController implements ErrorController {

    @Value("${server.servlet.context-path}")
    private String path;
    @Autowired
    private AdministrationService adminService;
    @Autowired
    private UtilisateurRepo utilisateurRepo;

    @Autowired
    private UserPrivilegeRepo userPrivilegeRepo;
    @Autowired
    private HistoryRepo historyRepo;
    @Autowired
    private NotificationsRepo notificationsRepo;
    @Autowired
    private UtilisateurService utilisateurService;


    @RequestMapping({"/index", "/"})
    public String dashboard(Model model, @AuthenticationPrincipal User user, HttpSession session) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        List<History> histories=historyRepo.findAll();
        model.addAttribute("histories", histories);
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


        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "DASH");
        model.addAttribute("path", path);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("menus", adminService.findAllMenus(currentUser));
        model.addAttribute("privilege", privilege);
        model.addAttribute("menuActive", "DASHBOARD");


        return "index";
    }

    @RequestMapping({"/dashbord"})
    public String tableauDeBord(Model model, @AuthenticationPrincipal User user, HttpSession session) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());

    	UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "DASH");
    	model.addAttribute("currentUser",currentUser);
    	model.addAttribute("menus",adminService.findAllMenus(currentUser));
        model.addAttribute("menuActive","DASH");
    	model.addAttribute("sousMenuActive",0);
    	model.addAttribute("privilege",privilege);


        return "redirect:index";
    }

    @RequestMapping({"/login"})
    public String login(Model model, HttpServletRequest request) {
        model.addAttribute("path", path);
        if (request.getParameter("password") != null)
            model.addAttribute("password", request.getParameter("password"));
        return "login";
    }

    // Login form with error
    @RequestMapping("/login-error")
    public String loginError(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        String errorMessage = null;
        if (session != null) {
            AuthenticationException ex = (AuthenticationException) session
                    .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                errorMessage = ex.getMessage();
            }
        }
        if (request.getParameter("password") != null)
            model.addAttribute("password", request.getParameter("password"));
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("error", true);
        return "login";
    }

    @RequestMapping({"/login-success"})
    public String AuthentificationSuccess(HttpSession session) throws ParseException {
        return "redirect:index";
    }


    @RequestMapping("/error")
    public String handleError(Model model) {
        model.addAttribute("path", path);
        return "error";
    }
}
