package jway.gmao.controller;


import jway.gmao.dao.*;
import jway.gmao.model.*;
import jway.gmao.service.AdministrationService;
import jway.gmao.service.StockService;
import jway.gmao.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value = "/equipements")
public class EquipementController {




    @Value("${dir.chemin_pj}")
    private String chemin_pj;
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
    @Value("${server.servlet.context-path}")
    private String path;

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
    private MarqueRepository marqueRepository;

    @Autowired
    private ModeleRepository modeleRepository;

    @Autowired
    private EquipementRepository equipementRepository;
    @Autowired
    private InstallationRepository installationRepository;
    @Autowired
    private EquipementsTypeRepository equipementsTypeRepository;
    @Autowired
    private EmplacementRepository emplacementRepository;
    @Autowired
    private EquipementStatutRepository equipementStatutRepository;
    @Autowired
    private FournisseurServiceRepository fournisseurServiceRepository;
    @Autowired
    private FournisseurArticleRepository fournisseurArticleRepository;
    @Autowired
    private FournisseurCategorieRepository fournisseurCategorieRepository;

    @Autowired
    private InstallationStatutRepository installationStatutRepository;
    @Autowired
    private InstallationHistoStatutRepository installationHistoStatutRepository;

    @Autowired
    private EquipementHistoEmplacementRepository histoEmplacementRepository;
    @Autowired
    private EquipementHistoInstallationRepository histoInstallationRepository;
    @Autowired
    private EquipementHistoStatutRepository histoStatutRepository;
    @Autowired
    private EquipementArticleRepository equipementArticleRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UniteRepository uniteRepository;
    @Autowired
    private EntrepotRepository entrepotRepository;
    @Autowired
    private MouvementArticleRepository mouvementArticleRepository;
    @Autowired
    private ArticleStockRepository articleStockRepository;
    @Autowired
    private InvPhysiqueRepository inventairePhysiqueRepository;
    @Autowired
    private InvPhysiqueDetailRepository inventairePhysiqueDetailRepository;
    @Autowired
    private StockService stockService;
    @Autowired
    private BonSortieRepository bonSortieRepository;
    @Autowired
    private BonSortieDetailRepository bonSortieDetailRepository;
    @Autowired
    private InterventionRepository interventionRepository;
    @Autowired
    private BonSortieStatusRepository bonSortieStatusRepository;
    @Autowired
    private BonReceptionRepository bonReceptionRepository;

    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private PaysRepository paysRepository;
    @Autowired
    private FormeJuridiqueRepository formeJuridiqueRepository;
    @Autowired
    private DeviseRepository deviseRepository;
    @Autowired
    private ActiviteRepository activiteRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private BonReceptionStatusRepository bonReceptionStatusRepository;
    @Autowired
    private BonReceptionDetailRepository bonReceptionDetailRepository;
    @Autowired
    private NotificationsRepo notificationsRepo;
    @Autowired
    private CategorieRepository categorieRepository;
    @Autowired
    private Equipement_PJRepository equipementPjRepository;
    
    @ModelAttribute
    public void addMenus(HttpSession session, Model model, @AuthenticationPrincipal User user) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS");
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
        model.addAttribute("menuActive", "EQUIPEMENTS");
    }

    //***********************************PARAMETRAGES*******************************************************************************
    @GetMapping("/params/types")
    public String types(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_PARAMS_TYPE");
        List<Equipement_Type> types = equipementsTypeRepository.findAllTypes(false);
        for(Equipement_Type type : types){
            boolean canDelete = false;
            boolean canUpdate = false;
            List<Equipement> equipements = equipementRepository.findAllEquipementsByType(type,false);
            if(equipements.size() == 0){
                canDelete=true;
                canUpdate=true;
            }
            type.setCanDelete(canDelete);
            type.setCanUpdate(canUpdate);
        }

        model.addAttribute("types", types);
        model.addAttribute("privilege", privilege);
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_PARAMS");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_PARAMS_TYPE");



        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("updatedPass") != null)
            model.addAttribute("updatedPass", request.getParameter("updatedPass"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        return "equipements/params/types";
    }
    @PostMapping(value = "/params/addType")
    public RedirectView addType(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String libelle = request.getParameter("libelle");
        Equipement_Type type = new Equipement_Type();
        type.setLibelle(libelle);
        equipementsTypeRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UN NOUVEAU TYPE D'EQUIPEMENT :"+libelle);
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("types?added=ok");
    }
    @PostMapping(value = "/params/editType")
    public RedirectView editType(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String id = request.getParameter("id");
        String libelle = request.getParameter("libelle");
        Equipement_Type type = equipementsTypeRepository.findTypeByID(Long.parseLong(id));
        type.setLibelle(libelle);
        equipementsTypeRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER LE TYPE D'EQUIPEMENT :"+libelle);
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("types?updated=ok");
    }
    @PostMapping(value = "/params/deleteType")
    public RedirectView deleteType(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String id = request.getParameter("id");
        Equipement_Type type = equipementsTypeRepository.findTypeByID(Long.parseLong(id));
        type.setEtatSupp(true);
        equipementsTypeRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER LE TYPE D'EQUIPEMENT :"+type.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("types?deleted=ok");
    }

    @GetMapping("/params/marques")
    public String marques(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_PARAMS_TYPE");
        List<Marque> marques = marqueRepository.findAllMarques(false);
        for(Marque type : marques){
            boolean canDelete = false;
            boolean canUpdate = false;
            List<Equipement> equipements = equipementRepository.findAllEquipementsByMarque(type,false);
            if(equipements.size() == 0){
                canDelete=true;
                canUpdate=true;
            }
            type.setCanDelete(canDelete);
            type.setCanUpdate(canUpdate);
        }

        model.addAttribute("marques", marques);
        model.addAttribute("privilege", privilege);
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_PARAMS");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_PARAMS_MARQUE");



        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("updatedPass") != null)
            model.addAttribute("updatedPass", request.getParameter("updatedPass"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        return "equipements/params/marques";
    }
    @PostMapping(value = "/params/addMarque")
    public RedirectView addMarque(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String libelle = request.getParameter("libelle");
        Marque type = new Marque();
        type.setLibelle(libelle);
        marqueRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UNE NOUVELLE MARQUE :"+libelle);
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("marques?added=ok");
    }
    @PostMapping(value = "/params/editMarque")
    public RedirectView editMarque(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String id = request.getParameter("id");
        String libelle = request.getParameter("libelle");
        Marque type = marqueRepository.findMarqueByID(Long.parseLong(id));
        type.setLibelle(libelle);
        marqueRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER LA MARQUE :"+libelle);
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("marques?updated=ok");
    }
    @PostMapping(value = "/params/deleteMarque")
    public RedirectView deleteMarque(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String id = request.getParameter("id");
        Marque type = marqueRepository.findMarqueByID(Long.parseLong(id));
        type.setEtatSupp(true);
        marqueRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER LA MARQUE :"+type.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("marques?deleted=ok");
    }

    @GetMapping("/params/modeles")
    public String modeles(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_PARAMS_TYPE");
        List<Modele> modeles = modeleRepository.findAllModeles(false);
        for(Modele type : modeles){
            boolean canDelete = false;
            boolean canUpdate = false;
            List<Equipement> equipements = equipementRepository.findAllEquipementsByModele(type,false);
            if(equipements.size() == 0){
                canDelete=true;
                canUpdate=true;
            }
            type.setCanDelete(canDelete);
            type.setCanUpdate(canUpdate);
        }

        model.addAttribute("modeles", modeles);
        model.addAttribute("privilege", privilege);
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_PARAMS");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_PARAMS_MODELE");



        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("updatedPass") != null)
            model.addAttribute("updatedPass", request.getParameter("updatedPass"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        return "equipements/params/modeles";
    }
    @PostMapping(value = "/params/addModele")
    public RedirectView addModele(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String libelle = request.getParameter("libelle");
        Modele type = new Modele();
        type.setLibelle(libelle);
        modeleRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UN NOUVEAU MODELE :"+libelle);
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("modeles?added=ok");
    }
    @PostMapping(value = "/params/editModele")
    public RedirectView editModele(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String id = request.getParameter("id");
        String libelle = request.getParameter("libelle");
        Modele type = modeleRepository.findModeleByID(Long.parseLong(id));
        type.setLibelle(libelle);
        modeleRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER LE MODELE :"+libelle);
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("modeles?updated=ok");
    }
    @PostMapping(value = "/params/deleteModele")
    public RedirectView deleteModele(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String id = request.getParameter("id");
        Modele type = modeleRepository.findModeleByID(Long.parseLong(id));
        type.setEtatSupp(true);
        modeleRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER LE MODELE :"+type.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("modeles?deleted=ok");
    }

    @GetMapping("/params/status_equipements")
    public String status_equipements(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_PARAMS_STATUT_EQUIPEMENT");
        List<Equipement_Status> status = equipementStatutRepository.findAllStatus(false);
        for(Equipement_Status type : status){
            boolean canDelete = false;
            boolean canUpdate = false;
            List<Equipement> equipements = equipementRepository.findAllEquipementsByStatus(type,false);
            if(equipements.size() == 0){
                canDelete=true;
                canUpdate=true;
            }
            type.setCanDelete(canDelete);
            type.setCanUpdate(canUpdate);
        }

        model.addAttribute("status", status);
        model.addAttribute("privilege", privilege);
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_PARAMS");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_PARAMS_STATUT_EQUIPEMENT");



        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("updatedPass") != null)
            model.addAttribute("updatedPass", request.getParameter("updatedPass"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        return "equipements/params/status_equipements";
    }
    @PostMapping(value = "/params/addStatusEquipement")
    public RedirectView addStatusEquipement(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String libelle = request.getParameter("libelle");
        Equipement_Status type = new Equipement_Status();
        type.setLibelle(libelle);
        equipementStatutRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UN NOUVEAU STATUT D'ÉQUIPEMENT :"+libelle);
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("status_equipements?added=ok");
    }
    @PostMapping(value = "/params/editStatusEquipement")
    public RedirectView editStatusEquipement(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String id = request.getParameter("id");
        String libelle = request.getParameter("libelle");
        Equipement_Status type = equipementStatutRepository.findEquipement_StatusByID(Long.parseLong(id));
        type.setLibelle(libelle);
        equipementStatutRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER LE STATUT D'ÉQUIPEMENT :"+libelle);
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("status_equipements?updated=ok");
    }
    @PostMapping(value = "/params/deleteStatusEquipement")
    public RedirectView deleteStatusEquipement(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String id = request.getParameter("id");
        Equipement_Status type = equipementStatutRepository.findEquipement_StatusByID(Long.parseLong(id));
        type.setEtatSupp(true);
        equipementStatutRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER LE STATUT D'ÉQUIPEMENT :"+type.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("status_equipements?deleted=ok");
    }

    @GetMapping("/params/status_installations")
    public String status_installations(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_PARAMS_STATUT_INSTALLATION");
        List<Installation_Status> status = installationStatutRepository.findAllStatus(false);
        for(Installation_Status type : status){
            boolean canDelete = false;
            boolean canUpdate = false;
            List<Installation> installations = installationRepository.findAllInstallationsByStatus(type,false);
            if(installations.size() == 0){
                canDelete=true;
                canUpdate=true;
            }
            type.setCanDelete(canDelete);
            type.setCanUpdate(canUpdate);
        }

        model.addAttribute("status", status);
        model.addAttribute("privilege", privilege);
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_PARAMS");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_PARAMS_STATUT_INSTALLATION");



        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("updatedPass") != null)
            model.addAttribute("updatedPass", request.getParameter("updatedPass"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        return "equipements/params/status_installations";
    }
    @PostMapping(value = "/params/addStatusInstallation")
    public RedirectView addStatusInstallation(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String libelle = request.getParameter("libelle");
        Installation_Status type = new Installation_Status();
        type.setLibelle(libelle);
        installationStatutRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UN NOUVEAU STATUT D'INSTALLATION :"+libelle);
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("status_installations?added=ok");
    }
    @PostMapping(value = "/params/editStatusInstallation")
    public RedirectView editStatusInstallation(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String id = request.getParameter("id");
        String libelle = request.getParameter("libelle");
        Installation_Status type = installationStatutRepository.findStatusBy(Long.parseLong(id));
        type.setLibelle(libelle);
        installationStatutRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER LE STATUT D'INSTALLATION :"+libelle);
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("status_installations?updated=ok");
    }
    @PostMapping(value = "/params/deleteStatusInstallation")
    public RedirectView deleteStatusInstallation(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String id = request.getParameter("id");
        Installation_Status type = installationStatutRepository.findStatusBy(Long.parseLong(id));
        type.setEtatSupp(true);
        installationStatutRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER LE STATUT D'INSTALLATION :"+type.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("status_installations?deleted=ok");
    }


    @GetMapping("/params/categories")
    public String categories(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_PARAMS_TYPE");
        List<Categorie> categories = categorieRepository.findAllCategories(false);
        for(Categorie type : categories){
            boolean canDelete = false;
            boolean canUpdate = false;
            List<Equipement> equipements = equipementRepository.findAllEquipementsByCategorie(type,false);
            if(equipements.size() == 0){
                canDelete=true;
                canUpdate=true;
            }
            type.setCanDelete(canDelete);
            type.setCanUpdate(canUpdate);
        }

        model.addAttribute("categories", categories);
        model.addAttribute("privilege", privilege);
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_PARAMS");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_PARAMS_CATEGORIE");



        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("updatedPass") != null)
            model.addAttribute("updatedPass", request.getParameter("updatedPass"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        return "equipements/params/categories";
    }
    @PostMapping(value = "/params/addCategorie")
    public RedirectView addCategorie(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String libelle = request.getParameter("libelle");
        Categorie type = new Categorie();
        type.setLibelle(libelle);
        type.setEtatSupp(false);
        categorieRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UNE NOUVELLE CATEGORIE :"+libelle);
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("categories?added=ok");
    }
    @PostMapping(value = "/params/editCategorie")
    public RedirectView editCategorie(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String id = request.getParameter("id");
        String libelle = request.getParameter("libelle");
        Categorie type = categorieRepository.findCategorieByID(Long.parseLong(id));
        type.setLibelle(libelle);
        categorieRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER LA CATEGORIE :"+libelle);
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("categories?updated=ok");
    }
    @PostMapping(value = "/params/deleteCategorie")
    public RedirectView deleteCategorie(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String id = request.getParameter("id");
        Categorie type = categorieRepository.findCategorieByID(Long.parseLong(id));
        type.setEtatSupp(true);
        categorieRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER LA CATEGORIE :"+type.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("categories?deleted=ok");
    }


    //*************************************EQUIPEMENTS*********************************************************************************
    @GetMapping("/equipements_list")
    public String equipements_list(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_EQUIPEMENTS");
        List<Equipement> equipements = equipementRepository.findAllEquipements(false);
        List<Installation> installations = installationRepository.findAllInstallations(false);
        List<Emplacement> emplacements = emplacementRepository.findAllEmplacemets(false);
        List<Categorie> categories = categorieRepository.findAllCategories(false);
        for(Equipement type : equipements){
            boolean canDelete = true;
            boolean canUpdate = true;
            List<Equipement_Article> articles = equipementArticleRepository.findAllByEquipement(type);
            if(articles.size() > 0){
                canDelete = false;
                canUpdate = false;
                
            }

            type.setCanDelete(canDelete);
            type.setCanUpdate(canUpdate);
        }

        model.addAttribute("categories", categories);
        model.addAttribute("emplacements", emplacements);
        model.addAttribute("installations", installations);
        model.addAttribute("equipements", equipements);
        model.addAttribute("privilege", privilege);
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_EQUIPEMENTS");



        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("updatedPass") != null)
            model.addAttribute("updatedPass", request.getParameter("updatedPass"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));
        if (request.getParameter("changed") != null)
            model.addAttribute("changed", request.getParameter("changed"));


        return "equipements/equipements_list";
    }
     @GetMapping("/equipements_expired")
    public String equipements_expired(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_EQUIPEMENTS");
        List<Equipement> equipements = new ArrayList<>();
        List<Equipement> equipementsKM = equipementRepository.findEquipementsKMToChange(false);
        List<Equipement> equipementsDuree = equipementRepository.findEquipementsDureeToChange(false,new Date());

        equipements.addAll(equipementsDuree);
        equipements.addAll(equipementsKM);

        List<Installation> installations = installationRepository.findAllInstallations(false);
        List<Emplacement> emplacements = emplacementRepository.findAllEmplacemets(false);
        for(Equipement type : equipements){
            boolean canDelete = true;
            boolean canUpdate = true;
            List<Equipement_Article> articles = equipementArticleRepository.findAllByEquipement(type);
            if(articles.size() > 0){
                canDelete = false;
                canUpdate = false;

            }

            type.setCanDelete(canDelete);
            type.setCanUpdate(canUpdate);
        }

        model.addAttribute("emplacements", emplacements);
        model.addAttribute("installations", installations);
        model.addAttribute("equipements", equipements);
        model.addAttribute("privilege", privilege);
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_EQUIPEMENTS");
         model.addAttribute("expired", true);



        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("updatedPass") != null)
            model.addAttribute("updatedPass", request.getParameter("updatedPass"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));
        if (request.getParameter("changed") != null)
            model.addAttribute("changed", request.getParameter("changed"));


        return "equipements/equipements_list";
    }
    @GetMapping("/equipements_add")
    public String equipements_add(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request, @RequestParam long catId) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_EQUIPEMENTS");
        List<Installation> installations = installationRepository.findAllInstallations(false);
        List<String> ref_equipements = equipementRepository.findAllRef();
        List<Equipement_Type> types = equipementsTypeRepository.findAllTypes(false);
        List<Emplacement> emplacements = emplacementRepository.findAllEmplacemets(false);
        List<Equipement_Status> status = equipementStatutRepository.findAllStatus(false);
        List<Fournisseur_Service> fournisseurs = fournisseurServiceRepository.findAllFournisseurs(false);
        List<Marque> marques = marqueRepository.findAllMarques(false);
        List<Modele> modeles = modeleRepository.findAllModeles(false);
        List<Fournisseur_Categorie> categories_fournisseur = fournisseurCategorieRepository.findAll(false);


        model.addAttribute("catId",catId);
        model.addAttribute("ref_equipements", ref_equipements);
        model.addAttribute("marques", marques);
        model.addAttribute("modeles", modeles);
        model.addAttribute("fournisseurs", fournisseurs);
        model.addAttribute("categories_fournisseur", categories_fournisseur);
        model.addAttribute("status", status);
        model.addAttribute("types", types);
        model.addAttribute("emplacements", emplacements);
        model.addAttribute("installations", installations);
        model.addAttribute("privilege", privilege);
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_EQUIPEMENTS");



        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("updatedPass") != null)
            model.addAttribute("updatedPass", request.getParameter("updatedPass"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        return "equipements/equipements_add";
    }
    @PostMapping(value = "/equipements_add")
    public RedirectView equipements_add(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model,@RequestParam(name = "file",required = false) MultipartFile file) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String libelle = request.getParameter("libelle");
        String code = request.getParameter("code");
        String date_service = request.getParameter("date_service");
        String date_acquisition = request.getParameter("date_acquisition");
        String description = request.getParameter("description");
        String num_serie = request.getParameter("num_serie");
        String has_compteur = request.getParameter("has_compteur");
        String numInventaire = request.getParameter("numInventaire");
        String categorie = request.getParameter("catId");
        String prixachat = request.getParameter("prixachat");
        String type_compteur = null;
        boolean compteur = false;
        Long seuil_compteur = null;
        Date date_initiale_compteur = null;
        Date date_fin_compteur = null;
        Long valeur_initiale_compteur = null;
        if(has_compteur != null && has_compteur.equals("oui")) {
            compteur = true;
            type_compteur = request.getParameter("type_compteur");
            if(type_compteur != null && type_compteur.equals("nombre")) {
                seuil_compteur = Long.parseLong(request.getParameter("seuil_compteur_km"));
                valeur_initiale_compteur = Long.parseLong(request.getParameter("valeur_compteur_km"));
            }else{
                seuil_compteur = Long.parseLong(request.getParameter("seuil_compteur_duree"));
                String str = request.getParameter("valeur_compteur_duree");
                if(str != null && !str.equals("")) {
                    date_initiale_compteur = sdf.parse(str);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date_initiale_compteur);
                    cal.add(Calendar.MONTH, seuil_compteur.intValue());

                    date_fin_compteur = cal.getTime();
                }
            }

        }


        String type_id = request.getParameter("type_id");
        Equipement_Type type = null;
        if(type_id !=null && type_id.equals("new")){
            String libelle_type = request.getParameter("libelle_type");
            type= new Equipement_Type();
            type.setEtatSupp(false);
            type.setLibelle(libelle_type);
            equipementsTypeRepository.save(type);
        }
        else{
            if(type_id != null && !type_id.equals("")) type = equipementsTypeRepository.findTypeByID(Long.parseLong(type_id));
        }

        String installation_id = request.getParameter("installation_id");
        Installation installation = null;
        if(installation_id !=null && installation_id.equals("new")){}
        else{
            if(installation_id != null && !installation_id.equals("")) installation = installationRepository.findInstallationByID(Long.parseLong(installation_id));
        }

        String marque_id = request.getParameter("marque_id");
        Marque marque = null;
        if(marque_id !=null && marque_id.equals("new")){
            String libelle_marque = request.getParameter("libelle_marque");
            marque= new Marque();
            marque.setEtatSupp(false);
            marque.setLibelle(libelle_marque);
            marqueRepository.save(marque);
        }
        else{
            if(marque_id != null && !marque_id.equals("")) marque = marqueRepository.findMarqueByID(Long.parseLong(marque_id));
        }

        String modele_id = request.getParameter("modele_id");
        Modele modele = null;
        if(modele_id !=null && modele_id.equals("new")){
            String libelle_modele = request.getParameter("libelle_modele");
            modele= new Modele();
            modele.setEtatSupp(false);
            modele.setLibelle(libelle_modele);
            modeleRepository.save(modele);
        }
        else{
            if(modele_id != null && !modele_id.equals("")) modele = modeleRepository.findModeleByID(Long.parseLong(modele_id));
        }

        String emplacement_id = request.getParameter("emplacement_id");
        Emplacement emplacement = null;
        if(emplacement_id !=null && emplacement_id.equals("new")){
            String code_emplacement = request.getParameter("code_emplacement");
            String libelle_emplacement = request.getParameter("libelle_emplacement");
            emplacement= new Emplacement();
            emplacement.setEtatSupp(false); emplacement.setCode(code_emplacement);
            emplacement.setLibelle(libelle_emplacement);
            emplacementRepository.save(emplacement);
        }
        else{
            if(emplacement_id != null && !emplacement_id.equals("")) emplacement = emplacementRepository.findEmplacementByID(Long.parseLong(emplacement_id));
        }

        String status_id = request.getParameter("status_id");
        Equipement_Status status = null;
        if(status_id !=null && status_id.equals("new")){
            String libelle_status = request.getParameter("libelle_status");
            status= new Equipement_Status();
            status.setEtatSupp(false);
            status.setLibelle(libelle_status);
            equipementStatutRepository.save(status);
        }
        else{
            if(status_id != null && !status_id.equals("")) status = equipementStatutRepository.findEquipement_StatusByID(Long.parseLong(status_id));
        }

        String fournisseur_id = request.getParameter("fournisseur_id");
        Fournisseur_Service fournisseur = null;
        if(fournisseur_id !=null && fournisseur_id.equals("new")){
            String categorie_fournisseur = request.getParameter("categorie_fournisseur");
            String libelle_fournisseur = request.getParameter("libelle_fournisseur");
            Fournisseur_Categorie cat = fournisseurCategorieRepository.findByID(Long.parseLong(categorie_fournisseur));
            Collection<Fournisseur_Categorie> categories = new ArrayList<>();
            categories.add(cat);

            fournisseur = new Fournisseur_Service();
            fournisseur.setFournisseur(1);
            fournisseur.setEtatSupp(false);
            fournisseur.setRs(libelle_fournisseur);
            fournisseur.setCategories(categories);
            fournisseurServiceRepository.save(fournisseur);
        }
        else{
            if(fournisseur_id != null && !fournisseur_id.equals("")) fournisseur = fournisseurServiceRepository.findSocieteByID(Long.parseLong(fournisseur_id));
        }


        Equipement equipement = new Equipement();
        equipement.setCode(code);
        equipement.setNum_serie(num_serie);
        equipement.setDescription(description);
        equipement.setLibelle(libelle);
        equipement.setEtatSupp(false);
        equipement.setStatus(status);
        equipement.setMarque(marque);
        equipement.setModele(modele);
        equipement.setFournisseur(fournisseur);
        equipement.setType(type);
        equipement.setNum_inventaire(numInventaire);
        equipement.setPrix_achat(prixachat);
        if(type != null && type.getLibelle().equals("Installé")){
            equipement.setInstallation(installation);
        }else{
            equipement.setEmplacement(emplacement);
        }
        if(date_acquisition != null && !date_acquisition.equals("")) equipement.setDate_acquisition(sdf.parse(date_acquisition));
        if(date_service != null && !date_service.equals("")) equipement.setDate_service(sdf.parse(date_service));
        equipement.setHas_compteur(compteur);
        equipement.setType_compteur(type_compteur);
        equipement.setValeur_initiale_compteur(valeur_initiale_compteur);
        equipement.setValeur_actuelle_compteur(valeur_initiale_compteur);
        equipement.setDate_initiale_compteur(date_initiale_compteur);
        equipement.setDate_fin_compteur(date_fin_compteur);
        equipement.setSeuil_compteur(seuil_compteur);
        if (categorie != null) {
            Categorie categorie1 = categorieRepository.findCategorieByID(Long.parseLong(categorie));
            equipement.setCategorie(categorie1);
        }
         equipement = equipementRepository.save(equipement);

        String chemin = chemin_pj+"ged/";
        File folder_pj = new File(chemin);
        if (!folder_pj.exists()) {
            folder_pj.mkdirs();
        }

        chemin = chemin_pj+"ged/equipement/";
        folder_pj = new File(chemin);
        if (!folder_pj.exists()) {
            folder_pj.mkdirs();
        }

        chemin = chemin_pj+"ged/equipement/"+equipement.getId()+"/";
        folder_pj = new File(chemin);
        if (!folder_pj.exists()) {
            folder_pj.mkdirs();
        }

        if (!file.isEmpty()) {
            try {
                String imageFolder2 = chemin;
                String originalFileName = file.getOriginalFilename();
                File pt = new File(imageFolder2 + originalFileName);
                File p = pt.getParentFile();
                String filePath = Paths.get(imageFolder2, originalFileName).toString();

                if (!p.exists()) {
                    p.mkdirs();
                }

                String pj = file.getOriginalFilename();
                String nom_piece = pj.substring(0, pj.lastIndexOf("."));
                String extension = pj.substring(pj.lastIndexOf("."), pj.length());
                int m = 1;
                while (pt.exists()) {

                    pj = nom_piece + " (" + m + ")" + extension;
                    pt = new File(imageFolder2 + pj);
                    m++;
                }

                file.transferTo(pt);
                Equipement_PJ piece = new Equipement_PJ();
                piece.setEquipement(equipement);
                piece.setDate_ajout(new Date());
                piece.setNom_pj(pj);
                piece.setTaille_pj(file.getSize());
                piece.setAuteur(utilisateur.getFirstName() + " " + utilisateur.getLastName());
                piece.setChemin_pj(filePath);
                equipementPjRepository.save(piece);


            } catch (IllegalStateException | IOException e) {
                System.out.println("IllegalStateException");
                e.printStackTrace();
            }
        }

        if(type != null && type.getLibelle().equals("Installé")){
            histoInstallationRepository.updateOldHistoByEquipement(equipement);
            Equipement_Historique_Installation histo = new Equipement_Historique_Installation();
            histo.setEquipement(equipement);
            histo.setInstallation(installation);
            histo.setDate(new Date());
            histo.setActual(true);
            histoInstallationRepository.save(histo);
        }else{
            histoEmplacementRepository.updateOldHistoByEquipement(equipement);
            Equipement_Historique_Emplacement histo = new Equipement_Historique_Emplacement();
            histo.setEquipement(equipement);
            histo.setEmplacement(emplacement);
            histo.setDate(new Date());
            histo.setActual(true);
            histoEmplacementRepository.save(histo);
        }

        if(status != null ){

            histoStatutRepository.updateOldStatusByEquipement(equipement);
            Equipement_Historique_Statut histo = new Equipement_Historique_Statut();
            histo.setEquipement(equipement);
            histo.setStatus(status);
            histo.setActual(true);
            histo.setDate(new Date());
            histoStatutRepository.save(histo);
        }


        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UN NOUVEAU ÉQUIPEMENT : "+equipement.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("equipements_list?added="+added);
    }
    @GetMapping("/equipements_edit/{id}")
    public String equipements_edit(@PathVariable(name = "id") Long id,Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_EQUIPEMENTS");
        Equipement equipement = equipementRepository.findEquipementByID(id);
        List<Installation> installations = installationRepository.findAllInstallations(false);
        List<String> ref_equipements = equipementRepository.findAllRef();
        List<Equipement_Type> types = equipementsTypeRepository.findAllTypes(false);
        List<Emplacement> emplacements = emplacementRepository.findAllEmplacemets(false);
        List<Equipement_Status> status = equipementStatutRepository.findAllStatus(false);
        List<Fournisseur_Service> fournisseurs = fournisseurServiceRepository.findAllFournisseurs(false);
        List<Marque> marques = marqueRepository.findAllMarques(false);
        List<Modele> modeles = modeleRepository.findAllModeles(false);
        List<Categorie> categories = categorieRepository.findAllCategories(false);
        List<Fournisseur_Categorie> categories_fournisseur = fournisseurCategorieRepository.findAll(false);
        Equipement_PJ equipementPj = equipementPjRepository.findEquipementByEquipement(false, equipement);

        if (equipementPj != null) {
            model.addAttribute("imageName",equipementPj.getNom_pj());
        }
        model.addAttribute("equipement", equipement);
        model.addAttribute("ref_equipements", ref_equipements);
        model.addAttribute("marques", marques);
        model.addAttribute("modeles", modeles);
        model.addAttribute("fournisseurs", fournisseurs);
        model.addAttribute("categories_fournisseur", categories_fournisseur);
        model.addAttribute("status", status);
        model.addAttribute("types", types);
        model.addAttribute("emplacements", emplacements);
        model.addAttribute("installations", installations);
        model.addAttribute("categories", categories);
        model.addAttribute("privilege", privilege);
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_EQUIPEMENTS");



        return "equipements/equipements_edit";
    }
    @PostMapping(value = "/equipements_edit")
    public RedirectView equipements_edit(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model,@RequestParam(name = "file",required = false) MultipartFile file) throws ParseException, IOException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String libelle = request.getParameter("libelle");
        String code = request.getParameter("code");
        String date_service = request.getParameter("date_service");
        String date_acquisition = request.getParameter("date_acquisition");
        String description = request.getParameter("description");
        String num_serie = request.getParameter("num_serie");
        String has_compteur = request.getParameter("has_compteur");
        String numInventaire = request.getParameter("numInventaire");
        String categorie = request.getParameter("catId");
        String prixachat = request.getParameter("prixachat");
        String type_compteur = null;
        boolean compteur = false;
        Long seuil_compteur = null;
        Date date_initiale_compteur = null;
        Date date_fin_compteur = null;
        Long valeur_initiale_compteur = null;
        if(has_compteur != null && has_compteur.equals("oui")) {
            compteur = true;
            type_compteur = request.getParameter("type_compteur");
            if(type_compteur != null && type_compteur.equals("nombre")) {
                seuil_compteur = Long.parseLong(request.getParameter("seuil_compteur_km"));
                valeur_initiale_compteur = Long.parseLong(request.getParameter("valeur_compteur_km"));
            }else{
                seuil_compteur = Long.parseLong(request.getParameter("seuil_compteur_duree"));
                String str = request.getParameter("valeur_compteur_duree");
                if(str != null && !str.equals("")) {
                    date_initiale_compteur = sdf.parse(str);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date_initiale_compteur);
                    cal.add(Calendar.MONTH, seuil_compteur.intValue());

                    date_fin_compteur = cal.getTime();
                }
            }

        }

        String type_id = request.getParameter("type_id");
        Equipement_Type type = null;
        if(type_id !=null && type_id.equals("new")){
            String libelle_type = request.getParameter("libelle_type");
            type= new Equipement_Type();
            type.setEtatSupp(false);
            type.setLibelle(libelle_type);
            equipementsTypeRepository.save(type);
        }
        else{
            if(type_id != null && !type_id.equals("")) type = equipementsTypeRepository.findTypeByID(Long.parseLong(type_id));
        }

        String installation_id = request.getParameter("installation_id");
        Installation installation = null;
        if(installation_id !=null && installation_id.equals("new")){}
        else{
            if(installation_id != null && !installation_id.equals("")) installation = installationRepository.findInstallationByID(Long.parseLong(installation_id));
        }

        String marque_id = request.getParameter("marque_id");
        Marque marque = null;
        if(marque_id !=null && marque_id.equals("new")){
            String libelle_marque = request.getParameter("libelle_marque");
            marque= new Marque();
            marque.setEtatSupp(false);
            marque.setLibelle(libelle_marque);
            marqueRepository.save(marque);
        }
        else{
            if(marque_id != null && !marque_id.equals("")) marque = marqueRepository.findMarqueByID(Long.parseLong(marque_id));
        }

        String modele_id = request.getParameter("modele_id");
        Modele modele = null;
        if(modele_id !=null && modele_id.equals("new")){
            String libelle_modele = request.getParameter("libelle_modele");
            modele= new Modele();
            modele.setEtatSupp(false);
            modele.setLibelle(libelle_modele);
            modeleRepository.save(modele);
        }
        else{
            if(modele_id != null && !modele_id.equals("")) modele = modeleRepository.findModeleByID(Long.parseLong(modele_id));
        }

        String emplacement_id = request.getParameter("emplacement_id");
        Emplacement emplacement = null;
        if(emplacement_id !=null && emplacement_id.equals("new")){
            String code_emplacement = request.getParameter("code_emplacement");
            String libelle_emplacement = request.getParameter("libelle_emplacement");
            emplacement= new Emplacement();
            emplacement.setEtatSupp(false); emplacement.setCode(code_emplacement);
            emplacement.setLibelle(libelle_emplacement);
            emplacementRepository.save(emplacement);
        }
        else{
            if(emplacement_id != null && !emplacement_id.equals("")) emplacement = emplacementRepository.findEmplacementByID(Long.parseLong(emplacement_id));
        }

        String status_id = request.getParameter("status_id");
        Equipement_Status status = null;
        if(status_id !=null && status_id.equals("new")){
            String libelle_status = request.getParameter("libelle_status");
            status= new Equipement_Status();
            status.setEtatSupp(false);
            status.setLibelle(libelle_status);
            equipementStatutRepository.save(status);
        }
        else{
            if(status_id != null && !status_id.equals("")) status = equipementStatutRepository.findEquipement_StatusByID(Long.parseLong(status_id));
        }

        String fournisseur_id = request.getParameter("fournisseur_id");
        Fournisseur_Service fournisseur = null;
        if(fournisseur_id !=null && fournisseur_id.equals("new")){
            String categorie_fournisseur = request.getParameter("categorie_fournisseur");
            String libelle_fournisseur = request.getParameter("libelle_fournisseur");
            Fournisseur_Categorie cat = fournisseurCategorieRepository.findByID(Long.parseLong(categorie_fournisseur));
            Collection<Fournisseur_Categorie> categories = new ArrayList<>();
            categories.add(cat);

            fournisseur = new Fournisseur_Service();
            fournisseur.setFournisseur(1);
            fournisseur.setEtatSupp(false);
            fournisseur.setRs(libelle_fournisseur);
            fournisseur.setCategories(categories);
            fournisseurServiceRepository.save(fournisseur);
        }
        else{
            if(fournisseur_id != null && !fournisseur_id.equals("")) fournisseur = fournisseurServiceRepository.findSocieteByID(Long.parseLong(fournisseur_id));
        }


        Equipement equipement = equipementRepository.findEquipementByID(Long.parseLong(request.getParameter("id")));
        Equipement_Status oldstatus = equipement.getStatus();

        equipement.setCode(code);
        equipement.setNum_serie(num_serie);
        equipement.setDescription(description);
        equipement.setLibelle(libelle);
        equipement.setStatus(status);
        equipement.setMarque(marque);
        equipement.setModele(modele);
        equipement.setFournisseur(fournisseur);
        equipement.setType(type);
        equipement.setNum_inventaire(numInventaire);
        equipement.setPrix_achat(prixachat);
        if(type != null && type.getLibelle().equals("Installé")){
            equipement.setInstallation(installation);
            equipement.setEmplacement(null);
        }else{
            equipement.setEmplacement(emplacement);
            equipement.setInstallation(null);
        }
        if(date_acquisition != null && !date_acquisition.equals("")) equipement.setDate_acquisition(sdf.parse(date_acquisition));
        if(date_service != null && !date_service.equals("")) equipement.setDate_service(sdf.parse(date_service));
        equipement.setHas_compteur(compteur);
        equipement.setType_compteur(type_compteur);
        equipement.setValeur_initiale_compteur(valeur_initiale_compteur);
        equipement.setDate_initiale_compteur(date_initiale_compteur);
        equipement.setDate_fin_compteur(date_fin_compteur);
        equipement.setSeuil_compteur(seuil_compteur);
        if (categorie != null) {
            Categorie categorie1 = categorieRepository.findCategorieByID(Long.parseLong(categorie));
            equipement.setCategorie(categorie1);
        }
        equipement = equipementRepository.save(equipement);

        Equipement_PJ equipementPj = equipementPjRepository.findEquipementByEquipement(false, equipement);

        if (file != null && !file.isEmpty()) {
            String oldImagePath = equipementPj.getChemin_pj();
            if (oldImagePath != null && !oldImagePath.isEmpty()) {
                Path oldImageFilePath = Paths.get(oldImagePath);
                Files.deleteIfExists(oldImageFilePath);
            }
            String originalFileName = file.getOriginalFilename();
            String newImagePath = Paths.get(chemin_pj, "ged", "equipement", String.valueOf(equipement.getId()), originalFileName).toString();
            equipementPj.setChemin_pj(newImagePath);
            equipementPj.setNom_pj(originalFileName);
            equipementPjRepository.save(equipementPj);

            // Save the file to the specified directory
            Path path = Paths.get(newImagePath);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
        }

        if(type != null && type.getLibelle().equals("Installé")){
            histoInstallationRepository.updateOldHistoByEquipement(equipement);
            Equipement_Historique_Installation histo = new Equipement_Historique_Installation();
            histo.setEquipement(equipement);
            histo.setInstallation(installation);
            histo.setDate(new Date());
            histo.setActual(true);
            histoInstallationRepository.save(histo);
        }else{
            histoEmplacementRepository.updateOldHistoByEquipement(equipement);
            Equipement_Historique_Emplacement histo = new Equipement_Historique_Emplacement();
            histo.setEquipement(equipement);
            histo.setEmplacement(emplacement);
            histo.setDate(new Date());
            histo.setActual(true);
            histoEmplacementRepository.save(histo);
        }

        if(status != null && (oldstatus != status || oldstatus == null)){
            histoStatutRepository.updateOldStatusByEquipement(equipement);
            Equipement_Historique_Statut histo = new Equipement_Historique_Statut();
            histo.setEquipement(equipement);
            histo.setStatus(status);
            histo.setActual(true);
            histo.setDate(new Date());
            histoStatutRepository.save(histo);
        }


        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER L'ÉQUIPEMENT : "+equipement.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("equipements_list?updated="+added);
    }
    @PostMapping(value = "/equipements_change")
    public RedirectView equipements_change(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";


        String description = request.getParameter("description");


        String installation_id = request.getParameter("installation_id");
        Installation installation = null;
        if(installation_id !=null && installation_id.equals("new")){}
        else{
            if(installation_id != null && !installation_id.equals("")) installation = installationRepository.findInstallationByID(Long.parseLong(installation_id));
        }

        String emplacement_id = request.getParameter("emplacement_id");
        Emplacement emplacement = null;
        if(emplacement_id !=null && emplacement_id.equals("new")){
            String code_emplacement = request.getParameter("code_emplacement");
            String libelle_emplacement = request.getParameter("libelle_emplacement");
            emplacement= new Emplacement();
            emplacement.setEtatSupp(false);
            emplacement.setCode(code_emplacement);
            emplacement.setLibelle(libelle_emplacement);
            emplacementRepository.save(emplacement);
        }
        else{
            if(emplacement_id != null && !emplacement_id.equals("")) emplacement = emplacementRepository.findEmplacementByID(Long.parseLong(emplacement_id));
        }

        Equipement equipement = equipementRepository.findEquipementByID(Long.parseLong(request.getParameter("id")));
        Equipement_Type type = equipement.getType();
        String action ="";

        if(type != null && type.getLibelle().equals("Installé")){
            histoInstallationRepository.updateOldHistoByEquipement(equipement);
            Equipement_Historique_Installation histo = new Equipement_Historique_Installation();
            histo.setDescription(description);
            histo.setEquipement(equipement);
            histo.setInstallation(installation);
            histo.setDate(new Date());
            histo.setActual(true);
            histoInstallationRepository.save(histo);
            action = "CHANGER L'INSTALLATION DE L'EQUIPEMENT : "+equipement.getLibelle()+"PAR : "+installation.getLibelle();

            equipement.setInstallation(installation);
            equipementRepository.save(equipement);
        }else{
            histoEmplacementRepository.updateOldHistoByEquipement(equipement);
            Equipement_Historique_Emplacement histo = new Equipement_Historique_Emplacement();
            histo.setDescription(description);
            histo.setEquipement(equipement);
            histo.setEmplacement(emplacement);
            histo.setDate(new Date());
            histo.setActual(true);
            histoEmplacementRepository.save(histo);
            action = "CHANGER L'EMPLACEMENT DE L'EQUIPEMENT : "+equipement.getLibelle()+"PAR : "+emplacement.getLibelle();


            equipement.setEmplacement(emplacement);
            equipementRepository.save(equipement);
        }

        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction(action);
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("equipements_list?changed="+added);
    }
    @PostMapping(value = "/equipements_delete")
    public RedirectView equipements_delete(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");

        Equipement equipement =  equipementRepository.findEquipementByID(Long.parseLong(id));
        equipement.setEtatSupp(true);
        equipementRepository.save(equipement);
        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER L'ÉQUIPEMENT : "+equipement.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("equipements_list?deleted="+added);
    }

    @GetMapping("/equipements_articles/{id}")
    public String equipements_articles(@PathVariable(name = "id") Long id,Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_EQUIPEMENTS");
        Equipement equipement = equipementRepository.findEquipementByID(id);

        List<Equipement_Article> equipement_articles = equipementArticleRepository.findAllByEquipement(equipement);
        for(Equipement_Article type : equipement_articles){
            boolean canDelete = true;
            boolean canUpdate = true;

            type.setCanDelete(canDelete);
            type.setCanUpdate(canUpdate);
        }
        List<Article> articles = articleRepository.findArticlesNotAffect(false,equipement);
        if(!equipement.isHas_compteur()) {
            articles = articleRepository.findArticlesNotAffectNotCompteur(false,equipement);
        }
        List<Unite> unites = uniteRepository.findAllUnites(false);

        model.addAttribute("unites", unites);
        model.addAttribute("articles", articles);
        model.addAttribute("equipement_articles", equipement_articles);
        model.addAttribute("equipement", equipement);
        model.addAttribute("privilege", privilege);


        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_EQUIPEMENTS");

        return "equipements/equipements_articles";
    }
    @PostMapping(value = "/equipements_articles/addArticle")
    public RedirectView addArticle(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");
        String article_id = request.getParameter("article_id");
        Article article = null;
        if (article_id != null && article_id.equals("new")) {

            String article_unite = request.getParameter("article_unite");
            Unite unite = null;
            if (article_unite != null && article_unite.equals("new")) {
                String unite_libelle = request.getParameter("unite_libelle");
                String unite_code = request.getParameter("unite_code");

                unite = new Unite();
                unite.setEtatSupp(false);
                unite.setCode(unite_code);
                unite.setLibelle(unite_libelle);
                unite = uniteRepository.save(unite);
            }
            else {
                if(article_unite !=null && !article_unite.equals("")) unite = uniteRepository.findUniteByID(Long.parseLong(article_unite));
            }


            String article_code = request.getParameter("article_code");
            String has_compteur = request.getParameter("has_compteur");
            String valeur_compteur = request.getParameter("valeur_compteur");
            String article_libelle = request.getParameter("article_libelle");
            Double price = Double.parseDouble(request.getParameter("article_pmp"));

            article = new Article();
            article.setEtatSupp(false);
            article.setCode(article_code);
            article.setLibelle(article_libelle);
            article.setUnite(unite);
            article.setPmp(price);
            article.setHas_compteur(has_compteur);
            article = articleRepository.save(article);

        }
        else {
            if(article_id !=null && !article_id.equals("")) article = articleRepository.findArticleByID(Long.parseLong(article_id));
        }
        Equipement equipement = equipementRepository.findEquipementByID(Long.parseLong(id));

        Equipement_Article equipement_article = new Equipement_Article();
        equipement_article.setArticle(article);
        equipement_article.setEquipement(equipement);
        equipementArticleRepository.save(equipement_article);

        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AFFECTER L'ARTICLE : " + article.getLibelle() + " Á L'ÉQUIPEMENT : " + equipement.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("../equipements_articles/"+equipement.getId()+"?added="+added);
    }
    @PostMapping(value = "/equipements_articles/deleteArticle")
    public RedirectView deleteArticle(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");

        Equipement_Article equipement_article =  equipementArticleRepository.findByID(Long.parseLong(id));
        Article article =equipement_article.getArticle();
        Equipement equipement =equipement_article.getEquipement();
        equipementArticleRepository.delete(equipement_article);
        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER L'ARTICLE : "+article.getLibelle()+" DE L'ÉQUIPEMENT : "+equipement.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("../equipements_articles/"+equipement.getId()+"?deleted="+added);
    }
    @GetMapping("/equipements_status/{id}")
    public String equipements_status(@PathVariable(name = "id") Long id,Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_EQUIPEMENTS");
        Equipement equipement = equipementRepository.findEquipementByID(id);
        List<Equipement_Historique_Statut> equipements_status = histoStatutRepository.findAllByEquipement(equipement);

        model.addAttribute("equipements_status", equipements_status);
        model.addAttribute("equipement", equipement);
        model.addAttribute("privilege", privilege);

        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_EQUIPEMENTS");

        return "equipements/equipements_status";
    }
    @GetMapping("/equipements_historiques")
    public String equipements_historiques(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_HISTORIQUES");
        List<Equipement> equipements = equipementRepository.findAllEquipements(false);

        String str = request.getParameter("equipement_id");
        if(str != null && !"".equals(str)){
            Equipement equipement = equipementRepository.findEquipementByID(Long.parseLong(str));
            if(equipement.getType() != null && equipement.getType().getLibelle().equals("Mobile")){
                List<Equipement_Historique_Emplacement> historiques = histoEmplacementRepository.findAllByEquipement(equipement);
                model.addAttribute("historiques", historiques);
            }else{
                List<Equipement_Historique_Installation> historiques = histoInstallationRepository.findAllByEquipement(equipement);
                model.addAttribute("historiques", historiques);
            }

            model.addAttribute("equipement", equipement);
        }

        model.addAttribute("equipements", equipements);
        model.addAttribute("privilege", privilege);

        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_HISTORIQUES");

        return "equipements/equipements_historiques";
    }




    //*************************************INSTALLATIONS*********************************************************************************
    @GetMapping("/installations_list")
    public String installations_list(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_INSTALLATIONS");
        List<Installation> installations = installationRepository.findAllInstallations(false);
        for(Installation type : installations){
            boolean canDelete = true;
            boolean canUpdate = true;
            List<Equipement> equipements = equipementRepository.findAllEquipementsByInstallation(type,false);
            if(equipements.size() > 0){
                canDelete = false;
                canUpdate = false;
                
            }

            type.setCanDelete(canDelete);
            type.setCanUpdate(canUpdate);
        }

        List<Emplacement> emplacements = emplacementRepository.findAllEmplacemets(false);

        model.addAttribute("emplacements", emplacements);
        model.addAttribute("installations", installations);
        model.addAttribute("privilege", privilege);


        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_INSTALLATIONS");



        return "equipements/installations_list";
    }
    @GetMapping("/installations_add")
    public String installations_add(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_INSTALLATIONS");
        List<String> ref_installations = installationRepository.findAllRef();
        List<Emplacement> emplacements = emplacementRepository.findAllEmplacemets(false);
        List<Installation_Status> status = installationStatutRepository.findAllStatus(false);


        model.addAttribute("status", status);
        model.addAttribute("ref_installations", ref_installations);
        model.addAttribute("emplacements", emplacements);
        model.addAttribute("privilege", privilege);
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_INSTALLATIONS");



        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("updatedPass") != null)
            model.addAttribute("updatedPass", request.getParameter("updatedPass"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        return "equipements/installations_add";
    }
    @PostMapping(value = "/installations_add")
    public RedirectView installations_add(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String libelle = request.getParameter("libelle");
        String code = request.getParameter("code");
        String date = request.getParameter("date");
        String description = request.getParameter("description");


        String emplacement_id = request.getParameter("emplacement_id");
        Emplacement emplacement = null;
        if(emplacement_id !=null && emplacement_id.equals("new")){
            String code_emplacement = request.getParameter("code_emplacement");
            String libelle_emplacement = request.getParameter("libelle_emplacement");
            emplacement= new Emplacement();
            emplacement.setEtatSupp(false); emplacement.setCode(code_emplacement);
            emplacement.setLibelle(libelle_emplacement);
            emplacementRepository.save(emplacement);
        }
        else{
            if(emplacement_id != null && !emplacement_id.equals("")) emplacement = emplacementRepository.findEmplacementByID(Long.parseLong(emplacement_id));
        }

        String status_id = request.getParameter("status_id");
        Installation_Status status = null;
        if(status_id !=null && status_id.equals("new")){
            String libelle_status = request.getParameter("libelle_status");
            status= new Installation_Status();
            status.setEtatSupp(false);
            status.setLibelle(libelle_status);
            installationStatutRepository.save(status);
        }
        else{
            if(status_id != null && !status_id.equals("")) status = installationStatutRepository.findStatusBy(Long.parseLong(status_id));
        }

        Installation installation = new Installation();
        installation.setCode(code);
        installation.setDescription(description);
        installation.setLibelle(libelle);
        installation.setEtatSupp(false);
        installation.setStatus(status);
        installation.setEmplacement(emplacement);

        if(date != null && !date.equals("")) installation.setDate(sdf.parse(date));
        installation = installationRepository.save(installation);

        if(status != null ){
            installationHistoStatutRepository.updateOldStatusByInstallation(installation);
            Installation_Historique_Statut histo = new Installation_Historique_Statut();
            histo.setInstallation(installation);
            histo.setStatus(status);
            histo.setActual(true);
            histo.setDate(new Date());
            installationHistoStatutRepository.save(histo);
        }


        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UNE NOUVELLE INSTALLATION : "+installation.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("installations_list?added="+added);
    }
    @GetMapping("/installations_edit/{id}")
    public String installations_edit(@PathVariable(name = "id") Long id,Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_INSTALLATIONS");
        Installation installation = installationRepository.findInstallationByID(id);
        List<String> ref_installations = installationRepository.findAllRef();
        List<Emplacement> emplacements = emplacementRepository.findAllEmplacemets(false);
        List<Installation_Status> status = installationStatutRepository.findAllStatus(false);


        model.addAttribute("installation", installation);
        model.addAttribute("status", status);
        model.addAttribute("ref_installations", ref_installations);
        model.addAttribute("emplacements", emplacements);
        model.addAttribute("privilege", privilege);
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_INSTALLATIONS");



        return "equipements/installations_edit";
    }
    @PostMapping(value = "/installations_edit")
    public RedirectView installations_edit(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String libelle = request.getParameter("libelle");
        String code = request.getParameter("code");
        String date = request.getParameter("date");
        String description = request.getParameter("description");


        String emplacement_id = request.getParameter("emplacement_id");
        Emplacement emplacement = null;
        if(emplacement_id !=null && emplacement_id.equals("new")){
            String code_emplacement = request.getParameter("code_emplacement");
            String libelle_emplacement = request.getParameter("libelle_emplacement");
            emplacement= new Emplacement();
            emplacement.setEtatSupp(false); emplacement.setCode(code_emplacement);
            emplacement.setLibelle(libelle_emplacement);
            emplacementRepository.save(emplacement);
        }
        else{
            if(emplacement_id != null && !emplacement_id.equals("")) emplacement = emplacementRepository.findEmplacementByID(Long.parseLong(emplacement_id));
        }

        String status_id = request.getParameter("status_id");
        Installation_Status status = null;
        if(status_id !=null && status_id.equals("new")){
            String libelle_status = request.getParameter("libelle_status");
            status= new Installation_Status();
            status.setEtatSupp(false);
            status.setLibelle(libelle_status);
            installationStatutRepository.save(status);
        }
        else{
            if(status_id != null && !status_id.equals("")) status = installationStatutRepository.findStatusBy(Long.parseLong(status_id));
        }

        Installation installation = installationRepository.findInstallationByID(Long.parseLong(request.getParameter("id")));
        Installation_Status oldstatus = installation.getStatus();
        installation.setCode(code);
        installation.setDescription(description);
        installation.setLibelle(libelle);
        installation.setStatus(status);
        installation.setEmplacement(emplacement);

        if(date != null && !date.equals("")) installation.setDate(sdf.parse(date));
        installation = installationRepository.save(installation);

        if(status != null && (oldstatus != status || oldstatus == null)){
           installationHistoStatutRepository.updateOldStatusByInstallation(installation);
            Installation_Historique_Statut histo = new Installation_Historique_Statut();
            histo.setInstallation(installation);
            histo.setStatus(status);
            histo.setActual(true);
            histo.setDate(new Date());
            installationHistoStatutRepository.save(histo);
        }


        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER L'INSTALLATION : "+installation.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("installations_list?updated="+added);
    }
    @PostMapping(value = "/installations_delete")
    public RedirectView installations_delete(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");

        Installation installation =  installationRepository.findInstallationByID(Long.parseLong(id));
        installation.setEtatSupp(true);
        installationRepository.save(installation);
        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER L'INSTALLATION : "+installation.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("installations_list?deleted="+added);
    }
    @GetMapping("/installations_status/{id}")
    public String installations_status(@PathVariable(name = "id") Long id,Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_INSTALLATIONS");
        Installation installation = installationRepository.findInstallationByID(id);
        List<Installation_Historique_Statut> installations_status = installationHistoStatutRepository.findAllByInstallation(installation);

        model.addAttribute("installations_status", installations_status);
        model.addAttribute("installation", installation);
        model.addAttribute("privilege", privilege);

        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_INSTALLATIONS");

        return "equipements/installations_status";
    }




    //*************************************EMPLACEMENTS*********************************************************************************
    @GetMapping("/emplacements_list")
    public String emplacements_list(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_EMPLACEMENTS");
        List<Emplacement> emplacements = emplacementRepository.findAllEmplacemets(false);
        model.addAttribute("emplacements", emplacements);
        for(Emplacement type : emplacements){
            boolean canDelete = true;
            boolean canUpdate = true;
            List<Equipement> equipements = equipementRepository.findAllEquipementsByEmplacement(type,false);
            if(equipements.size() > 0){
                canDelete = false;
                canUpdate = false;
                
            }
            List<Installation> installations = installationRepository.findAllInstallationsByEmplacement(type,false);
            if(equipements.size() > 0){
                canDelete = false;
                canUpdate = false;
                
            }

            type.setCanDelete(canDelete);
            type.setCanUpdate(canUpdate);
        }

        List<String> codeemplacements = emplacementRepository.findCodeEmplacements(false);
        model.addAttribute("codeemplacements", codeemplacements);

        model.addAttribute("emplacements", emplacements);
        model.addAttribute("privilege", privilege);


        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_EMPLACEMENTS");



        return "equipements/emplacements_list";
    }
    @PostMapping(value = "/emplacements_add")
    public RedirectView emplacements_add(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String libelle = request.getParameter("libelle");
        String code = request.getParameter("code");
        String immeuble = request.getParameter("immeuble");
        String etage = request.getParameter("etage");
        String zone = request.getParameter("zone");

        Emplacement emplacement = new Emplacement();
        emplacement.setCode(code);
        emplacement.setLibelle(libelle);
        emplacement.setImmeuble(immeuble);
        emplacement.setEtage(etage);
        emplacement.setZone(zone);
        emplacement.setEtatSupp(false);
        emplacementRepository.save(emplacement);
        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UN NOUVEL EMPLACEMENT : "+emplacement.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("emplacements_list?added="+added);
    }
    @PostMapping(value = "/emplacements_edit")
    public RedirectView emplacements_edit(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String libelle = request.getParameter("libelle");
        String code = request.getParameter("code");
        String immeuble = request.getParameter("immeuble");
        String etage = request.getParameter("etage");
        String zone = request.getParameter("zone");

        Emplacement emplacement =emplacementRepository.findEmplacementByID(Long.parseLong(request.getParameter("id")));
        emplacement.setCode(code);
        emplacement.setLibelle(libelle);
        emplacement.setImmeuble(immeuble);
        emplacement.setEtage(etage);
        emplacement.setZone(zone);
        emplacementRepository.save(emplacement);
        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER L'EMPLACEMENT : "+emplacement.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("emplacements_list?updated="+added);
    }
    @PostMapping(value = "/emplacements_delete")
    public RedirectView emplacements_delete(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");

        Emplacement emplacement =  emplacementRepository.findEmplacementByID(Long.parseLong(id));
        emplacement.setEtatSupp(true);
        emplacementRepository.save(emplacement);
        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER L'EMPLACEMENT : "+emplacement.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("emplacements_list?deleted="+added);
    }


    //*************************************Stock*********************************************************************************
    @GetMapping("/stock/articles_list")
    public String articles_list(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_STOCK_ARTICLES");
        List<Unite> unites = uniteRepository.findAllUnites(false);
        List<Article> articles = articleRepository.findAllArticles(false);
        for(Article type : articles){
            boolean canDelete = true;
            boolean canUpdate = true;
            List<Equipement_Article> equipements = equipementArticleRepository.findAllByArticle(type);
            if(equipements.size() > 0){
                canDelete = false;
                canUpdate = false;
                
            }

            List<Mouvements_Article> mouvementsArticles = mouvementArticleRepository.findMouvementsByArticle(type);
            if(mouvementsArticles.size() > 0){
                canDelete = false;
                canUpdate = false;
                
            }

            type.setCanDelete(canDelete);
            type.setCanUpdate(canUpdate);
        }


        model.addAttribute("unites", unites);
        model.addAttribute("articles", articles);

        model.addAttribute("privilege", privilege);


        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_STOCK");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_STOCK_ARTICLES");



        return "equipements/stock_articles_list";
    }
     @GetMapping("/stock/articles_expired")
    public String articles_expired(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_STOCK_ARTICLES");
        List<Unite> unites = uniteRepository.findAllUnites(false);
        List<Article> articles = new ArrayList<>();
        List<Article> articlesKM = articleRepository.findArticlesKMToChange(false);
        List<Article> articlesDuree = articleRepository.findArticlesDureeToChange(false,new Date());

        articles.addAll(articlesKM);
        articles.addAll(articlesDuree);

        for(Article type : articles){
            boolean canDelete = true;
            boolean canUpdate = true;
            List<Equipement_Article> equipements = equipementArticleRepository.findAllByArticle(type);
            if(equipements.size() > 0){
                canDelete = false;
                canUpdate = false;

            }

            List<Mouvements_Article> mouvementsArticles = mouvementArticleRepository.findMouvementsByArticle(type);
            if(mouvementsArticles.size() > 0){
                canDelete = false;
                canUpdate = false;

            }

            type.setCanDelete(canDelete);
            type.setCanUpdate(canUpdate);
        }


        model.addAttribute("unites", unites);
        model.addAttribute("articles", articles);

        model.addAttribute("privilege", privilege);
        model.addAttribute("expired", true);


        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_STOCK");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_STOCK_ARTICLES");



        return "equipements/stock_articles_list";
    }
    @PostMapping(value = "/stock/articles_add")
    public RedirectView articles_add(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String article_unite = request.getParameter("article_unite");
        Unite unite = null;
        if (article_unite != null && article_unite.equals("new")) {
            String unite_libelle = request.getParameter("unite_libelle");
            String unite_code = request.getParameter("unite_code");

            unite = new Unite();
            unite.setEtatSupp(false);
            unite.setCode(unite_code);
            unite.setLibelle(unite_libelle);
            unite = uniteRepository.save(unite);
        }
        else {
            if(article_unite !=null && !article_unite.equals("")) unite = uniteRepository.findUniteByID(Long.parseLong(article_unite));
        }

        String has_compteur = request.getParameter("has_compteur");
        String type_compteur = null;
        boolean compteur = false;
        Long seuil_compteur = null;
        Date date_initiale_compteur = null;
        Date date_fin_compteur = null;
        Long valeur_initiale_compteur = null;
        if(has_compteur != null && (has_compteur.equals("Propre compteur") || has_compteur.equals("hybride"))) {
            compteur = true;
            type_compteur = request.getParameter("type_compteur");
            if(type_compteur != null && type_compteur.equals("nombre")) {
                seuil_compteur = Long.parseLong(request.getParameter("seuil_compteur_km"));
                valeur_initiale_compteur = Long.parseLong(request.getParameter("valeur_compteur_km"));
            }else{
                seuil_compteur = Long.parseLong(request.getParameter("seuil_compteur_duree"));
                String str = request.getParameter("valeur_compteur_duree");
                if(str != null && !str.equals("")) {
                    date_initiale_compteur = sdf.parse(str);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date_initiale_compteur);
                    cal.add(Calendar.MONTH, seuil_compteur.intValue());

                    date_fin_compteur = cal.getTime();
                }
            }

        }


        String article_code = request.getParameter("article_code");
        String article_libelle = request.getParameter("article_libelle");
        Double price = Double.parseDouble(request.getParameter("article_pmp"));

        Article article = new Article();
        article.setEtatSupp(false);
        article.setCode(article_code);
        article.setLibelle(article_libelle);
        article.setUnite(unite);
        article.setPmp(price);
        article.setHas_compteur(has_compteur);
        article.setType_compteur(type_compteur);
        article.setValeur_initiale_compteur(valeur_initiale_compteur);
        article.setValeur_actuelle_compteur(valeur_initiale_compteur);
        article.setDate_initiale_compteur(date_initiale_compteur);
        article.setDate_fin_compteur(date_fin_compteur);
        article.setSeuil_compteur(seuil_compteur);
        article = articleRepository.save(article);



        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UN NOUVEAU ARTICLE : "+article.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("articles_list?added="+added);
    }
    @PostMapping(value = "/stock/articles_edit")
    public RedirectView articles_edit(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String article_unite = request.getParameter("article_unite");
        Unite unite = null;
        if (article_unite != null && article_unite.equals("new")) {
            String unite_libelle = request.getParameter("unite_libelle");
            String unite_code = request.getParameter("unite_code");

            unite = new Unite();
            unite.setEtatSupp(false);
            unite.setCode(unite_code);
            unite.setLibelle(unite_libelle);
            unite = uniteRepository.save(unite);
        }
        else {
            if(article_unite !=null && !article_unite.equals("")) unite = uniteRepository.findUniteByID(Long.parseLong(article_unite));
        }


        String article_code = request.getParameter("article_code");
        String has_compteur = request.getParameter("has_compteur2");
        String valeur_compteur = request.getParameter("valeur_compteur2");
        String article_libelle = request.getParameter("article_libelle");
        Double price = Double.parseDouble(request.getParameter("article_pmp"));
        String type_compteur = null;
        boolean compteur = false;
        Long seuil_compteur = null;
        Date date_initiale_compteur = null;
        Date date_fin_compteur = null;
        Long valeur_initiale_compteur = null;
        if(has_compteur != null && (has_compteur.equals("Propre compteur") || has_compteur.equals("hybride"))) {
            compteur = true;
            type_compteur = request.getParameter("type_compteur2");
            if(type_compteur != null && type_compteur.equals("nombre")) {
                seuil_compteur = Long.parseLong(request.getParameter("seuil_compteur_km"));
                valeur_initiale_compteur = Long.parseLong(request.getParameter("valeur_compteur_km"));
            }else{
                seuil_compteur = Long.parseLong(request.getParameter("seuil_compteur_duree"));
                String str = request.getParameter("valeur_compteur_duree");
                if(str != null && !str.equals("")) {
                    date_initiale_compteur = sdf.parse(str);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date_initiale_compteur);
                    cal.add(Calendar.MONTH, seuil_compteur.intValue());

                    date_fin_compteur = cal.getTime();
                }
            }

        }


        Article article = articleRepository.findArticleByID(Long.parseLong(request.getParameter("id")));
        article.setEtatSupp(false);
        article.setCode(article_code);
        article.setLibelle(article_libelle);
        article.setUnite(unite);
        article.setPmp(price);
        article.setHas_compteur(has_compteur);
        article.setType_compteur(type_compteur);
        article.setValeur_initiale_compteur(valeur_initiale_compteur);
        article.setDate_initiale_compteur(date_initiale_compteur);
        article.setDate_fin_compteur(date_fin_compteur);
        article.setSeuil_compteur(seuil_compteur);
        article = articleRepository.save(article);


        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER L'ARTICLE : "+article.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("articles_list?updated="+added);
    }
    @PostMapping(value = "/stock/articles_delete")
    public RedirectView articles_delete(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");

        Article article = articleRepository.findArticleByID(Long.parseLong(request.getParameter("id")));
        article.setEtatSupp(true);
        articleRepository.save(article);
        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER L'ARTICLE : "+article.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("articles_list?deleted="+added);
    }

    @GetMapping("/stock/mvmts")
    public String mvmts(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_STOCK_MVMT");

        List<Article> articles= articleRepository.findAllArticles(false);
        List<Entrepot> entrepots= entrepotRepository.findAllEntrepots(false);
        Article article= null;
        Entrepot entrepot= null;
        String str = request.getParameter("article_id");
        if (str != null && !"".equals(str)) {
            article = articleRepository.findArticleByID(Long.parseLong(str));
            model.addAttribute("article",article);
            str = request.getParameter("entrepot_id");
            if (str != null && !"".equals(str)) {
                entrepot = entrepotRepository.findEntrepotByID(Long.parseLong(str));
                model.addAttribute("entrepot",entrepot);
            }
        }
        List<Mouvements_Article> mvmts = mouvementArticleRepository.findMouvementsByArticleAndEntrepot(entrepot,article);

        model.addAttribute("mvmts", mvmts);
        model.addAttribute("articles", articles);
        model.addAttribute("entrepots", entrepots);
        model.addAttribute("privilege", privilege);


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_STOCK");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_STOCK_MVMT");



        return "equipements/stock_mvmts";
    }

    @GetMapping("/stock/stock")
    public String stock(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_STOCK_STOCK");

        List<Article> articles= articleRepository.findAllArticles(false);
        List<Entrepot> entrepots= entrepotRepository.findAllEntrepots(false);
        Article article= null;
        Entrepot entrepot= null;
        String str = request.getParameter("article_id");
        if (str != null && !"".equals(str)) {
            article = articleRepository.findArticleByID(Long.parseLong(str));
            model.addAttribute("article",article);
            str = request.getParameter("entrepot_id");
            if (str != null && !"".equals(str)) {
                entrepot = entrepotRepository.findEntrepotByID(Long.parseLong(str));
                model.addAttribute("entrepot",entrepot);
            }
        }
        List<Article_Stock> stock = articleStockRepository.findStockByEntrepot(entrepot,article);

        model.addAttribute("stock", stock);
        model.addAttribute("articles", articles);
        model.addAttribute("entrepots", entrepots);
        model.addAttribute("privilege", privilege);


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_STOCK");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_STOCK_STOCK");


        return "equipements/stock_stock";
    }

    @GetMapping("/stock/entrepots")
    public String entrepots(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_ENTREPOT");
        List<Entrepot> entrepots = entrepotRepository.findAllEntrepots(false);
        for(Entrepot type : entrepots){
            boolean canDelete = false;
            boolean canUpdate = false;
            List<Mouvements_Article> mvmts = mouvementArticleRepository.findMouvementsByEntrepot(type);
            if(mvmts.size() == 0){
                canDelete=true;
                canUpdate=true;
            }

            type.setCanDelete(canDelete);
            type.setCanUpdate(canUpdate);
        }

        model.addAttribute("entrepots", entrepots);
        model.addAttribute("privilege", privilege);
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_ENTREPOT");



        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("updatedPass") != null)
            model.addAttribute("updatedPass", request.getParameter("updatedPass"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        return "equipements/stock_entrepots";
    }
    @PostMapping(value = "/stock/addEntrepot")
    public RedirectView addEntrepot(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String libelle = request.getParameter("libelle");
        Entrepot type = new Entrepot();
        type.setLibelle(libelle);
        entrepotRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UN NOUVEL ENTREPÖT :"+libelle);
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("entrepots?added=ok");
    }
    @PostMapping(value = "/stock/editEntrepot")
    public RedirectView editEntrepot(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String id = request.getParameter("id");
        String libelle = request.getParameter("libelle");
        Entrepot type = entrepotRepository.findEntrepotByID(Long.parseLong(id));
        type.setLibelle(libelle);
        entrepotRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER L'ENTREPÔT :"+libelle);
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("entrepots?updated=ok");
    }
    @PostMapping(value = "/stock/deleteEntrepot")
    public RedirectView deleteEntrepot(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String id = request.getParameter("id");
        Entrepot type = entrepotRepository.findEntrepotByID(Long.parseLong(id));
        type.setEtatSupp(true);
        entrepotRepository.save(type);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER L'ENTREPÔT :"+type.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("entrepots?deleted=ok");
    }

    @GetMapping("/stock/inventaires")
    public String inventaires(Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_STOCK_INV");
        List<Entrepot> entrepots = entrepotRepository.findAllEntrepots(false);
        List<Inv_Physique> inventaire_physiques = inventairePhysiqueRepository.findAllInventaires(false);
        List<Inv_Physique> encours = inventairePhysiqueRepository.findAllInventairesEncours(false);
        for(Inv_Physique e :inventaire_physiques){
            boolean canUpdate = true;
            boolean canDelete = true;
            List<Inv_Physique_Details> details = inventairePhysiqueDetailRepository.findAllByInventaire(false,e);
            if(details.size() >0){
                canUpdate = false;
                canDelete = false;
            }
            e.setCanDelete(canDelete);
            e.setCanUpdate(canUpdate);

        }

        boolean canAdd = true;
        if(encours.size()>0) canAdd = false;
        model.addAttribute("canAdd", canAdd);
        model.addAttribute("entrepots", entrepots);
        model.addAttribute("inventaires", inventaire_physiques);
        model.addAttribute("privilege", privilege);


        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("validated") != null)
            model.addAttribute("validated", request.getParameter("validated"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_STOCK");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_STOCK_INV");


        return "equipements/stock_inventaires_list";
    }
    @PostMapping(value = "/stock/addInventaire")
    public RedirectView addInventaire(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String added = "nok";

        String date = request.getParameter("date");
        String entrepot_id = request.getParameter("entrepot_id");
        Entrepot entrepot = entrepotRepository.findEntrepotByID(Long.parseLong(entrepot_id));

        Inv_Physique d = new Inv_Physique();
        d.setEtatSupp(false);
        d.setDate(sdf.parse(date));
        d.setEntrepot(entrepot);
        d.setValid(false);
        inventairePhysiqueRepository.save(d);

        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UN INVENTSIRE PHYSIQUE "+d.getId()+" á L'ENTREPÔT : "+entrepot.getCode());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("inventaires?added=ok");
    }
    @PostMapping(value = "/stock/editInventaire")
    public RedirectView editInventaire(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String added = "ok";
        String id = request.getParameter("id");
        String entrepot_id = request.getParameter("entrepot_id");
        String date = request.getParameter("date");
        Inv_Physique inventaire = inventairePhysiqueRepository.findByID(Long.parseLong(id));
        Entrepot entrepot = entrepotRepository.findEntrepotByID(Long.parseLong(entrepot_id));

        inventaire.setDate(sdf.parse(date));
        inventaire.setEntrepot(entrepot);
        inventairePhysiqueRepository.save(inventaire);

        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER  L'INVENTAIRE  : "+inventaire.getId());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("inventaires?updated=ok");
    }
    @PostMapping(value = "/stock/deleteInventaire")
    public RedirectView deleteInventaire(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String added = "nok";

        String id = request.getParameter("id");
        Inv_Physique inventaire = inventairePhysiqueRepository.findByID(Long.parseLong(id));

        inventaire.setEtatSupp(true);
        inventairePhysiqueRepository.save(inventaire);

        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER  L'INVENTAIRE  : "+inventaire.getId());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("inventaires?deleted=ok");
    }
    @PostMapping(value = "/stock/validerInventaire")
    public RedirectView validInventaire(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(user.getUsername());
        String added = "nok";

        String id = request.getParameter("id");
        Inv_Physique inventaire = inventairePhysiqueRepository.findByID(Long.parseLong(id));

        inventaire.setValid(true);
        inventaire.setDate_validation(new Date());
        inventairePhysiqueRepository.save(inventaire);

        mouvementArticleRepository.updateIPByEntrepotAndDate(inventaire.getEntrepot(),inventaire.getDate());

        // Remplacer par nouveau stock
        List<Inv_Physique_Details> ipdList = inventairePhysiqueDetailRepository.findAllByInventaire(false,inventaire);
        for (Inv_Physique_Details ipd : ipdList) {
            Article_Stock old_stock = articleStockRepository.findStockByEntrepotAndArticle(inventaire.getEntrepot(),ipd.getArticle());
            double oldQte=0;
            double newQte=ipd.getQuantite();
            if (old_stock != null ) oldQte = old_stock.getStock_reel();
            double somme = (newQte - oldQte);
            String op="";
            String lib="";

            if (somme > 0) {
                //MVT stock :
                lib = "Incrémentation par Inventaire ( N° " +inventaire.getId()+" ) valeur  " + somme;
            } else if (somme == 0) {
                //MVT stock :
                lib = "Égal par Inventaire ( N° " +inventaire.getId()+" ) valeur  " + somme;
            } else {
                //MVT stock :
                lib = "Décrémentation par Inventaire ( N° " +inventaire.getId()+" ) valeur  " + somme;
            }
            Mouvements_Article mvmt_inventaire = new Mouvements_Article();
            mvmt_inventaire.setEntrepot(inventaire.getEntrepot());
            mvmt_inventaire.setArticle(ipd.getArticle());
            mvmt_inventaire.setDate(inventaire.getDate());
            mvmt_inventaire.setDescription(lib);
            mvmt_inventaire.setQuantite(newQte);
            mvmt_inventaire.setCump(ipd.getPrix_unitaire());
            mvmt_inventaire.set_ip(true);
            mouvementArticleRepository.save(mvmt_inventaire);


            if(old_stock != null) articleStockRepository.delete(old_stock);
            Article_Stock ipr = new Article_Stock();
            ipr.setArticle(ipd.getArticle());
            ipr.setPrix_unitaire(ipd.getArticle().getPmp());
            ipr.setEntrepot(inventaire.getEntrepot());
            ipr.setStock_reel(ipd.getQuantite());
            ipr.setStock_theorique(ipd.getQuantite());
            articleStockRepository.save(ipr);

        }

        List<Article> articles = articleRepository.findAllArticles(false);
        for (Article article : articles) {
            Inv_Physique_Details ipd = inventairePhysiqueDetailRepository.findInv_Physique_DetailsbyIPAndArticle(inventaire,article);
            if (ipd != null) {
                double cump = ipd.getPrix_unitaire();
                double stock = ipd.getQuantite();
                double[] ds = stockService.recalculateCUMPAndStock(article, inventaire.getEntrepot(), cump, stock);
                cump = ds[0];
                stock = ds[1];
                Article_Stock ipr = articleStockRepository.findStockByEntrepotAndArticle(inventaire.getEntrepot(), article);
                ipr.setPrix_unitaire(cump);
                ipr.setStock_reel(stock);
                ipr.setStock_theorique(stock);
                articleStockRepository.save(ipr);

                continue;
            }
            if (ipd == null) {
                Article_Stock old_stock = articleStockRepository.findStockByEntrepotAndArticle(inventaire.getEntrepot(),article);
                double oldQte=0;
                double newQte=0;
                if (old_stock != null ) oldQte = old_stock.getStock_reel();
                double somme = (newQte - oldQte);
                String op="";
                String lib="";

                if (somme > 0) {
                    //MVT stock :
                    lib = "Incrémentation par Inventaire ( N° " +inventaire.getId()+" ) valeur  " + somme;
                } else if (somme == 0) {
                    //MVT stock :
                    lib = "Égal par Inventaire ( N° " +inventaire.getId()+" ) valeur  " + somme;
                } else {
                    //MVT stock :
                    lib = "Décrémentation par Inventaire ( N° " +inventaire.getId()+" ) valeur  " + somme;
                }
                Mouvements_Article mvmt_inventaire = new Mouvements_Article();
                mvmt_inventaire.setEntrepot(inventaire.getEntrepot());
                mvmt_inventaire.setArticle(article);
                mvmt_inventaire.setDate(inventaire.getDate());
                mvmt_inventaire.setDescription(lib);
                mvmt_inventaire.setQuantite(newQte);
                mvmt_inventaire.setCump(article.getPmp());
                mvmt_inventaire.set_ip(true);
                mouvementArticleRepository.save(mvmt_inventaire);


                double cump = article.getPmp();
                double stock = 0;
                double[] ds = stockService.recalculateCUMPAndStock(article, inventaire.getEntrepot(), cump, stock);
                cump = ds[0];
                stock = ds[1];
                if (old_stock != null) articleStockRepository.delete(old_stock);
                Article_Stock ipr = new Article_Stock();
                ipr.setArticle(article);
                ipr.setPrix_unitaire(cump);
                ipr.setEntrepot(inventaire.getEntrepot());
                ipr.setStock_reel(stock);
                ipr.setStock_theorique(stock);
                articleStockRepository.save(ipr);
            }

        }
        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("VALIDER  L'INVENTAIRE  : "+inventaire.getId());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("inventaires?validated=ok");
    }
    @GetMapping("/stock/inventaires/detail/{id}")
    public String inv_detail(@PathVariable(name = "id") Long id, Model model, @AuthenticationPrincipal User user, HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_STOCK_INV");

        Inv_Physique inv= inventairePhysiqueRepository.findByID(id);
        List<Inv_Physique_Details> details = inventairePhysiqueDetailRepository.findAllByInventaire(false,inv);
        List<Article> articles = articleRepository.findArticlesNotAffectINV(false,inv);

        boolean canUpdate = true;
        boolean canDelete = true;
        if(details.size() >0){
            canUpdate = false;
            canDelete = false;
        }
        inv.setCanDelete(canDelete);
        inv.setCanUpdate(canUpdate);

        model.addAttribute("articles", articles);
        model.addAttribute("details", details);
        model.addAttribute("inventaire", inv);
        model.addAttribute("privilege", privilege);


        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));
        if (request.getParameter("validated") != null)
            model.addAttribute("validated", request.getParameter("validated"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_STOCK");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_STOCK_INV");



        return "equipements/stock_inventaires_detail";
    }
    @PostMapping(value = "/stock/addInvDetail")
    public RedirectView addInvDetail(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        double qte = Double.parseDouble(request.getParameter("qte"));
        String inventaire_id = request.getParameter("inventaire_id");
        String article_id = request.getParameter("article_id");
        Article article = articleRepository.findArticleByID(Long.parseLong(article_id));
        Inv_Physique inventaire = inventairePhysiqueRepository.findByID(Long.parseLong(inventaire_id));

        double cump =article.getPmp();
        Article_Stock stock = articleStockRepository.findStockByEntrepotAndArticle(inventaire.getEntrepot(),article);
        if(stock != null ) cump=stock.getPrix_unitaire();

        Inv_Physique_Details d = new Inv_Physique_Details();
        d.setEtatSupp(false);
        d.setArticle(article);
        d.setQuantite(qte);
        d.setInv_physique(inventaire);
        d.setPrix_unitaire(cump);
        inventairePhysiqueDetailRepository.save(d);

        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER L'ARTICLE "+article.getLibelle()+" A L'INVENTAIRE  : "+inventaire.getId());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("inventaires/detail/"+inventaire.getId()+"?added="+added);
    }
    @PostMapping(value = "/stock/deleteInvDetail")
    public RedirectView deleteInvDetail(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String deleted = "nok";

        String id = request.getParameter("id");

        Inv_Physique_Details d =  inventairePhysiqueDetailRepository.findByID(Long.parseLong(id));
        d.setEtatSupp(true);
        inventairePhysiqueDetailRepository.save(d);

        deleted = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER L'ARTICLE "+d.getArticle().getLibelle()+" De L'INVENTAIRE  : "+d.getInv_physique().getId());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("inventaires/detail/"+d.getInv_physique().getId()+"?deleted="+deleted);
    }


    @GetMapping("/stock/bs_list")
    public String bss(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_STOCK_BS");
        List<Intervention> interventions = interventionRepository.findAllInterventionsEncours(false);
        List<Entrepot> entrepots = entrepotRepository.findAllEntrepots(false);
        List<Bon_Sortie> bon_sorties = bonSortieRepository.findAllBon_Sorties(false);
        for(Bon_Sortie e :bon_sorties){
            e.setCanDelete(true);
            e.setCanUpdate(true);

        }
        model.addAttribute("entrepots", entrepots);
        model.addAttribute("interventions", interventions);
        model.addAttribute("bss", bon_sorties);
        model.addAttribute("privilege", privilege);


        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("validated") != null)
            model.addAttribute("validated", request.getParameter("validated"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_STOCK");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_STOCK_BS");

        return "equipements/bs_list";
    }
    @GetMapping("/stock/bs_add")
    public String addInstallation(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_STOCK_BS");
        model.addAttribute("privilege", privilege);

        List<Intervention> interventions = interventionRepository.findAllInterventionsEncours(false);
        List<Entrepot> entrepots = entrepotRepository.findAllEntrepots(false);

        String str =request.getParameter("intervention_id");
        if(str != null && !"".equals(str)) {
            Intervention intervention = interventionRepository.findInterventionByID(Long.parseLong(str));
            model.addAttribute("intervention", intervention);
        }
        model.addAttribute("entrepots", entrepots);
        model.addAttribute("interventions", interventions);

        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_STOCK");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_STOCK_BS");


        return "equipements/bs_add";
    }
    @PostMapping(value = "/stock/bs_add")
    public RedirectView addBS(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";
        String updated = "nok";
        String actions ="";

        String date = request.getParameter("date");
        String code = request.getParameter("code");
        String intervention_id = request.getParameter("intervention_id");
        String entrepot_id = request.getParameter("entrepot_id");
        Intervention intervention = interventionRepository.findInterventionByID(Long.parseLong(intervention_id));
        Entrepot entrepot = entrepotRepository.findEntrepotByID(Long.parseLong(entrepot_id));
        Bon_Sortie_Status status = bonSortieStatusRepository.findStatusByID(1);

        Bon_Sortie br= new Bon_Sortie();
        br.setEtatSupp(false);
        br.setCode(code);
        br.setDate(sdf.parse(date));
        br.setEntrepot(entrepot);
        br.setIntervention(intervention);
        br.setStatus(status);
        bonSortieRepository.save(br);
        added = "ok";

        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UN BON DE SORTIE : "+br.getCode()+" POUR L'INTERVENTION : "+intervention.getCode());
        history.setDateCreation(new Date());
        history.setModule("STOCK");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("bs_detail/"+br.getId()+"?added="+added);
    }
    @PostMapping(value = "/stock/bs_edit")
    public RedirectView editBS(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String updated = "nok";

        String id = request.getParameter("id");
        String date = request.getParameter("date");
        String code = request.getParameter("code");
        String intervention_id = request.getParameter("intervention_id");
        String entrepot_id = request.getParameter("entrepot_id");
        Intervention intervention = interventionRepository.findInterventionByID(Long.parseLong(intervention_id));
        Entrepot entrepot = entrepotRepository.findEntrepotByID(Long.parseLong(entrepot_id));
        Bon_Sortie_Status status = bonSortieStatusRepository.findStatusByID(1);


        Bon_Sortie br= bonSortieRepository.findBon_SortieByID(Long.parseLong(id));
        br.setCode(code);
        br.setDate(sdf.parse(date));
        br.setEntrepot(entrepot);
        br.setIntervention(intervention);
        bonSortieRepository.save(br);
        updated = "ok";

        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER LE BON DE SORTIE : "+br.getCode());
        history.setDateCreation(new Date());
        history.setModule("STOCK");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("bs_list?updated="+updated);

    }
    @PostMapping(value = "/stock/bs_delete")
    public RedirectView deleteBS(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());

        String deleted = "nok";
        String id = request.getParameter("id");
        Bon_Sortie br = bonSortieRepository.findBon_SortieByID(Long.parseLong(id));
        br.setEtatSupp(true);
        bonSortieRepository.save(br);
        deleted="ok";
        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER UN BON DE SORTIE : "+br.getCode());
        history.setDateCreation(new Date());
        history.setModule("STOCK");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************



        return new RedirectView("bs_list?deleted="+deleted);
    }
    @PostMapping(value = "/stock/bs_valider")
    public RedirectView validerBS(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());

        String validated = "nok";
        String id = request.getParameter("id");
        Bon_Sortie br = bonSortieRepository.findBon_SortieByID(Long.parseLong(id));
        Bon_Sortie_Status status = bonSortieStatusRepository.findStatusByID(2);
        br.setStatus(status);
        bonSortieRepository.save(br);
        Entrepot entrepot= br.getEntrepot();
        List<Bon_Sortie_Detail> details = bonSortieDetailRepository.findAllBon_SortiesByBs(false,br);
        for(Bon_Sortie_Detail d : details){
            Article article= d.getArticle();
            double q = d.getQte();

            // update STOCK_ACTUEL
            Article_Stock stock = articleStockRepository.findStockByEntrepotAndArticle(entrepot, article);
            if (stock == null) {
                stock = new Article_Stock();
                stock.setArticle(article);
                stock.setEntrepot(entrepot);
                stock.setStock_reel(0-q);
                stock.setStock_theorique(0-q);
            } else {
                stock.setStock_theorique(stock.getStock_theorique() - q);
                stock.setStock_reel(stock.getStock_reel() - q);
            }

            // Ajout Mouvement Article
            Mouvements_Article ma = new Mouvements_Article();
            ma.setArticle(article);
            ma.setEntrepot(entrepot);
            Intervention intervention = br.getIntervention();
            ma.setDescription("BS INTERVENTION N° " + intervention.getCode());
            ma.setNumpiece(br.getCode());
            ma.setQuantite(0-q);
            ma.setDate(br.getDate());
            mouvementArticleRepository.save(ma);


            double cump = article.getPmp();
            double s = 0;
            double[] ds = stockService.recalculateCUMPAndStock(article, entrepot, cump, s);
            cump =ds[0];
            s =ds[1];
            stock.setPrix_unitaire(cump);
            stock.setStock_reel(s);
            articleStockRepository.save(stock);
        }
        validated="ok";
        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("VALIDER UN BON DE SORTIE : "+br.getCode());
        history.setDateCreation(new Date());
        history.setModule("STOCK");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        String action = request.getParameter("action");
        if(action != null && action.equals("detail")) {
            return new RedirectView("bs_detail/"+br.getId()+"?validated="+validated);
        }else{
            return new RedirectView("bs_list?validated="+validated);
        }

    }
    @GetMapping("/stock/bs_detail/{id}")
    public String bs_detail(@PathVariable(name = "id") Long id, Model model, @AuthenticationPrincipal User user, HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_STOCK_BS");

        Bon_Sortie br= bonSortieRepository.findBon_SortieByID(id);
        List<Bon_Sortie_Detail> details = bonSortieDetailRepository.findAllBon_SortiesByBs(false,br);
        Intervention intervention = br.getIntervention();

        List<Article> articles = articleRepository.findArticlesNotAffectBS(false,br);
        List<Unite> unites = uniteRepository.findAllUnites(false);

        model.addAttribute("unites", unites);
        model.addAttribute("articles", articles);
        model.addAttribute("details", details);
        model.addAttribute("bs", br);
        model.addAttribute("privilege", privilege);


        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));
        if (request.getParameter("validated") != null)
            model.addAttribute("validated", request.getParameter("validated"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_STOCK");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_STOCK_BS");


        return "equipements/bs_detail";
    }
    @PostMapping(value = "/stock/addBSDetail")
    public RedirectView addBSDetail(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        double qte = Double.parseDouble(request.getParameter("qte"));
        String br_id = request.getParameter("bs_id");
        String article_id = request.getParameter("article_id");
        Article article = null;
        if(article_id !=null && article_id.equals("new")){
            String article_code = request.getParameter("article_code");
            String article_libelle = request.getParameter("article_libelle");
            String article_unite = request.getParameter("article_unite");
            Double price = Double.parseDouble(request.getParameter("article_pmp"));

            Unite unite = uniteRepository.findUniteByID(Long.parseLong(article_unite));
            article= new Article();
            article.setEtatSupp(false);
            article.setCode(article_code);
            article.setLibelle(article_libelle);
            article.setUnite(unite);
            article.setPmp(price);
            article = articleRepository.save(article);

        }
        else{
            if(article_id !=null && !article_id.equals("")) article = articleRepository.findArticleByID(Long.parseLong(article_id));
        }
        Bon_Sortie br = bonSortieRepository.findBon_SortieByID(Long.parseLong(br_id));

        Bon_Sortie_Detail d = new Bon_Sortie_Detail();
        d.setEtatSupp(false);
        d.setArticle(article);
        d.setQte(qte);
        d.setBs(br);
        bonSortieDetailRepository.save(d);

        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER L'ARTICLE "+article.getLibelle()+" AU BS  : "+br.getCode());
        history.setDateCreation(new Date());
        history.setModule("STOCK");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("bs_detail/"+br.getId()+"?added="+added);
    }
    @PostMapping(value = "/stock/deleteBSDetail")
    public RedirectView deleteBSDetail(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String deleted = "nok";

        String id = request.getParameter("id");

        Bon_Sortie_Detail d =  bonSortieDetailRepository.findBon_SortieByID(Long.parseLong(id));
        d.setEtatSupp(true);
        bonSortieDetailRepository.save(d);

        deleted = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER L'ARTICLE "+d.getArticle().getLibelle()+" DU BS  : "+d.getBs().getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCE");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("bs_detail/"+d.getBs().getId()+"?deleted="+deleted);
    }


    //*************************************Fournisseurs*********************************************************************************
    @GetMapping("/fournisseurs_list")
    public String fournisseurs_list(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_FRN_FRN");
        List<Fournisseur_Categorie> categories = fournisseurCategorieRepository.findAll(false);
        List<Fournisseur_Article> societes = fournisseurArticleRepository.findAllFournisseurs(false);
        for(Fournisseur_Article e :societes){
            boolean canDelete=false;
            boolean canUpdate=false;

            e.setCanDelete(canDelete);
            e.setCanUpdate(canUpdate);
        }
        model.addAttribute("categories", categories);
        model.addAttribute("fournisseurs", societes);
        model.addAttribute("privilege", privilege);


        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_FRN");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_FRN_FRN");

        return "equipements/fournisseurs_list";
    }
    @GetMapping("/fournisseurs_add")
    public String addFournisseur(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_FRN_FRN");
        model.addAttribute("privilege", privilege);

        List<Pays> pays = paysRepository.findAllPayss(false);
        List<Province> provinces = provinceRepository.findAllProvincesByPays(false,12);
        List<Forme_Juridique> formes = formeJuridiqueRepository.findAllForme_JuridiquesByPays(false,12);
        List<Devise> devises = deviseRepository.findAllDevises(false);
        List<Activite> activites = activiteRepository.findAllActivites(false);
        List<Fournisseur_Categorie> categories = fournisseurCategorieRepository.findAll(false);
        List<Region> regions = regionRepository.findAllRegionsByPays(false,12);

        model.addAttribute("categories", categories);
        model.addAttribute("provinces", provinces);
        model.addAttribute("activites", activites);
        model.addAttribute("devises", devises);
        model.addAttribute("formes", formes);
        model.addAttribute("pays", pays);
        model.addAttribute("regions", regions);

        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_FRN");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_FRN_FRN");

        return "equipements/fournisseurs_add";
    }
    @PostMapping(value = "/fournisseurs_add")
    public RedirectView fournisseurs_add(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String rs = request.getParameter("rs");
        String pays_id = request.getParameter("pays_id");
        String province_id = request.getParameter("province_id");
        String forme_id = request.getParameter("forme_id");
        String devise_id = request.getParameter("devise_id");
        String activite_id = request.getParameter("activite_id");

        String address = request.getParameter("address");
        String ville = request.getParameter("ville");
        String zip = request.getParameter("zip");
        String phone = request.getParameter("phone");
        String fax = request.getParameter("fax");
        String email = request.getParameter("email");
        String web = request.getParameter("web");
        String rc = request.getParameter("rc");
        String patente = request.getParameter("patente");
        String iff = request.getParameter("iff");
        String cnss = request.getParameter("cnss");
        String ice = request.getParameter("ice");
        String capital = request.getParameter("capital");

        String[] cat_ids = request.getParameterValues("cat_ids");
        Collection<Fournisseur_Categorie> categories = new ArrayList<>();
        if (cat_ids != null) {
            for (int i = 0; i < cat_ids.length; i++) {
                Fournisseur_Categorie categorie=null;
                String cat_id = cat_ids[i];
                if(cat_id !=null && cat_id.equals("new")){
                    String libelle_cat = request.getParameter("libelle_cat");
                    categorie = new Fournisseur_Categorie();
                    categorie.setLibelle(libelle_cat);
                    fournisseurCategorieRepository.save(categorie);
                }
                else{
                    if(cat_id !=null && !cat_id.equals("")) {
                        categorie = fournisseurCategorieRepository.findByID(Long.parseLong(cat_id));
                    }
                }
                categories.add(categorie);
            }
        }


        Pays pays = null;
        if(pays_id !=null && pays_id.equals("new")){
            String libelle_pays = request.getParameter("libelle_pays");
            String code_pays = request.getParameter("code_pays");
            pays = new Pays();
            pays.setCode(code_pays);
            pays.setLabel(libelle_pays);
            paysRepository.save(pays);
        }
        else{
            if(pays_id !=null && !pays_id.equals("")) pays = paysRepository.findPaysByID(Integer.parseInt(pays_id));
        }

        Province province = null;
        if(province_id !=null && province_id.equals("new")){
            String libelle_province = request.getParameter("libelle_province");
            String code_province = request.getParameter("code_province");
            String region_province = request.getParameter("region_province");
            province = new Province();
            province.setCode_departement(code_province);
            province.setNom(libelle_province);
            province.setFk_region(Integer.parseInt(region_province));
            provinceRepository.save(province);
        }
        else{
            if(province_id !=null && !province_id.equals("")) province = provinceRepository.findProvinceByID(Integer.parseInt(province_id));
        }

        Forme_Juridique forme = null;
        if(forme_id !=null && forme_id.equals("new")){
            String libelle_forme = request.getParameter("libelle_forme");
            String code_forme = request.getParameter("code_forme");
            String pays_forme = request.getParameter("pays_forme");
            forme = new Forme_Juridique();
            forme.setCode(code_forme);
            forme.setLibelle(libelle_forme);
            forme.setFk_pays(Integer.parseInt(pays_forme));
            formeJuridiqueRepository.save(forme);
        }
        else{
            if(forme_id !=null && !forme_id.equals("")) forme = formeJuridiqueRepository.findForme_JuridiqueByID(Integer.parseInt(forme_id));
        }


        Devise devise = null;
        if(devise_id !=null && devise_id.equals("new")){
            String code_devise = request.getParameter("code_devise");
            String libelle_devise = request.getParameter("libelle_devise");
            devise = new Devise();
            devise.setCode_iso(code_devise);
            devise.setLabel(libelle_devise);
            deviseRepository.save(devise);
        }
        else{
            if(devise_id !=null && !devise_id.equals("")) devise = deviseRepository.findDeviseByID(devise_id);
        }


        Activite activite = null;
        if(activite_id !=null && activite_id.equals("new")){
            String code_activite = request.getParameter("code_activite");
            String libelle_activite = request.getParameter("libelle_activite");
            activite = new Activite();
            activite.setCode(code_activite);
            activite.setLabel(libelle_activite);
            activiteRepository.save(activite);
        }
        else{
            if(activite_id !=null && !activite_id.equals("")) activite = activiteRepository.findActiviteByID(Integer.parseInt(activite_id));
        }

        Fournisseur_Article fournisseur = new Fournisseur_Article();
        fournisseur.setCategories(categories);
        fournisseur.setPays(pays);
        fournisseur.setProvince(province);
        fournisseur.setForme(forme);
        fournisseur.setDevise(devise);
        fournisseur.setActivite(activite);

        fournisseur.setCapital(capital);
        fournisseur.setRs(rs);
        fournisseur.setPhone(phone);
        fournisseur.setFax(fax);
        fournisseur.setEmail(email);
        fournisseur.setUrl(web);
        fournisseur.setFournisseur(1);
        fournisseur.setZiptown(zip);
        fournisseur.setAddress(address);
        fournisseur.setTown(ville);
        fournisseur.setEtatSupp(false);

        fournisseur.setSiren(rc);
        fournisseur.setSiret(patente);
        fournisseur.setApe(iff);
        fournisseur.setIdprof4(cnss);
        fournisseur.setIdprof5(ice);
        fournisseurArticleRepository.save(fournisseur);

        added="ok";
        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UN FOURNISEUR: "+fournisseur.getRs());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("fournisseurs_detail/"+fournisseur.getId()+"?added="+added);
    }
    @GetMapping("/fournisseurs_edit/{id}")
    public String fournisseurs_edit(Model model,@PathVariable(name = "id") Long id, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_FRN_FRN");
        model.addAttribute("privilege", privilege);

        Fournisseur_Article societe = fournisseurArticleRepository.findSocieteByID(id);

        model.addAttribute("fournisseur", societe);
        List<Pays> pays = paysRepository.findAllPayss(false);
        List<Province> provinces = provinceRepository.findAllProvincesByPays(false,12);
        List<Forme_Juridique> formes = formeJuridiqueRepository.findAllForme_JuridiquesByPays(false,12);
        List<Devise> devises = deviseRepository.findAllDevises(false);
        List<Activite> activites = activiteRepository.findAllActivites(false);
        List<Fournisseur_Categorie> categories = fournisseurCategorieRepository.findAll(false);
        List<Region> regions = regionRepository.findAllRegionsByPays(false,12);


        model.addAttribute("categories", categories);
        model.addAttribute("provinces", provinces);
        model.addAttribute("activites", activites);
        model.addAttribute("devises", devises);
        model.addAttribute("formes", formes);
        model.addAttribute("pays", pays);
        model.addAttribute("regions", regions);



        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_FRN");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_FRN_FRN");


        return "equipements/fournisseurs_edit";
    }
    @PostMapping(value = "/fournisseurs_edit")
    public RedirectView fournisseurs_edit(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");
        String rs = request.getParameter("rs");
        String pays_id = request.getParameter("pays_id");
        String province_id = request.getParameter("province_id");
        String forme_id = request.getParameter("forme_id");
        String devise_id = request.getParameter("devise_id");
        String activite_id = request.getParameter("activite_id");

        String address = request.getParameter("address");
        String ville = request.getParameter("ville");
        String zip = request.getParameter("zip");
        String phone = request.getParameter("phone");
        String fax = request.getParameter("fax");
        String email = request.getParameter("email");
        String web = request.getParameter("web");
        String rc = request.getParameter("rc");
        String patente = request.getParameter("patente");
        String iff = request.getParameter("iff");
        String cnss = request.getParameter("cnss");
        String ice = request.getParameter("ice");
        String capital = request.getParameter("capital");

        fournisseurCategorieRepository.deleteCatsForFournisseurArticle(Long.parseLong(id));
        String[] cat_ids = request.getParameterValues("cat_ids");
        Collection<Fournisseur_Categorie> categories = new ArrayList<>();
        if (cat_ids != null) {
            for (int i = 0; i < cat_ids.length; i++) {
                Fournisseur_Categorie categorie=null;
                String cat_id = cat_ids[i];
                if(cat_id !=null && cat_id.equals("new")){
                    String libelle_cat = request.getParameter("libelle_cat");
                    categorie = new Fournisseur_Categorie();
                    categorie.setLibelle(libelle_cat);
                    fournisseurCategorieRepository.save(categorie);
                }
                else{
                    if(cat_id !=null && !cat_id.equals("")) {
                        categorie = fournisseurCategorieRepository.findByID(Long.parseLong(cat_id));
                    }
                }
                categories.add(categorie);
            }
        }

        Pays pays = null;
        if(pays_id !=null && pays_id.equals("new")){
            String libelle_pays = request.getParameter("libelle_pays");
            String code_pays = request.getParameter("code_pays");
            pays = new Pays();
            pays.setCode(code_pays);
            pays.setLabel(libelle_pays);
            paysRepository.save(pays);
        }
        else{
            if(pays_id !=null && !pays_id.equals("")) pays = paysRepository.findPaysByID(Integer.parseInt(pays_id));
        }

        Province province = null;
        if(province_id !=null && province_id.equals("new")){
            String libelle_province = request.getParameter("libelle_province");
            String code_province = request.getParameter("code_province");
            String region_province = request.getParameter("region_province");
            province = new Province();
            province.setCode_departement(code_province);
            province.setNom(libelle_province);
            province.setFk_region(Integer.parseInt(region_province));
            provinceRepository.save(province);
        }
        else{
            if(province_id !=null && !province_id.equals("")) province = provinceRepository.findProvinceByID(Integer.parseInt(province_id));
        }

        Forme_Juridique forme = null;
        if(forme_id !=null && forme_id.equals("new")){
            String libelle_forme = request.getParameter("libelle_forme");
            String code_forme = request.getParameter("code_forme");
            String pays_forme = request.getParameter("pays_forme");
            forme = new Forme_Juridique();
            forme.setCode(code_forme);
            forme.setLibelle(libelle_forme);
            forme.setFk_pays(Integer.parseInt(pays_forme));
            formeJuridiqueRepository.save(forme);
        }
        else{
            if(forme_id !=null && !forme_id.equals("")) forme = formeJuridiqueRepository.findForme_JuridiqueByID(Integer.parseInt(forme_id));
        }


        Devise devise = null;
        if(devise_id !=null && devise_id.equals("new")){
            String code_devise = request.getParameter("code_devise");
            String libelle_devise = request.getParameter("libelle_devise");
            devise = new Devise();
            devise.setCode_iso(code_devise);
            devise.setLabel(libelle_devise);
            deviseRepository.save(devise);
        }
        else{
            if(devise_id !=null && !devise_id.equals("")) devise = deviseRepository.findDeviseByID(devise_id);
        }


        Activite activite = null;
        if(activite_id !=null && activite_id.equals("new")){
            String code_activite = request.getParameter("code_activite");
            String libelle_activite = request.getParameter("libelle_activite");
            activite = new Activite();
            activite.setCode(code_activite);
            activite.setLabel(libelle_activite);
            activiteRepository.save(activite);
        }
        else{
            if(activite_id !=null && !activite_id.equals("")) activite = activiteRepository.findActiviteByID(Integer.parseInt(activite_id));
        }

        Fournisseur_Article fournisseur = fournisseurArticleRepository.findSocieteByID(Long.parseLong(id));
        fournisseur.setCategories(categories);
        fournisseur.setPays(pays);
        fournisseur.setProvince(province);
        fournisseur.setForme(forme);
        fournisseur.setDevise(devise);
        fournisseur.setActivite(activite);

        fournisseur.setCapital(capital);
        fournisseur.setRs(rs);
        fournisseur.setPhone(phone);
        fournisseur.setFax(fax);
        fournisseur.setEmail(email);
        fournisseur.setUrl(web);
        fournisseur.setFournisseur(1);
        fournisseur.setZiptown(zip);
        fournisseur.setAddress(address);
        fournisseur.setTown(ville);
        fournisseur.setEtatSupp(false);

        fournisseur.setSiren(rc);
        fournisseur.setSiret(patente);
        fournisseur.setApe(iff);
        fournisseur.setIdprof4(cnss);
        fournisseur.setIdprof5(ice);
        fournisseurArticleRepository.save(fournisseur);

        added="ok";

        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER LE FOURNISEUR: "+fournisseur.getRs());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("fournisseurs_detail/"+fournisseur.getId()+"?updated="+added);
    }
    @GetMapping("/fournisseurs_detail/{id}")
    public String detailFournisseur(Model model,@PathVariable(name = "id") Long id, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_FRN_FRN");
        UserPrivilege privilegeBR = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_FRN_BR");
        model.addAttribute("privilege", privilege);
        model.addAttribute("privilegeBR", privilegeBR);

        Fournisseur_Article societe = fournisseurArticleRepository.findSocieteByID(id);
        List<Bon_Reception> receptions = bonReceptionRepository.findAllBon_ReceptionsByFournisseur(false,societe);
        boolean canDelete=false;
        boolean canUpdate=false;
        if(receptions.size() == 0) {
            canDelete=true;
            canUpdate=true;
        }
        societe.setCanDelete(canDelete);
        societe.setCanUpdate(canUpdate);

        model.addAttribute("fournisseur", societe);
        model.addAttribute("receptions", receptions);

        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_FRN");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_FRN_FRN");

        return "equipements/fournisseurs_detail";
    }
    @PostMapping(value = "/fournisseurs_delete")
    public RedirectView fournisseurs_delete(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());

        String deleted = "nok";
        String id = request.getParameter("id");
        Fournisseur_Article societe = fournisseurArticleRepository.findSocieteByID(Long.parseLong(id));
        List<Bon_Reception> contrats = bonReceptionRepository.findAllBon_ReceptionsByFournisseur(false,societe);
        if(contrats.size() == 0){
            societe.setEtatSupp(true);
            fournisseurArticleRepository.save(societe);
            deleted="ok";
            //**********************************************************************
            //**********************************************************************
            History history = new History();
            history.setAction("SUPPRIMER UN FOURNISSEUR : "+societe.getRs());
            history.setDateCreation(new Date());
            history.setModule("ÉQUIPEMENTS");
            history.setUtilisateur(utilisateur);
            historyRepo.save(history);
            //**********************************************************************
            //**********************************************************************
        }


        return new RedirectView("fournisseurs_list?deleted="+deleted);
    }


    @GetMapping("/brs_list")
    public String brs(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_FRN_BR");
        List<Fournisseur_Article> fournisseurs = fournisseurArticleRepository.findAllFournisseurs(false);
        List<Entrepot> entrepots = entrepotRepository.findAllEntrepots(false);
        List<Bon_Reception> bon_receptions = bonReceptionRepository.findAllBon_Receptions(false);
        for(Bon_Reception e :bon_receptions){
            e.setCanDelete(true);
            e.setCanUpdate(true);

        }
        model.addAttribute("entrepots", entrepots);
        model.addAttribute("fournisseurs", fournisseurs);
        model.addAttribute("brs", bon_receptions);
        model.addAttribute("privilege", privilege);


        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("validated") != null)
            model.addAttribute("validated", request.getParameter("validated"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));



        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_FRN");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_FRN_BR");
        
        return "equipements/brs_list";
    }
    @PostMapping(value = "/brs_add")
    public RedirectView addBR(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";
        String updated = "nok";
        String actions ="";

        String date = request.getParameter("date");
        String code = request.getParameter("code");
        String fournisseur_id = request.getParameter("fournisseur_id");
        String entrepot_id = request.getParameter("entrepot_id");
        Fournisseur_Article fournisseur = fournisseurArticleRepository.findSocieteByID(Long.parseLong(fournisseur_id));
        Entrepot entrepot = entrepotRepository.findEntrepotByID(Long.parseLong(entrepot_id));
        Bon_Reception_Status status = bonReceptionStatusRepository.findStatusByID(1);

        Bon_Reception br= new Bon_Reception();
        br.setEtatSupp(false);
        br.setCode(code);
        br.setDate(sdf.parse(date));
        br.setEntrepot(entrepot);
        br.setFournisseur(fournisseur);
        br.setStatus(status);
        bonReceptionRepository.save(br);
        added = "ok";

        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UN BON DE RÉCEPTION : "+br.getCode());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("brs_detail/"+br.getId()+"?added="+added);
    }
    @PostMapping(value = "/brs_edit")
    public RedirectView editBR(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String updated = "nok";

        String id = request.getParameter("id");
        String date = request.getParameter("date");
        String code = request.getParameter("code");
        String fournisseur_id = request.getParameter("fournisseur_id");
        String entrepot_id = request.getParameter("entrepot_id");
        Fournisseur_Article fournisseur = fournisseurArticleRepository.findSocieteByID(Long.parseLong(fournisseur_id));
        Entrepot entrepot = entrepotRepository.findEntrepotByID(Long.parseLong(entrepot_id));

        Bon_Reception br= bonReceptionRepository.findBon_ReceptionByID(Long.parseLong(id));
        br.setCode(code);
        br.setDate(sdf.parse(date));
        br.setEntrepot(entrepot);
        br.setFournisseur(fournisseur);
        bonReceptionRepository.save(br);
        updated = "ok";

        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER LE BON DE RÉCEPTION : "+br.getCode());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("brs_list?updated="+updated);

    }
    @PostMapping(value = "/brs_delete")
    public RedirectView deleteBR(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());

        String deleted = "nok";
        String id = request.getParameter("id");
        Bon_Reception br = bonReceptionRepository.findBon_ReceptionByID(Long.parseLong(id));
        br.setEtatSupp(true);
        bonReceptionRepository.save(br);
        deleted="ok";
        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER UN BON DE RÉCEPTION : "+br.getCode());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************



        return new RedirectView("brs_list?deleted="+deleted);
    }
    @PostMapping(value = "/brs_valider")
    public RedirectView validerBR(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());

        String validated = "nok";
        String id = request.getParameter("id");
        Bon_Reception br = bonReceptionRepository.findBon_ReceptionByID(Long.parseLong(id));
        Bon_Reception_Status status = bonReceptionStatusRepository.findStatusByID(2);
        br.setStatus(status);
        bonReceptionRepository.save(br);
        Entrepot entrepot= br.getEntrepot();
        List<Bon_Reception_Detail> details = bonReceptionDetailRepository.findAllBon_ReceptionsByBR(false,br);
        for(Bon_Reception_Detail d : details){
            Article article= d.getArticle();
            double q = d.getQte();

            // update STOCK_ACTUEL
            Article_Stock stock = articleStockRepository.findStockByEntrepotAndArticle(entrepot, article);
            if (stock == null) {
                stock = new Article_Stock();
                stock.setArticle(article);
                stock.setEntrepot(entrepot);
                stock.setStock_reel(q);
                stock.setStock_theorique(q);
            } else {
                stock.setStock_theorique(stock.getStock_theorique() + q);
                stock.setStock_reel(stock.getStock_reel() + q);
            }

            // Ajout Mouvement Article
            Mouvements_Article ma = new Mouvements_Article();
            ma.setArticle(article);
            ma.setEntrepot(entrepot);
            ma.setDescription("BRF");
            ma.setNumpiece(br.getCode());
            ma.setQuantite(q);
            ma.setDate(br.getDate());
            ma.setPrix_achat(d.getHt());
            mouvementArticleRepository.save(ma);


            double cump = article.getPmp();
            double s = 0;
            double[] ds = stockService.recalculateCUMPAndStock(article, entrepot, cump, s);
            cump =ds[0];
            s =ds[1];
            stock.setPrix_unitaire(cump);
            stock.setStock_reel(s);
            articleStockRepository.save(stock);
        }
        validated="ok";
        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("VALIDER UN BON DE RÉCEPTION : "+br.getCode());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        String action = request.getParameter("action");
        if(action != null && action.equals("detail")) {
            return new RedirectView("brs_detail/"+br.getId()+"?validated="+validated);
        }else{
            return new RedirectView("brs_list?validated="+validated);
        }

    }
    @GetMapping("/brs_detail/{id}")
    public String br_detail(@PathVariable(name = "id") Long id, Model model, @AuthenticationPrincipal User user, HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_FRN_BR");

        Bon_Reception br= bonReceptionRepository.findBon_ReceptionByID(id);
        List<Bon_Reception_Detail> details = bonReceptionDetailRepository.findAllBon_ReceptionsByBR(false,br);
        List<Article> articles = articleRepository.findArticlesNotAffectBR(false,br);
        List<Unite> unites = uniteRepository.findAllUnites(false);

        model.addAttribute("unites", unites);
        model.addAttribute("articles", articles);
        model.addAttribute("details", details);
        model.addAttribute("br", br);
        model.addAttribute("privilege", privilege);


        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("validated") != null)
            model.addAttribute("validated", request.getParameter("validated"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "EQUIPEMENTS_FRN");
        model.addAttribute("soussousMenuActive", "EQUIPEMENTS_FRN_BR");


        return "equipements/brs_detail";
    }
    @PostMapping(value = "/addBRDetail")
    public RedirectView addDetail(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        int tva = Integer.parseInt(request.getParameter("tva"));
        double ht = Double.parseDouble(request.getParameter("ht"));
        double qte = Double.parseDouble(request.getParameter("qte"));
        String br_id = request.getParameter("br_id");
        String article_id = request.getParameter("article_id");
        double total_ht = ht * qte;
        double total_ttc = total_ht + (total_ht*tva /100);

        Bon_Reception br = bonReceptionRepository.findBon_ReceptionByID(Long.parseLong(br_id));
        Article article = null;
        if(article_id !=null && article_id.equals("new")){
            String article_code = request.getParameter("article_code");
            String article_libelle = request.getParameter("article_libelle");
            String article_unite = request.getParameter("article_unite");
            Double price = Double.parseDouble(request.getParameter("article_pmp"));

            Unite unite = uniteRepository.findUniteByID(Long.parseLong(article_unite));
            article= new Article();
            article.setEtatSupp(false);
            article.setCode(article_code);
            article.setLibelle(article_libelle);
            article.setUnite(unite);
            article.setPmp(price);
            article.setHas_compteur("San");
            article = articleRepository.save(article);

        }else{
            if(article_id !=null && !article_id.equals(""))  article = articleRepository.findArticleByID(Long.parseLong(article_id));
        }

        Bon_Reception_Detail d = new Bon_Reception_Detail();
        d.setEtatSupp(false);
        d.setArticle(article);
        d.setQte(qte);
        d.setHt(ht);
        d.setTva(tva);
        d.setTotal_ttc(total_ttc);
        d.setTotal_ht(total_ht);
        d.setBr(br);
        bonReceptionDetailRepository.save(d);

        br.setTotal_ht(br.getTotal_ht()+total_ht);
        br.setTotal_ttc(br.getTotal_ttc()+total_ttc);
        bonReceptionRepository.save(br);

        article.setPmp(ht);
        articleRepository.save(article);
        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER L'ARTICLE "+article.getLibelle()+" AU BR  : "+br.getCode());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("brs_detail/"+br.getId()+"?added="+added);
    }
    @PostMapping(value = "/deleteBRDetail")
    public RedirectView deleteDetail(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String deleted = "nok";

        String id = request.getParameter("id");

        Bon_Reception_Detail d =  bonReceptionDetailRepository.findBon_ReceptionByID(Long.parseLong(id));
        d.setEtatSupp(true);
        bonReceptionDetailRepository.save(d);

        Bon_Reception br =d.getBr();
        br.setTotal_ht(br.getTotal_ht()+d.getTotal_ht());
        br.setTotal_ttc(br.getTotal_ttc()+d.getTotal_ttc());
        bonReceptionRepository.save(br);
        deleted = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER L'ARTICLE "+d.getArticle().getLibelle()+" DU BR  : "+d.getBr().getCode());
        history.setDateCreation(new Date());
        history.setModule("ÉQUIPEMENTS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("brs_detail/"+d.getBr().getId()+"?deleted="+deleted);
    }

    @GetMapping("/image/{equipementid}/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String equipementid, @PathVariable String imageName) throws IOException {
        String uploadDirectory = chemin_pj + "ged/equipement/" + equipementid + "/";
        Path imagePath = Paths.get(uploadDirectory, imageName);

        Resource resource;
        try {
            resource = new UrlResource(imagePath.toUri());
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String contentType;
        try {
            contentType = Files.probeContentType(imagePath);
        } catch (IOException e) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }


}
