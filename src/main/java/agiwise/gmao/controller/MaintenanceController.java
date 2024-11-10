package jway.gmao.controller;


import jway.gmao.dao.*;
import jway.gmao.model.*;
import jway.gmao.service.AdministrationService;
import jway.gmao.service.UtilisateurService;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value = "/maintenances")
public class MaintenanceController {




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
    private OuvrierRepository ouvrierRepository;
    @Autowired
    private UtilisateurRepo utilisateurRepo;
    @Autowired
    private UserPrivilegeRepo userPrivilegeRepo;
    @Autowired
    private BonSortieRepository bonSortieRepository;
    @Autowired
    private TacheRepository tacheRepository;
    @Autowired
    private BonSortieDetailRepository bonSortieDetailRepository;
    @Autowired
    private HistoryRepo historyRepo;
    @Autowired
    private EquipementRepository equipementRepository;
    @Autowired
    private InterventionRepository interventionRepository;
    @Autowired
    private InterventionTypeRepository interventionTypeRepository;
    @Autowired
    private InterventionStatutRepository interventionStatutRepository;
    @Autowired
    private InterventionHistoStatutRepository interventionHistoStatutRepository;
    @Autowired
    private FournisseurServiceRepository fournisseurServiceRepository;
    @Autowired
    private FournisseurCategorieRepository fournisseurCategorieRepository;
    @Autowired
    private InstallationRepository installationRepository;
    @Autowired
    private ContratHistoRepository contratHistoRepository;
    @Autowired
    private ArticleStockRepository articleStockRepository;
    @Autowired
    private InterventionTacheRepository interventionTacheRepository;
    @Autowired
    private ContratMaintenanceStatusRepository contratMaintenanceStatusRepository;
    @Autowired
    private ContratMaintenanceRepository contratMaintenanceRepository;
    @Autowired
    private ContratMaintenanceDetailRepository contratMaintenanceDetailRepository;
    @Autowired
    private MaintenanceTypeRepository maintenanceTypeRepository;
    @Autowired
    private SousTraitanceRepository sousTraitanceRepository;
    @Autowired
    private SousTraitanceStatusRepository sousTraitanceStatusRepository;
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
    private SousTraitanceStatusHistoRepository sousTraitanceStatusHistoRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private ReclamationRepository reclamationRepository;
    @Autowired
    private Reclamation_PJRepository reclamation_pjRepository;
    @Autowired
    private ReclamationStatusRepository reclamationStatusRepository;
    @Autowired
    private ReclamationHistoStatutRepository reclamationHistoStatutRepository;
    @Autowired
    private EmplacementRepository emplacementRepository;
    @Autowired
    private NotificationsRepo notificationsRepo;
    @Autowired
    private UtilisateurService utilisateurService;


    @ModelAttribute
    public void addMenus(HttpSession session, Model model, @AuthenticationPrincipal User user) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE");
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
        model.addAttribute("menuActive", "MAINTENANCE");
    }

    //*************************************Fournisseurs*********************************************************************************
    @GetMapping("/fournisseurs_list")
    public String fournisseurs_list(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_FRN_FRN");
        List<Fournisseur_Categorie> categories = fournisseurCategorieRepository.findAll(false);
        List<Fournisseur_Service> societes = fournisseurServiceRepository.findAllFournisseurs(false);
        for(Fournisseur_Service e :societes){
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
        model.addAttribute("sousMenuActive", "MAINTENANCE_FRN");
        model.addAttribute("soussousMenuActive", "MAINTENANCE_FRN_FRN");

        return "maintenances/fournisseurs_list";
    }
    @GetMapping("/fournisseurs_add")
    public String addFournisseur(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_FRN_FRN");
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
        model.addAttribute("sousMenuActive", "MAINTENANCE_FRN");
        model.addAttribute("soussousMenuActive", "MAINTENANCE_FRN_FRN");

        return "maintenances/fournisseurs_add";
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

        Fournisseur_Service fournisseur = new Fournisseur_Service();
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
        fournisseurServiceRepository.save(fournisseur);

        added="ok";
        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UN FOURNISEUR: "+fournisseur.getRs());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("fournisseurs_detail/"+fournisseur.getId()+"?added="+added);
    }
    @GetMapping("/fournisseurs_edit/{id}")
    public String fournisseurs_edit(Model model,@PathVariable(name = "id") Long id, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_FRN_FRN");
        model.addAttribute("privilege", privilege);

        Fournisseur_Service societe = fournisseurServiceRepository.findSocieteByID(id);

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
        model.addAttribute("sousMenuActive", "MAINTENANCE_FRN");
        model.addAttribute("soussousMenuActive", "MAINTENANCE_FRN_FRN");


        return "maintenances/fournisseurs_edit";
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

        Fournisseur_Service fournisseur = fournisseurServiceRepository.findSocieteByID(Long.parseLong(id));
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
        fournisseurServiceRepository.save(fournisseur);

        added="ok";

        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER LE FOURNISEUR: "+fournisseur.getRs());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("fournisseurs_detail/"+fournisseur.getId()+"?updated="+added);
    }
    @GetMapping("/fournisseurs_detail/{id}")
    public String detailFournisseur(Model model,@PathVariable(name = "id") Long id, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_FRN_FRN");
        model.addAttribute("privilege", privilege);

        Fournisseur_Service societe = fournisseurServiceRepository.findSocieteByID(id);
        List<Contrat_Maintenance> contrats = contratMaintenanceRepository.findAllContrat_MaintenancesByFournisseur(false,societe);
        List<Sous_Traitance> sous_traitances = sousTraitanceRepository.findAllSous_TraitancesByFournisseur(false,societe);
        boolean canDelete=false;
        boolean canUpdate=false;
        if(contrats.size() == 0) {
            canDelete=true;
            canUpdate=true;
        }else if(sous_traitances.size() == 0) {
            canDelete=true;
            canUpdate=true;
        }
        societe.setCanDelete(canDelete);
        societe.setCanUpdate(canUpdate);

        model.addAttribute("fournisseur", societe);
        model.addAttribute("contrats", contrats);
        model.addAttribute("sous_traitances", sous_traitances);

        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_FRN");
        model.addAttribute("soussousMenuActive", "MAINTENANCE_FRN_FRN");

        return "maintenances/fournisseurs_detail";
    }
    @PostMapping(value = "/fournisseurs_delete")
    public RedirectView fournisseurs_delete(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());

        String deleted = "nok";
        String id = request.getParameter("id");
        Fournisseur_Service societe = fournisseurServiceRepository.findSocieteByID(Long.parseLong(id));
        List<Contrat_Maintenance> contrats = contratMaintenanceRepository.findAllContrat_MaintenancesByFournisseur(false,societe);
        if(contrats.size() == 0){
            societe.setEtatSupp(true);
            fournisseurServiceRepository.save(societe);
            deleted="ok";
            //**********************************************************************
            //**********************************************************************
            History history = new History();
            history.setAction("SUPPRIMER UN FOURNISSEUR : "+societe.getRs());
            history.setDateCreation(new Date());
            history.setModule("MAINTENANCES");
            history.setUtilisateur(utilisateur);
            historyRepo.save(history);
            //**********************************************************************
            //**********************************************************************
        }


        return new RedirectView("fournisseurs_list?deleted="+deleted);
    }



    @GetMapping("/sous_traitances_list")
    public String sous_traitances_list(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_FRN_ST");
        List<Sous_Traitance> sous_traitances = sousTraitanceRepository.findAllSous_Traitances(false);

        String str=request.getParameter("fournisseur_id");
        if(str != null && !"".equals(str)){
            Fournisseur_Service societe=fournisseurServiceRepository.findSocieteByID(Long.parseLong(str));
            sous_traitances = sousTraitanceRepository.findAllSous_TraitancesByFournisseur(false,societe);

        }
        for(Sous_Traitance st : sous_traitances){
            boolean canDelete=false;
            boolean canUpdate=false;
            st.setCanDelete(canDelete);
            st.setCanUpdate(canUpdate);
        }
        model.addAttribute("sous_traitances", sous_traitances);
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
        model.addAttribute("sousMenuActive", "MAINTENANCE_FRN");
        model.addAttribute("soussousMenuActive", "MAINTENANCE_FRN_ST");


        return "maintenances/sous_traitances_list";
    }
    @GetMapping("/sous_traitances_add")
    public String sous_traitances_add(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_FRN_ST");
        model.addAttribute("privilege", privilege);
        model.addAttribute("action", "/fournisseurs/soutraitance/addST");

        List<Intervention> interventions = interventionRepository.findAllInterventionsEncours(false);
        Fournisseur_Categorie categorie = fournisseurCategorieRepository.findByID(4);
        List<Fournisseur_Service> fournisseurs = fournisseurServiceRepository.findAllSoustraitant(false,categorie);

        String str =request.getParameter("intervention_id");
        if(str != null && !"".equals(str)) {
            Intervention intervention = interventionRepository.findInterventionByID(Long.parseLong(str));
            model.addAttribute("intervention", intervention);
        }

        model.addAttribute("fournisseurs", fournisseurs);
        model.addAttribute("interventions", interventions);

        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_FRN");
        model.addAttribute("soussousMenuActive", "MAINTENANCE_FRN_ST");


        return "maintenances/sous_traitances_add";
    }
    @PostMapping(value = "/sous_traitances_add")
    public RedirectView sous_traitances_add(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String montant = request.getParameter("montant");
        String date = request.getParameter("date");
        String observation = request.getParameter("observation");
        String code = request.getParameter("code");
        String fournisseur_id = request.getParameter("fournisseur_id");
        String intervention_id = request.getParameter("intervention_id");

        Fournisseur_Service fournisseur = null;
        if(fournisseur_id !=null && fournisseur_id.equals("new")){
            Fournisseur_Categorie categorie = fournisseurCategorieRepository.findByID(4);
            String libelle_st= request.getParameter("libelle_st");
            Collection<Fournisseur_Categorie> categories = new ArrayList<>();
            categories.add(categorie);
            fournisseur= new Fournisseur_Service();
            fournisseur.setEtatSupp(false);
            fournisseur.setRs(libelle_st);
            fournisseur.setFournisseur(1);
            fournisseur.setCategories(categories);
            fournisseurServiceRepository.save(fournisseur);
        }
        else{
            if(fournisseur_id !=null && !fournisseur_id.equals("")) fournisseur = fournisseurServiceRepository.findSocieteByID(Long.parseLong(fournisseur_id));
        }


        Intervention intervention = interventionRepository.findInterventionByID(Long.parseLong(intervention_id));
        Sous_Traitance_Status status = sousTraitanceStatusRepository.findStatusByID(1);

        Sous_Traitance st = new Sous_Traitance();
        st.setCode(code);
        st.setObservation(observation);
        st.setDate(sdf.parse(date));
        st.setEtatSupp(false);
        st.setIntervention(intervention);
        st.setSous_traitant(fournisseur);
        st.setStatus(status);
        st.setMontant(Double.parseDouble(montant));
        sousTraitanceRepository.save(st);


        if(status != null ){
            sousTraitanceStatusHistoRepository.updateOldStatusByST(st);
            SousTraitance_Historique_Statut histo = new SousTraitance_Historique_Statut();
            histo.setSous_traitance(st);
            histo.setStatus(status);
            histo.setActual(true);
            histo.setDate(new Date());
            sousTraitanceStatusHistoRepository.save(histo);
        }

        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UNE NOUVELLE SOUS-TRAITANCE "+st.getCode()+" á L'INTERVENTION : "+intervention.getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("sous_traitances_list?added="+added);
    }
    @GetMapping("/sous_traitances_edit/{id}")
    public String sous_traitances_edit(@PathVariable(name = "id") int id, Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_FRN_ST");
        model.addAttribute("privilege", privilege);
        model.addAttribute("action", "/fournisseurs/soutraitance/editST");

        Sous_Traitance st = sousTraitanceRepository.findSous_TraitanceByID(id);
        List<Intervention> interventions = interventionRepository.findAllInterventionsEncours(false);

        Fournisseur_Categorie categorie = fournisseurCategorieRepository.findByID(4);
        List<Fournisseur_Service> fournisseurs = fournisseurServiceRepository.findAllSoustraitant(false,categorie);

        model.addAttribute("st", st);
        model.addAttribute("fournisseurs", fournisseurs);
        model.addAttribute("interventions", interventions);

        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_FRN");
        model.addAttribute("soussousMenuActive", "MAINTENANCE_FRN_ST");


        return "maintenances/sous_traitances_edit";
    }
    @PostMapping(value = "/sous_traitances_edit")
    public RedirectView sous_traitances_edit(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");
        String montant = request.getParameter("montant");
        String date = request.getParameter("date");
        String observation = request.getParameter("observation");
        String code = request.getParameter("code");
        String fournisseur_id = request.getParameter("fournisseur_id");
        String intervention_id = request.getParameter("intervention_id");
        Fournisseur_Service fournisseur = fournisseurServiceRepository.findSocieteByID(Long.parseLong(fournisseur_id));
        Intervention intervention = interventionRepository.findInterventionByID(Long.parseLong(intervention_id));
        Sous_Traitance_Status status = sousTraitanceStatusRepository.findStatusByID(1);

        Sous_Traitance st = sousTraitanceRepository.findSous_TraitanceByID(Long.parseLong(id));
        st.setCode(code);
        st.setObservation(observation);
        st.setDate(sdf.parse(date));
        st.setIntervention(intervention);
        st.setSous_traitant(fournisseur);
        st.setMontant(Double.parseDouble(montant));

        sousTraitanceRepository.save(st);


        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER LS SOUS-TRAITANCE : "+st.getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("sous_traitances_list?updated="+added);
    }
    @PostMapping(value = "/sous_traitances_editMT")
    public RedirectView updateMT(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");
        String montant = request.getParameter("montant");

        Sous_Traitance st = sousTraitanceRepository.findSous_TraitanceByID(Long.parseLong(id));
        st.setMontant(Double.parseDouble(montant));
        sousTraitanceRepository.save(st);


        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER LE MONTANT DE LA SOUS-TRAITANCE : "+st.getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("sous_traitances_list?updated="+added);
    }
    @PostMapping(value = "/sous_traitances_valider")
    public RedirectView validerST(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");
        Sous_Traitance_Status status = sousTraitanceStatusRepository.findStatusByID(2);

        Sous_Traitance st = sousTraitanceRepository.findSous_TraitanceByID(Long.parseLong(id));
        st.setStatus(status);
        sousTraitanceRepository.save(st);

        if(status != null ){
            sousTraitanceStatusHistoRepository.updateOldStatusByST(st);
            SousTraitance_Historique_Statut histo = new SousTraitance_Historique_Statut();
            histo.setSous_traitance(st);
            histo.setStatus(status);
            histo.setActual(true);
            histo.setDate(new Date());
            sousTraitanceStatusHistoRepository.save(histo);
        }

        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("VALIDER LA SOUS-TRAITANCE : "+st.getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("sous_traitances_list?validated="+added);
    }
    @PostMapping(value = "/sous_traitances_delete")
    public RedirectView sous_traitances_delete(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");
        Sous_Traitance_Status status = sousTraitanceStatusRepository.findStatusByID(2);

        Sous_Traitance st = sousTraitanceRepository.findSous_TraitanceByID(Long.parseLong(id));
        st.setEtatSupp(true);
        sousTraitanceRepository.save(st);


        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER LA SOUS-TRAITANCE : "+st.getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("sous_traitances_list?deleted="+added);
    }
    @GetMapping("/sous_traitances_status/{id}")
    public String sous_traitances_status(@PathVariable(name = "id") Long id,Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_FRN_ST");
        Sous_Traitance sous_traitance = sousTraitanceRepository.findSous_TraitanceByID(id);
        List<SousTraitance_Historique_Statut> sous_traitances_status = sousTraitanceStatusHistoRepository.findStatusByST(sous_traitance);

        model.addAttribute("sous_traitances_status", sous_traitances_status);
        model.addAttribute("sous_traitance", sous_traitance);
        model.addAttribute("privilege", privilege);

        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_FRN");
        model.addAttribute("soussousMenuActive", "MAINTENANCE_FRN_ST");

        return "maintenances/sous_traitances_status";
    }





    //*************************************Contrats demaintenances*********************************************************************************
    @GetMapping("/contrats_list")
    public String contrats_list(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_CONTRAT");
        List<Fournisseur_Service> fournisseurs = fournisseurServiceRepository.findAllFournisseurs(false);
        List<Contrat_Maintenance> contrats = contratMaintenanceRepository.findAllContrat_Maintenances(false);
        for(Contrat_Maintenance e :contrats){
            boolean canDelete=true;
            boolean canUpdate=true;

            e.setCanDelete(canDelete);
            e.setCanUpdate(canUpdate);
        }
        model.addAttribute("contrats", contrats);
        model.addAttribute("fournisseurs", fournisseurs);
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
        model.addAttribute("sousMenuActive", "MAINTENANCE_CONTRAT");

        return "maintenances/contrats_list";
    }
    @GetMapping("/contrats_add")
    public String contrats_add(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_CONTRAT");
        model.addAttribute("privilege", privilege);

        List<Fournisseur_Service> fournisseurs = fournisseurServiceRepository.findAllFournisseurs(false);
        List<String> refs = contratMaintenanceRepository.findRefs();

        model.addAttribute("fournisseurs", fournisseurs);
        model.addAttribute("refs", refs);

        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_CONTRAT");

        return "maintenances/contrats_add";
    }
    @PostMapping(value = "/contrats_add")
    public RedirectView contrats_add(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String code = request.getParameter("code");
        String date_debut = request.getParameter("date_debut");
        String date_fin = request.getParameter("date_fin");
        String description = request.getParameter("description");
       

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


        Contrat_Maintenance_Statut status = contratMaintenanceStatusRepository.findStatusByID(1);
        Contrat_Maintenance equipement = new Contrat_Maintenance();
        equipement.setCode(code);
        equipement.setObservation(description);
        equipement.setEtatSupp(false);
        equipement.setStatus(status);
        equipement.setFournisseur(fournisseur);
        equipement = contratMaintenanceRepository.save(equipement);
       
        if(date_debut != null && !date_debut.equals("")) equipement.setDate_debut(sdf.parse(date_debut));
        if(date_fin != null && !date_fin.equals("")) equipement.setDate_fin(sdf.parse(date_fin));
        

        if(status != null ){
            contratHistoRepository.updateOldStatusByContrat(equipement);
            Contrat_Historique_Statut histo = new Contrat_Historique_Statut();
            histo.setContrat(equipement);
            histo.setStatus(status);
            histo.setActual(true);
            histo.setDate(new Date());
            contratHistoRepository.save(histo);
        }


        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UN NOUVEAU CONTRAT DE MAINTENANCE : "+equipement.getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("contrats_detail/"+equipement.getId()+"?added="+added);
    }
    @GetMapping("/contrats_edit/{id}")
    public String contrats_edit(@PathVariable(name = "id") Long id,Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_CONTRAT");
        model.addAttribute("privilege", privilege);

        Contrat_Maintenance contrat = contratMaintenanceRepository.findContrat_MaintenanceByID(id);
        List<Fournisseur_Service> fournisseurs = fournisseurServiceRepository.findAllFournisseurs(false);
        List<String> refs = contratMaintenanceRepository.findRefs();

        model.addAttribute("contrat", contrat);
        model.addAttribute("fournisseurs", fournisseurs);
        model.addAttribute("refs", refs);

        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_CONTRAT");

        return "maintenances/contrats_edit";
    }
    @PostMapping(value = "/contrats_edit")
    public RedirectView contrats_edit(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("contrat_id");
        Contrat_Maintenance equipement = contratMaintenanceRepository.findContrat_MaintenanceByID(Long.parseLong(id));

        String code = request.getParameter("code");
        String date_debut = request.getParameter("date_debut");
        String date_fin = request.getParameter("date_fin");
        String description = request.getParameter("description");


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


        equipement.setCode(code);
        equipement.setObservation(description);
        equipement.setFournisseur(fournisseur);
        equipement = contratMaintenanceRepository.save(equipement);

        if(date_debut != null && !date_debut.equals("")) equipement.setDate_debut(sdf.parse(date_debut));
        if(date_fin != null && !date_fin.equals("")) equipement.setDate_fin(sdf.parse(date_fin));


        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER LE CONTRAT DE MAINTENANCE : "+equipement.getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("contrats_detail/"+equipement.getId()+"?updated="+added);
    }
    @PostMapping(value = "/contrats_delete")
    public RedirectView contrats_delete(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";
        long id = Long.parseLong(request.getParameter("id"));
        Contrat_Maintenance contrat = contratMaintenanceRepository.findContrat_MaintenanceByID(id);
        contrat.setEtatSupp(true);
        contratMaintenanceRepository.save(contrat);
        List<Contrat_Maintenance_Detail> details = contratMaintenanceDetailRepository.findAllContrat_MaintenancesByContrat(false,contrat);

        for(Contrat_Maintenance_Detail d : details){
            if(d.getType() != null && d.getType().getId() == 1){
                Intervention intervention = interventionRepository.findInterventionByContratDetailID(id);
                intervention.setEtatSupp(true);
                interventionRepository.save(intervention);
            }
        }

        added="ok";
        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER LE CONTRAT DE MAINTENANCE: "+contrat.getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("contrats_list?deleted="+added);
    }
    @PostMapping(value = "/contrats_valider")
    public RedirectView contrats_valider(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";
        long id = Long.parseLong(request.getParameter("id"));
        Contrat_Maintenance contrat = contratMaintenanceRepository.findContrat_MaintenanceByID(id);

        Contrat_Maintenance_Statut status = contratMaintenanceStatusRepository.findStatusByID(2);
        contrat.setStatus(status);
        contratMaintenanceRepository.save(contrat);

        if(status != null ){
            contratHistoRepository.updateOldStatusByContrat(contrat);
            Contrat_Historique_Statut histo = new Contrat_Historique_Statut();
            histo.setContrat(contrat);
            histo.setStatus(status);
            histo.setActual(true);
            histo.setDate(new Date());
            contratHistoRepository.save(histo);
        }


        added="ok";
        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("VALIDER LE CONTRAT DE MAINTENANCE: "+contrat.getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("contrats_list?validated="+added);
    }
    @GetMapping("/contrats_detail/{id}")
    public String contrats_detail(Model model,@PathVariable(name = "id") Long id, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_CONTRAT");
        model.addAttribute("privilege", privilege);

        Contrat_Maintenance contrat = contratMaintenanceRepository.findContrat_MaintenanceByID(id);
        //List<Equipement> equipements = equipementRepository.findAllEquipementsNotContrat(false,contrat);
        List<Equipement> equipements = equipementRepository.findAllEquipements(false);
        List<Contrat_Maintenance_Detail> details = contratMaintenanceDetailRepository.findAllContrat_MaintenancesByContrat(false,contrat);
        List<Maintenance_Type> types = maintenanceTypeRepository.findAllTypes(false);
        List<Intervention_Type> intervention_types = interventionTypeRepository.findAllTypes(false);
        boolean canDelete=false;
        boolean canUpdate=false;
        if(details.size() == 0){
            canDelete=true;
            canUpdate=true;
        }
        contrat.setCanDelete(canDelete);
        contrat.setCanUpdate(canUpdate);

        for(Contrat_Maintenance_Detail d :details){
            canDelete=false;
            canUpdate=false;
            if(d.getType() != null && d.getType().getId() == 1){
                Intervention intervention = interventionRepository.findInterventionByContratDetailID(id);
                if(intervention == null || (intervention != null && intervention.getStatus().getId() == 1)) {
                    canDelete=true;
                    canUpdate=true;
                }
                if(intervention!=null){
                   d.setIntervention_id(intervention.getId());
                }
            }
            d.setCanDelete(canDelete);
            d.setCanUpdate(canUpdate);

        }
        model.addAttribute("intervention_types", intervention_types);
        model.addAttribute("types", types);
        model.addAttribute("contrat", contrat);
        model.addAttribute("equipements", equipements);
        model.addAttribute("details", details);

        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_CONTRAT");

        return "maintenances/contrats_detail";
    }
    @PostMapping(value = "/contrats_detail_add")
    public RedirectView contrats_detail_add(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";
        Contrat_Maintenance contrat = contratMaintenanceRepository.findContrat_MaintenanceByID(Long.parseLong(request.getParameter("contrat_id")));

        String equipement_id = request.getParameter("equipement_id");
        Equipement equipement = null ;
        if(equipement_id !=null && !equipement_id.equals("")) equipement = equipementRepository.findEquipementByID(Integer.parseInt(equipement_id));

        String type_id = request.getParameter("type_id");
        Maintenance_Type type = null ;
        if(type_id !=null && !type_id.equals("")) type = maintenanceTypeRepository.findTypeByID(Integer.parseInt(type_id));


       Contrat_Maintenance_Detail detail = new Contrat_Maintenance_Detail();
       detail.setContrat(contrat);
       detail.setEtatSupp(false);
       detail.setEquipement(equipement);
       detail.setType(type);
       contratMaintenanceDetailRepository.save(detail);

       if(type != null && type.getId()==1){
           String intervention_type_id = request.getParameter("intervention_type");
           String intervention_code = request.getParameter("intervention_code");
           String intervention_date = request.getParameter("intervention_date").replace("T"," ");

           Intervention_Type intervention_type = interventionTypeRepository.findIntervention_TypeByID(Long.parseLong(intervention_type_id));
           Intervention_Status intervention_statut = interventionStatutRepository.findIntervention_StatusByID(1);
           Intervention intervention = new Intervention();
           intervention.setCode(intervention_code);
           intervention.setEquipement(equipement);
           intervention.setEtatSupp(false);
           intervention.setDate_debut(sdf3.parse(intervention_date));
           intervention.setStatus(intervention_statut);
           intervention.setType(intervention_type);
           intervention.setContrat_detail_id(detail.getId());
           interventionRepository.save(intervention);
           intervention.setFournisseur(contrat.getFournisseur());
           intervention.setResponsable(utilisateur);
           intervention=interventionRepository.save(intervention);

           added = "ok";


           if(intervention_statut != null ){
               interventionHistoStatutRepository.updateOldStatusByIntervention(intervention);
               Intervention_Historique_Statut histo = new Intervention_Historique_Statut();
               histo.setIntervention(intervention);
               histo.setStatus(intervention_statut);
               histo.setActual(true);
               histo.setDate(new Date());
               interventionHistoStatutRepository.save(histo);
           }


       }

        added="ok";
        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UN éQUIPEMENT AU CONTRAT DE MAINTENANCE: "+contrat.getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("contrats_detail/"+contrat.getId()+"?added="+added);
    }
    @PostMapping(value = "/contrats_detail_delete")
    public RedirectView contrats_detail_delete(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";
        long id = Long.parseLong(request.getParameter("id"));
        Contrat_Maintenance_Detail d = contratMaintenanceDetailRepository.findContrat_MaintenanceByID(id);

        if(d.getType() != null && d.getType().getId() == 1){
            Intervention intervention = interventionRepository.findInterventionByContratDetailID(id);
            intervention.setEtatSupp(true);
            interventionRepository.save(intervention);
        }
        d.setEtatSupp(true);
        contratMaintenanceDetailRepository.save(d);

        added="ok";
        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER L'éQUIPEMENT : "+d.getEquipement().getLibelle()+" DU CONTRAT DE MAINTENANCE: "+d.getContrat().getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("contrats_detail/"+d.getContrat().getId()+"?deleted="+added);
    }
    @GetMapping("/contrats_status/{id}")
    public String contrats_status(@PathVariable(name = "id") Long id,Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_CONTRAT");
        Contrat_Maintenance contrat = contratMaintenanceRepository.findContrat_MaintenanceByID(id);
        List<Contrat_Historique_Statut> contrats_status = contratHistoRepository.findAllByContrat(contrat);

        model.addAttribute("contrats_status", contrats_status);
        model.addAttribute("contrat", contrat);
        model.addAttribute("privilege", privilege);

        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_CONTRAT");

        return "maintenances/contrats_status";
    }





    //*************************************interventions*********************************************************************************
    @GetMapping("/interventions_list")
    public String interventions(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) throws ParseException {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_INTERVENTION");
        List<Intervention> interventions = interventionRepository.findAllInterventions(false);

        String str = request.getParameter("date");
        if(str != null && !"".equals(str)){
            Date date = sdf2.parse(str);
            interventions = interventionRepository.findAllInterventions(false,date);

        }
        model.addAttribute("interventions", interventions);
        model.addAttribute("privilege", privilege);


        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("started") != null)
            model.addAttribute("started", request.getParameter("started"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_INTERVENTION");


        return "maintenances/interventions_list";
    }
    @GetMapping("/interventions_last")
    public String interventions_last(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) throws ParseException {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_INTERVENTION");
        List<Intervention> interventions = interventionRepository.findLastInterventions(false,new Date(),currentUser);

        String str = request.getParameter("date");
        if(str != null && !"".equals(str)){
            Date date = sdf2.parse(str);
            interventions = interventionRepository.findAllInterventions(false,date);

        }
        model.addAttribute("interventions", interventions);
        model.addAttribute("privilege", privilege);
        model.addAttribute("last", true);


        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("started") != null)
            model.addAttribute("started", request.getParameter("started"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_INTERVENTION");


        return "maintenances/interventions_list";
    }
    @GetMapping("/interventions_bientot")
    public String interventions_bientot(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) throws ParseException {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_INTERVENTION");
        List<Intervention> interventions = interventionRepository.findBientotInterventions(false,new Date(),currentUser.getId());

        String str = request.getParameter("date");
        if(str != null && !"".equals(str)){
            Date date = sdf2.parse(str);
            interventions = interventionRepository.findAllInterventions(false,date);

        }
        model.addAttribute("interventions", interventions);
        model.addAttribute("privilege", privilege);
        model.addAttribute("bientot", true);


        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("started") != null)
            model.addAttribute("started", request.getParameter("started"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_INTERVENTION");


        return "maintenances/interventions_list";
    }
    @GetMapping("/interventions_add")
    public String interventions_add(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_INTERVENTION");
        model.addAttribute("privilege", privilege);
        model.addAttribute("action", "/maintenance/addIntervention");

        String str = request.getParameter("equipement_id");
        if(str != null && !"".equals(str)){
            Equipement equipement = equipementRepository.findEquipementByID(Long.parseLong(str));
            model.addAttribute("equipement", equipement);

            str = request.getParameter("reclamation_id");
            if(str != null && !"".equals(str)){
                Reclamation reclamation = reclamationRepository.findArticleByID(Long.parseLong(str));
                model.addAttribute("reclamation", reclamation);
            }

        }
        List<Intervention_Type> types = interventionTypeRepository.findAllTypes(false);
        List<Equipement> equipements = equipementRepository.findAllEquipements(false);
        List<Installation> installations = installationRepository.findAllInstallations(false);
        List<Utilisateur> utilisateurs = utilisateurRepo.findByEtat_supp(false);

        model.addAttribute("utilisateurs", utilisateurs);
        model.addAttribute("types", types);
        model.addAttribute("installations", installations);
        model.addAttribute("equipements", equipements);

        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_INTERVENTION");


        return "maintenances/interventions_add";
    }
    @PostMapping(value = "/interventions_add")
    public RedirectView interventions_add(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String code = request.getParameter("code");
        String observation = request.getParameter("observation");
        String date = request.getParameter("date");
        String heure = request.getParameter("heure");
        String reclamation_id = request.getParameter("reclamation_id");
        String equipement_id = request.getParameter("equipement_id");
        String fournisseur_id = request.getParameter("fournisseur_id");
        String employe_id = request.getParameter("employe_id");
        String type_id = request.getParameter("type_id");
        Equipement equipement = equipementRepository.findEquipementByID(Long.parseLong(equipement_id));
        Intervention_Type type = null;
        if(type_id !=null && type_id.equals("new")){
            String libelle_type = request.getParameter("libelle_type");
            type= new Intervention_Type();
            type.setEtatSupp(false);
            type.setLibelle(libelle_type);
            interventionTypeRepository.save(type);
        }
        else{
            if(type_id !=null && !type_id.equals("")) type = interventionTypeRepository.findIntervention_TypeByID(Long.parseLong(type_id));
        }
        Fournisseur_Service fournisseur = null;
        if(fournisseur_id != null && !"".equals(fournisseur_id)){
            fournisseur=fournisseurServiceRepository.findSocieteByID(Long.parseLong(fournisseur_id));
        }
        //Installation installation = installationRepository.findInstallationByID(Long.parseLong(installation_id));
        Intervention_Status status = interventionStatutRepository.findIntervention_StatusByID(1);

        Utilisateur employe =  null;
        if(employe_id !=null && !employe_id.equals("")){
            if(employe_id !=null && !employe_id.equals("")) employe = utilisateurRepo.findById(Long.parseLong(employe_id));
        }

        Intervention intervention = new Intervention();
        intervention.setDate_debut(sdf3.parse(date+" "+heure));
        intervention.setCode(code);
        intervention.setObservation(observation);
        intervention.setEquipement(equipement);
        intervention.setFournisseur(fournisseur);
        intervention.setResponsable(employe);
        //intervention.setInstallation(installation);
        intervention.setStatus(status);
        intervention.setType(type);
        intervention=interventionRepository.save(intervention);

        added = "ok";


        if(status != null ){
            interventionHistoStatutRepository.updateOldStatusByIntervention(intervention);
            Intervention_Historique_Statut histo = new Intervention_Historique_Statut();
            histo.setIntervention(intervention);
            histo.setStatus(status);
            histo.setActual(true);
            histo.setDate(new Date());
            interventionHistoStatutRepository.save(histo);
        }

        if(reclamation_id != null && !"".equals(reclamation_id)){
            Reclamation reclamation = reclamationRepository.findArticleByID(Long.parseLong(reclamation_id));
            Reclamation_Statut reclamation_status = reclamationStatusRepository.findStatusByID(2);

            reclamation.setStatus(reclamation_status);
            reclamationRepository.save(reclamation);

            intervention.setReclamation(reclamation);
            interventionRepository.save(intervention);

            if(status != null ){
                reclamationHistoStatutRepository.updateOldStatusByReclamation(reclamation);
                Reclamation_Historique_Statut histo = new Reclamation_Historique_Statut();
                histo.setReclamation(reclamation);
                histo.setStatus(reclamation_status);
                histo.setActual(true);
                histo.setDate(new Date());
                histo.setDescription("Démarrer l'intervention N° : "+intervention.getCode());
                reclamationHistoStatutRepository.save(histo);
            }


            if(reclamation.getResponsable() != null){
                Notification notification = new Notification();
                notification.setModule("RECLAMATION");
                notification.setTitre("Le traitement d'une réclamation est commencé");
                notification.setObj_id(reclamation.getId());
                notification.setUrl("/maintenances/reclamations_list?reclamation_id="+reclamation_id);
                notificationsRepo.save(notification);

                Utilisateur responsable = reclamation.getResponsable();
                responsable.getNotifications().add(notification);
                utilisateurRepo.save(responsable);
            }
        }
        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UNE NOUVELLE INTERVENTION  : "+intervention.getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("interventions_detail/"+intervention.getId()+"?added="+added);
    }
    @GetMapping(value = "/interventions_edit/{id}")
    public String interventions_edit(@PathVariable(name = "id") Long id,HttpServletRequest request,  @AuthenticationPrincipal User user, Model model) throws ParseException {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_INTERVENTION");
        model.addAttribute("privilege", privilege);
        model.addAttribute("action", "/maintenance/updateIntervention");


        Intervention intervention = interventionRepository.findInterventionByID(id);
        List<Intervention_Type> types = interventionTypeRepository.findAllTypes(false);
        List<Equipement> equipements = equipementRepository.findAllEquipements(false);
        List<Installation> installations = installationRepository.findAllInstallations(false);
        List<Utilisateur> utilisateurs = utilisateurRepo.findByEtat_supp(false);

        model.addAttribute("utilisateurs", utilisateurs);
        model.addAttribute("intervention", intervention);
        model.addAttribute("types", types);
        model.addAttribute("installations", installations);
        model.addAttribute("equipements", equipements);

        model.addAttribute("sousMenuActive", "MAINTENANCE_INTERVENTION");


        return "maintenances/interventions_edit";
    }
    @PostMapping(value = "/interventions_edit")
    public RedirectView interventions_edit(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");
        String code = request.getParameter("code");
        String observation = request.getParameter("observation");
        String date = request.getParameter("date");
        String heure = request.getParameter("heure");
        String installation_id = request.getParameter("installation_id");
        String fournisseur_id = request.getParameter("fournisseur_id");
        String employe_id = request.getParameter("employe_id");
        String equipement_id = request.getParameter("equipement_id");
        String type_id = request.getParameter("type_id");
        Equipement equipement = equipementRepository.findEquipementByID(Long.parseLong(equipement_id));
        Intervention_Type type = interventionTypeRepository.findIntervention_TypeByID(Long.parseLong(type_id));
        //Installation installation = installationRepository.findInstallationByID(Long.parseLong(installation_id));
        Intervention_Status status = interventionStatutRepository.findIntervention_StatusByID(1);
        Fournisseur_Service fournisseur = null;
        if(fournisseur_id != null && !"".equals(fournisseur_id)){
            fournisseur=fournisseurServiceRepository.findSocieteByID(Long.parseLong(fournisseur_id));
        }

        Utilisateur employe =  null;
        if(employe_id !=null && !employe_id.equals("")){
            if(employe_id !=null && !employe_id.equals("")) employe = utilisateurRepo.findById(Long.parseLong(employe_id));
        }


        Intervention intervention = interventionRepository.findInterventionByID(Long.parseLong(id));
        intervention.setDate_debut(sdf3.parse(date+" "+heure));
        intervention.setCode(code);
        intervention.setObservation(observation);
        intervention.setEquipement(equipement);
        intervention.setFournisseur(fournisseur);
        intervention.setResponsable(employe);
        //intervention.setInstallation(installation);
        intervention.setType(type);
        interventionRepository.save(intervention);

        added = "ok";



        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFICATION DE L'INTERVENTION  : "+intervention.getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("interventions_list?updated="+added);
    }
    @PostMapping(value = "/interventions_delete")
    public RedirectView deleteIntervention(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String deleted = "nok";

        String id = request.getParameter("id");

        Intervention intervention =  interventionRepository.findInterventionByID(Long.parseLong(id));
        if(intervention.getStatus().getId() == 1){
            intervention.setEtatSupp(true);
            interventionRepository.save(intervention);
            deleted = "ok";

        }


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER L'INTERVENTION :" + intervention.getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("interventions_list?deleted="+deleted);
    }
    @PostMapping(value = "/interventions_demarrer")
    public RedirectView demarrerIntervention(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String deleted = "nok";

        String id = request.getParameter("id");

        Intervention intervention =  interventionRepository.findInterventionByID(Long.parseLong(id));
        if(intervention.getStatus().getId() == 1){
            Intervention_Status status = interventionStatutRepository.findIntervention_StatusByID(2);
            intervention.setStatus(status);
            interventionRepository.save(intervention);
            deleted = "ok";

            if(status != null ){
                interventionHistoStatutRepository.updateOldStatusByIntervention(intervention);
                Intervention_Historique_Statut histo = new Intervention_Historique_Statut();
                histo.setIntervention(intervention);
                histo.setStatus(status);
                histo.setActual(true);
                histo.setDate(new Date());
                interventionHistoStatutRepository.save(histo);
            }
        }


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("DÉMARRER L'INTERVENTION :" + intervention.getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("interventions_detail/"+intervention.getId()+"?started="+deleted);
    }
    @PostMapping(value = "/interventions_terminer")
    public RedirectView terminerIntervention(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String deleted = "nok";

        String action = request.getParameter("action");
        String id = request.getParameter("id");
        String date = request.getParameter("date");

        Intervention intervention =  interventionRepository.findInterventionByID(Long.parseLong(id));
        if(intervention.getStatus().getId() == 2){
            Intervention_Status status = interventionStatutRepository.findIntervention_StatusByID(3);
            intervention.setStatus(status);
            intervention.setDate_fin(sdf.parse(date));
            interventionRepository.save(intervention);

            List<Intervention_Tache> taches = interventionTacheRepository.findIntervention_TacheByIntervention(false,intervention);
            for(Intervention_Tache tache : taches){
                Ouvrier ouvrier = tache.getOuvrier();
                ouvrier.setDisponible(true);
                ouvrierRepository.save(ouvrier);
            }
            deleted = "ok";

            if(status != null ){
                interventionHistoStatutRepository.updateOldStatusByIntervention(intervention);
                Intervention_Historique_Statut histo = new Intervention_Historique_Statut();
                histo.setIntervention(intervention);
                histo.setStatus(status);
                histo.setActual(true);
                histo.setDate(new Date());
                interventionHistoStatutRepository.save(histo);
            }
        }


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("TERMINER L'INTERVENTION :" + intervention.getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        if(action != null && action.equals("detail")) {
            return new RedirectView("interventions_detail/"+intervention.getId()+"?finished="+deleted);
        }else{
            return new RedirectView("interventions_list?finished="+deleted);
        }
    }
    @GetMapping("/interventions_detail/{id}")
    public String interventions_details(@PathVariable(name = "id") Long id, Model model, @AuthenticationPrincipal User user, HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_INTERVENTION");
        UserPrivilege privilegeST = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_FRN_ST");
        UserPrivilege privilegeBS = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "EQUIPEMENTS_STOCK_BS");

        Intervention intervention = interventionRepository.findInterventionByID(id);
        List<Tache> liste_taches = tacheRepository.findAllTachesNotYet(false,intervention);
        List<Ouvrier> ouvriers = ouvrierRepository.findOuvriers(false);

        double total = 0;
        double total_mod = 0;
        List<Intervention_Tache> taches = interventionTacheRepository.findIntervention_TacheByIntervention(false,intervention);
        for(Intervention_Tache i : taches){
            total_mod = total_mod + (i.getOuvrier().getThm()*i.getDuree());
        }

        double total_pieces_rechanges = 0;
        List<Bon_Sortie> bon_sorties = bonSortieRepository.findAllBon_SortiesByIntervention(false,intervention);
        List<Bon_Sortie_Detail> list = new ArrayList<>();
        for(Bon_Sortie bs : bon_sorties){
            List<Bon_Sortie_Detail> details = bonSortieDetailRepository.findAllBon_SortiesByBs(false,bs);
            for(Bon_Sortie_Detail d : details){
                Article_Stock article_stock = articleStockRepository.findStockByEntrepotAndArticle(bs.getEntrepot(),d.getArticle());
                double cump =d.getArticle().getPmp();
                if(article_stock != null){
                    cump=article_stock.getPrix_unitaire();
                }
                d.setCump(cump);
                total_pieces_rechanges = total_pieces_rechanges + cump*d.getQte();
                list.add(d);

            }
        }

        double total_st = 0;
        List<Sous_Traitance> soutraitances = sousTraitanceRepository.findAllSous_TraitancesByIntervention(false,intervention);
        for(Sous_Traitance i : soutraitances){
            total_st = total_st + (i.getMontant());
        }


        model.addAttribute("total", total_pieces_rechanges+total_mod+total_st);
        model.addAttribute("taches", taches);
        model.addAttribute("intervention", intervention);
        model.addAttribute("list", list);
        model.addAttribute("soutraitances", soutraitances);
        model.addAttribute("total_pieces_rechanges", total_pieces_rechanges);
        model.addAttribute("total_mod", total_mod);
        model.addAttribute("total_st", total_st);
        model.addAttribute("privilege", privilege);
        model.addAttribute("privilegeST", privilegeST);
        model.addAttribute("privilegeBS", privilegeBS);
        model.addAttribute("ouvriers", ouvriers);
        model.addAttribute("liste_taches", liste_taches);

        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("started") != null)
            model.addAttribute("started", request.getParameter("started"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));



        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_INTERVENTION");


        return "maintenances/interventions_detail";
    }
    @GetMapping("/interventions_status/{id}")
    public String interventions_status(@PathVariable(name = "id") Long id,Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_INTERVENTION");
        Intervention intervention = interventionRepository.findInterventionByID(id);
        List<Intervention_Historique_Statut> interventions_status = interventionHistoStatutRepository.findAllByIntervention(intervention);

        model.addAttribute("interventions_status", interventions_status);
        model.addAttribute("intervention", intervention);
        model.addAttribute("privilege", privilege);

        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_INTERVENTION");

        return "maintenances/interventions_status";
    }
    @GetMapping("/interventions_taches/{id}")
    public String taches(@PathVariable(name = "id") Long id, Model model, @AuthenticationPrincipal User user, HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_INTERVENTION");

        Intervention intervention = interventionRepository.findInterventionByID(id);
        List<Intervention_Tache> list = interventionTacheRepository.findIntervention_TacheByIntervention(false,intervention);
        List<Tache> taches = tacheRepository.findAllTachesNotYet(false,intervention);
        List<Ouvrier> ouvriers = ouvrierRepository.findOuvriers(false);

        model.addAttribute("intervention", intervention);
        model.addAttribute("list", list);
        model.addAttribute("ouvriers", ouvriers);
        model.addAttribute("taches", taches);
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
        model.addAttribute("sousMenuActive", "MAINTENANCE_INTERVENTION");


        return "maintenances/interventions_taches";
    }
    @PostMapping(value = "/addTache")
    public RedirectView addTache(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String action = request.getParameter("action");
        String id = request.getParameter("id");
        String observation = request.getParameter("observation");
        String date = request.getParameter("date");
        String tache_id = request.getParameter("tache_id");
        String employe_id = request.getParameter("employe_id");
        String duree = request.getParameter("duree");
        Tache tache = null;
        if(tache_id !=null && tache_id.equals("new")){

            String code_tache = request.getParameter("code_tache");
            String libelle_tache = request.getParameter("libelle_tache");
            String groupe_tache = request.getParameter("groupe_tache");
            String temps_tache = request.getParameter("temps_tache");

            tache = new Tache();
            tache.setLibelle(libelle_tache);
            tache.setCode(code_tache);
            tache.setGroupe(groupe_tache);
            tache.setTemps_estime(Double.parseDouble(temps_tache));
            tache.setEtatSupp(false);
            tache = tacheRepository.save(tache);
        }else{
            if(tache_id !=null && !tache_id.equals("")) tache = tacheRepository.findTacheByID(Long.parseLong(tache_id));
        }
        Intervention intervention = interventionRepository.findInterventionByID(Long.parseLong(id));
        Ouvrier ouvrier = ouvrierRepository.findOuvrierByID(Long.parseLong(employe_id));

        Intervention_Tache contrat = new Intervention_Tache();
        contrat.setObservation(observation);
        contrat.setIntervention(intervention);
        contrat.setDate(sdf.parse(date));
        contrat.setTache(tache);
        contrat.setOuvrier(ouvrier);
        contrat.setDuree(Double.parseDouble(duree));
        contrat.setEtatSupp(false);
        interventionTacheRepository.save(contrat);

        ouvrier.setDisponible(false);
        ouvrierRepository.save(ouvrier);
        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER LA TACHE "+tache.getLibelle()+" A L'INTERVENTION  : "+intervention.getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        if(action != null && action.equals("detail")) {
            return new RedirectView("interventions_detail/"+intervention.getId()+"?added="+added);
        }else{
            return new RedirectView("interventions_taches/"+intervention.getId()+"?added="+added);
        }
    }
    @PostMapping(value = "/deleteTache")
    public RedirectView deleteTache(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String deleted = "nok";

        String id = request.getParameter("id");

        Intervention_Tache tache =  interventionTacheRepository.findIntervention_TacheByID(Long.parseLong(id));
        if(tache.getIntervention().getStatus().getId() == 1){
            tache.setEtatSupp(true);
            interventionTacheRepository.save(tache);

            Ouvrier ouvrier=tache.getOuvrier();
            ouvrier.setDisponible(true);
            ouvrierRepository.save(ouvrier);
            deleted = "ok";

        }


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER LA TACHE : "+ tache.getTache().getLibelle()+" DE L'INTERVENTION :" + tache.getIntervention().getCode());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("interventions_taches/"+tache.getIntervention().getId()+"?deleted="+deleted);
    }

    @GetMapping("/interventions_calendar")
    public String interventions_calendar(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) throws ParseException {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_INTERVENTION");
        model.addAttribute("privilege", privilege);

        List<Intervention> interventions = interventionRepository.findAllInterventions(false);
        List<Intervention_Status> status = interventionStatutRepository.findAllStatus();
        List<Intervention_Type> types = interventionTypeRepository.findAllTypes(false);
        List<Equipement> equipements = equipementRepository.findAllEquipements(false);


        List<HashMap<String,Object>> list = new ArrayList<>();
        for(Intervention intervention : interventions){
            HashMap<String,Object> obj = new HashMap<>();
            obj.put("id",intervention.getId());
            obj.put("title",intervention.getCode());
            obj.put("start",intervention.getDate_debut());
            if(intervention.getDate_fin() != null) obj.put("end",intervention.getDate_fin());
            obj.put("description",intervention.getObservation());
            obj.put("statut",intervention.getStatus().getLibelle());
            switch (intervention.getStatus().getId().intValue()){
                case 1 :
                    obj.put("className","text-primary");
                    break;
                case 2 :
                    obj.put("className","text-warning");
                    break;
                case 3 :
                    obj.put("className","text-success");
                    break;
                case 4 :
                    obj.put("className","text-danger");
                    break;

            }


            HashMap<String,Object> tacheObj = new HashMap<>();
            tacheObj.put("calendar",intervention.getStatus().getLibelle());
            tacheObj.put("statut",intervention.getStatus().getId());
            tacheObj.put("description",intervention.getObservation());
            obj.put("extendedProps",tacheObj);

            list.add(obj);
        }

        model.addAttribute("interventions_obj", list);
        model.addAttribute("privilege", privilege);

        model.addAttribute("status", status);
        model.addAttribute("types", types);
        model.addAttribute("equipements", equipements);

        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_INTERVENTION");


        return "maintenances/interventions_calendar";
    }



    //*************************************PERSONNEL*********************************************************************************
    @GetMapping("/services_list")
    public String services(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_PERSONNEL_SERVICES");
        List<Service_Travaux> services = serviceRepository.findAllService_Travauxs(false);
        for(Service_Travaux e :services){
            List<Ouvrier> ouvriers = ouvrierRepository.findAllOuvriersByService(e);
            boolean canDelete=false;
            boolean canUpdate=false;
            if(ouvriers.size() == 0) {
                canDelete=true;
                canUpdate=true;
            }
            e.setCanDelete(canDelete);
            e.setCanUpdate(canUpdate);
        }
        model.addAttribute("services", services);
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
        model.addAttribute("sousMenuActive", "MAINTENANCE_PERSONNEL");
        model.addAttribute("soussousMenuActive", "MAINTENANCE_PERSONNEL_SERVICES");

        return "maintenances/services_list";
    }
    @PostMapping(value = "/services_add")
    public RedirectView addService(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String libelle = request.getParameter("libelle");

        Service_Travaux service = new Service_Travaux();
        service.setLibelle(libelle);
        service.setEtatSupp(false);
        serviceRepository.save(service);

        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UN SERVICE : "+service.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("EMPLOYÉS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("services_list?added="+added);
    }
    @PostMapping(value = "/services_edit")
    public RedirectView editService(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");
        String libelle = request.getParameter("libelle");

        Service_Travaux tache = serviceRepository.findService_TravauxByID(Long.parseLong(id));
        tache.setLibelle(libelle);
        serviceRepository.save(tache);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER LE SERVICE : "+tache.getLibelle());
        history.setDateCreation(new Date());
        history.setModule("EMPLOYÉS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("services_list?updated="+added);
    }
    @PostMapping(value = "/services_delete")
    public RedirectView deleteService(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());

        String deleted = "nok";
        String id = request.getParameter("id");
        Service_Travaux service = serviceRepository.findService_TravauxByID(Long.parseLong(id));
        List<Ouvrier> ouvriers = ouvrierRepository.findAllOuvriersByService(service);
        if(ouvriers.size() == 0){
            service.setEtatSupp(true);
            serviceRepository.save(service);
            deleted="ok";
            //**********************************************************************
            //**********************************************************************
            History history = new History();
            history.setAction("SUPPRIMER UN SERVICE : "+service.getLibelle());
            history.setDateCreation(new Date());
            history.setModule("EMPLOYÉS");
            history.setUtilisateur(utilisateur);
            historyRepo.save(history);
            //**********************************************************************
            //**********************************************************************
        }


        return new RedirectView("services_list?deleted="+deleted);
    }

    @GetMapping("/employes_list")
    public String employes(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_PERSONNEL_PERSONNEL");
        List<Ouvrier> ouvriers = ouvrierRepository.findAllOuvriers(false);
        List<Service_Travaux> services = serviceRepository.findAllService_Travauxs(false);
        List<String> cins = ouvrierRepository.findAllCIN(false);
        for(Ouvrier e :ouvriers){
            List<Intervention_Tache> taches = interventionTacheRepository.findIntervention_TacheByOuvrier(e);
            boolean canDelete=false;
            boolean canUpdate=false;
            if(taches.size() == 0) {
                canDelete=true;
                canUpdate=true;
            }
            e.setCanDelete(canDelete);
            e.setCanUpdate(canUpdate);
        }
        model.addAttribute("cins", cins);
        model.addAttribute("ouvriers", ouvriers);
        model.addAttribute("services", services);
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
        model.addAttribute("soussousMenuActive", "MAINTENANCE_PERSONNEL_PERSONNEL");
        model.addAttribute("sousMenuActive", "MAINTENANCE_PERSONNEL");

        return "maintenances/employes_list";
    }
    @PostMapping(value = "/employes_add")
    public RedirectView addEmploye(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String cin = request.getParameter("cin");
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String service_id = request.getParameter("service_id");
        double tjm = Double.parseDouble(request.getParameter("tjm"));
        Service_Travaux service_travaux =  null;
        if(service_id !=null && service_id.equals("new")){
            String libelle_type = request.getParameter("libelle_type");
            service_travaux= new Service_Travaux();
            service_travaux.setEtatSupp(false);
            service_travaux.setLibelle(libelle_type);
            serviceRepository.save(service_travaux);
        }
        else{
            if(service_id !=null && !service_id.equals("")) service_travaux = serviceRepository.findService_TravauxByID(Long.parseLong(service_id));
        }

        Ouvrier ouvrier = new Ouvrier();
        ouvrier.setCin(cin);
        ouvrier.setTjm(tjm);
        if(tjm >0) ouvrier.setThm(tjm/8);
        ouvrier.setNom(nom);
        ouvrier.setPrenom(prenom);
        ouvrier.setService(service_travaux);
        ouvrier.setEtatSupp(false);
        ouvrier.setDisponible(true);
        ouvrierRepository.save(ouvrier);

        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UN EMPLOYÉ : "+ouvrier.getCin() +" - "+ouvrier.getNom()+" "+ouvrier.getPrenom());
        history.setDateCreation(new Date());
        history.setModule("EMPLOYÉS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("employes_list?added="+added);
    }
    @PostMapping(value = "/employes_edit")
    public RedirectView editEmploye(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");
        String cin = request.getParameter("cin");
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String service_id = request.getParameter("service_id");
        double tjm = Double.parseDouble(request.getParameter("tjm"));

        Service_Travaux service_travaux = serviceRepository.findService_TravauxByID(Long.parseLong(service_id));
        Ouvrier ouvrier = ouvrierRepository.findOuvrierByID(Long.parseLong(id));
        ouvrier.setCin(cin);
        ouvrier.setTjm(tjm);
        if(tjm >0) ouvrier.setThm(tjm/8);
        ouvrier.setNom(nom);
        ouvrier.setPrenom(prenom);
        ouvrier.setService(service_travaux);
        ouvrierRepository.save(ouvrier);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER L'EMPLOYÉ : "+ouvrier.getCin() +" - "+ouvrier.getNom()+" "+ouvrier.getPrenom());
        history.setDateCreation(new Date());
        history.setModule("EMPLOYÉS");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new RedirectView("employes_list?updated="+added);
    }
    @PostMapping(value = "/employes_delete")
    public RedirectView deleteEmploye(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());

        String deleted = "nok";
        String id = request.getParameter("id");
        Ouvrier ouvrier = ouvrierRepository.findOuvrierByID(Long.parseLong(id));
        List<Intervention_Tache> taches = interventionTacheRepository.findIntervention_TacheByOuvrier(ouvrier);
        if(taches.size() == 0){
            ouvrier.setEtatSupp(true);
            ouvrierRepository.save(ouvrier);
            deleted="ok";
            //**********************************************************************
            //**********************************************************************
            History history = new History();
            history.setAction("SUPPRIMER L'EMPLOYÉ : "+ouvrier.getCin() +" - "+ouvrier.getNom()+" "+ouvrier.getPrenom());
            history.setDateCreation(new Date());
            history.setModule("EMPLOYÉS");
            history.setUtilisateur(utilisateur);
            historyRepo.save(history);
            //**********************************************************************
            //**********************************************************************
        }


        return new RedirectView("employes_list?deleted="+deleted);
    }


    //*************************************ANOMALIES*********************************************************************************
    @GetMapping("/reclamations_list")
    public String reclamations(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_ANOMALIE");
        List<Reclamation> reclamations = new ArrayList<>();

        String str = request.getParameter("reclamation_id");
        if(str != null && !"".equals(str)){
            Reclamation reclamation = reclamationRepository.findArticleByID(Long.parseLong(str));
            reclamations.add(reclamation);

            List<Notification> notifications = notificationsRepo.findNotificationByObjid(reclamation.getId(),"RECLAMATION");
            currentUser.getNotifications().removeAll(notifications);
            utilisateurRepo.save(currentUser);


        }else{
            reclamations = reclamationRepository.findAllReclamations(false);
        }

        for(Reclamation st : reclamations){
            List<Reclamation_PJ> pjs = reclamation_pjRepository.findAllReclamationsByReclamation(false,st);
            boolean canDelete=true;
            boolean canUpdate=true;
            if(pjs.size()>0){
                canDelete=false;
                canUpdate=false;
            }
            st.setCanDelete(canDelete);
            st.setCanUpdate(canUpdate);
        }
        List<Emplacement> emplacements = emplacementRepository.findAllEmplacemets(false);

        model.addAttribute("emplacements", emplacements);

        model.addAttribute("reclamations", reclamations);
        model.addAttribute("privilege", privilege);


        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));
        if (request.getParameter("traite") != null)
            model.addAttribute("traite", request.getParameter("traite"));
        if (request.getParameter("demarre") != null)
            model.addAttribute("demarre", request.getParameter("demarre"));
        if (request.getParameter("reopened") != null)
            model.addAttribute("reopened", request.getParameter("reopened"));
if (request.getParameter("nonvalide") != null)
            model.addAttribute("nonvalide", request.getParameter("nonvalide"));


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_ANOMALIE");


        return "maintenances/reclamations_list";
    }
    @GetMapping("/reclamations_add")
    public String reclamations_add(Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_ANOMALIE");

        List<Emplacement> emplacements = emplacementRepository.findAllEmplacemets(false);
        List<Equipement> equipements = equipementRepository.findAllEquipements(false);
        List<Service_Travaux> services = serviceRepository.findAllService_Travauxs(false);
        List<Utilisateur> utilisateurs = utilisateurRepo.findByEtat_supp(false);

        model.addAttribute("utilisateurs", utilisateurs);
        model.addAttribute("services", services);
        model.addAttribute("emplacements", emplacements);
        model.addAttribute("equipements", equipements);
        model.addAttribute("privilege", privilege);


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_ANOMALIE");


        return "maintenances/reclamations_add";
    }
    @PostMapping(value = "/reclamations_add")
    public RedirectView addReclamation(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String date = request.getParameter("date");
        String libelle = request.getParameter("libelle");
        String titre = request.getParameter("titre");
        String emplacement_id = request.getParameter("emplacement_id");
        String equipement_id = request.getParameter("equipement_id");
        boolean urgente = false;
        String urgent = request.getParameter("urgent");
        if(urgent != null && urgent.equals("oui")) urgente = true;
        String service_id = request.getParameter("service_id");
        String employe_id = request.getParameter("employe_id");

        Utilisateur employe =  null;
        if(employe_id !=null && !employe_id.equals("")){
            if(employe_id !=null && !employe_id.equals("")) employe = utilisateurRepo.findById(Long.parseLong(employe_id));
        }

        Service_Travaux service_travaux =  null;
        if(service_id !=null && service_id.equals("new")){
            String libelle_type = request.getParameter("libelle_type");
            service_travaux= new Service_Travaux();
            service_travaux.setEtatSupp(false);
            service_travaux.setLibelle(libelle_type);
            serviceRepository.save(service_travaux);
        }
        else{
            if(service_id !=null && !service_id.equals("")) service_travaux = serviceRepository.findService_TravauxByID(Long.parseLong(service_id));
        }

        Emplacement emplacement = null;
        if(emplacement_id !=null && emplacement_id.equals("new")){
            String libelle_emplacement= request.getParameter("libelle_emplacement");
            String code_emplacement= request.getParameter("code_emplacement");
            emplacement= new Emplacement();
            emplacement.setEtatSupp(false);
            emplacement.setCode(code_emplacement);
            emplacement.setLibelle(libelle_emplacement);
            emplacementRepository.save(emplacement);
        }
        else{
            if(emplacement_id !=null && !emplacement_id.equals("")) emplacement = emplacementRepository.findEmplacementByID(Long.parseLong(emplacement_id));
        }

        Equipement equipement = null;
        if(equipement_id !=null && !equipement_id.equals("")){
            equipement=equipementRepository.findEquipementByID(Long.parseLong(equipement_id));
        }
        Reclamation_Statut status = reclamationStatusRepository.findStatusByID(1);

        Reclamation reclamation = new Reclamation();
        reclamation.setDate(sdf.parse(date));
        reclamation.setLibelle(libelle);
        reclamation.setTitre(titre);
        reclamation.setEtatSupp(false);
        reclamation.setUser(utilisateur);
        reclamation.setService(service_travaux);
        reclamation.setResponsable(employe);
        reclamation.setEmplacement(emplacement);
        reclamation.setEquipement(equipement);
        reclamation.setUrgent(urgente);
        reclamation.setStatus(status);
        reclamationRepository.save(reclamation);

        if(status != null ){
            reclamationHistoStatutRepository.updateOldStatusByReclamation(reclamation);
            Reclamation_Historique_Statut histo = new Reclamation_Historique_Statut();
            histo.setReclamation(reclamation);
            histo.setActual(true);
            histo.setStatus(status);
            histo.setDate(new Date());
            reclamationHistoStatutRepository.save(histo);
        }

        if(employe != null){
            Notification notification = new Notification();
            notification.setModule("RECLAMATION");
            notification.setTitre("Nouvelle réclamation ajoutée");
            notification.setObj_id(reclamation.getId());
            notification.setUrl("/maintenances/reclamations_list?reclamation_id="+reclamation.getId());
            notificationsRepo.save(notification);

            employe.getNotifications().add(notification);
            utilisateurRepo.save(employe);
        }

        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UNE NOUVELLE RÈCLAMATION "+reclamation.getTitre());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("reclamations_list?added="+added);
    }
    @GetMapping("/reclamations_edit/{id}")
    public String reclamations_edit(@PathVariable(name = "id") Long id, Model model, @AuthenticationPrincipal User user,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_ANOMALIE");

        Reclamation reclamation= reclamationRepository.findArticleByID(id);
        List<Emplacement> emplacements = emplacementRepository.findAllEmplacemets(false);
        List<Equipement> equipements = equipementRepository.findAllEquipements(false);
        List<Service_Travaux> services = serviceRepository.findAllService_Travauxs(false);
        List<Utilisateur> utilisateurs = utilisateurRepo.findByEtat_supp(false);

        model.addAttribute("utilisateurs", utilisateurs);
        model.addAttribute("services", services);
        model.addAttribute("reclamation", reclamation);
        model.addAttribute("emplacements", emplacements);
        model.addAttribute("equipements", equipements);
        model.addAttribute("privilege", privilege);


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_ANOMALIE");



        return "maintenances/reclamations_edit";
    }
    @PostMapping(value = "/reclamations_edit")
    public RedirectView updateReclamation(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");

        String date = request.getParameter("date");
        String libelle = request.getParameter("libelle");
        String titre = request.getParameter("titre");
        String emplacement_id = request.getParameter("emplacement_id");
        String equipement_id = request.getParameter("equipement_id");
        String service_id = request.getParameter("service_id");
        String employe_id = request.getParameter("employe_id");

        Utilisateur employe =  null;
        if(employe_id !=null && !employe_id.equals("")){
            if(employe_id !=null && !employe_id.equals("")) employe = utilisateurRepo.findById(Long.parseLong(employe_id));
        }

        Service_Travaux service_travaux =  null;
        if(service_id !=null && service_id.equals("new")){
            String libelle_type = request.getParameter("libelle_type");
            service_travaux= new Service_Travaux();
            service_travaux.setEtatSupp(false);
            service_travaux.setLibelle(libelle_type);
            serviceRepository.save(service_travaux);
        }
        else{
            if(service_id !=null && !service_id.equals("")) service_travaux = serviceRepository.findService_TravauxByID(Long.parseLong(service_id));
        }


        Emplacement emplacement = emplacementRepository.findEmplacementByID(Long.parseLong(emplacement_id));
        String urgent = request.getParameter("urgent");
        boolean urgente = false;
        if(urgent != null && urgent.equals("oui")) urgente = true;
        Equipement equipement = null;
        if(equipement_id !=null && !equipement_id.equals("")){
            equipement=equipementRepository.findEquipementByID(Long.parseLong(equipement_id));
        }

        Reclamation reclamation = reclamationRepository.findArticleByID(Long.parseLong(id));
        reclamation.setDate(sdf.parse(date));
        reclamation.setDate_modification(new Date());
        reclamation.setLibelle(libelle);
        reclamation.setTitre(titre);
        reclamation.setUser(utilisateur);
        reclamation.setService(service_travaux);
        reclamation.setResponsable(employe);
        reclamation.setEmplacement(emplacement);
        reclamation.setEquipement(equipement);
        reclamation.setUrgent(urgente);
        reclamationRepository.save(reclamation);


        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER LA RECLAMATION : "+reclamation.getTitre());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("reclamations_list?updated="+added);
    }
    @PostMapping(value = "/reclamations_delete")
    public RedirectView deleteReclamation(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");
        Reclamation reclamation = reclamationRepository.findArticleByID(Long.parseLong(id));

        reclamation.setEtatSupp(true);
        reclamationRepository.save(reclamation);


        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER LA RéCLAMATION : "+reclamation.getTitre());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("reclamations_list?deleted="+added);
    }
    @PostMapping(value = "/reclamations_traiter")
    public RedirectView traiterReclamation(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");
        String response = request.getParameter("response");
        Reclamation reclamation = reclamationRepository.findArticleByID(Long.parseLong(id));
        Reclamation_Statut status = reclamationStatusRepository.findStatusByID(3);

        reclamation.setStatus(status);
        reclamation.setDate_traitement(new Date());
        reclamation.setResponse(response);
        reclamationRepository.save(reclamation);

        if(status != null ){
            reclamationHistoStatutRepository.updateOldStatusByReclamation(reclamation);
            Reclamation_Historique_Statut histo = new Reclamation_Historique_Statut();
            histo.setReclamation(reclamation);
            histo.setDescription(response);
            histo.setActual(true);
            histo.setStatus(status);
            histo.setDate(new Date());
            reclamationHistoStatutRepository.save(histo);
        }

        added = "ok";

        if(reclamation.getResponsable() != null){
            Notification notification = new Notification();
            notification.setModule("RECLAMATION");
            notification.setTitre("Réclamation a été traitée");
            notification.setObj_id(reclamation.getId());
            notification.setUrl("/maintenances/reclamations_list?reclamation_id="+reclamation.getId());
            notificationsRepo.save(notification);

            Utilisateur responsable = reclamation.getResponsable();
            responsable.getNotifications().add(notification);
            utilisateurRepo.save(responsable);
        }

        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER LE STATUT DE LA RéCLAMATION : "+reclamation.getTitre()+" EN TRAITÉ");
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        String action = request.getParameter("action");
        if(action != null && "detail".equals(action)){
            return new RedirectView("reclamations_detail/"+reclamation.getId()+"?traite="+added);

        }else{
            return new RedirectView("reclamations_list?traite="+added);

        }
    }
     @PostMapping(value = "/reclamations_non_valider")
    public RedirectView reclamations_non_valider(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");
        String response = request.getParameter("response");
        Reclamation reclamation = reclamationRepository.findArticleByID(Long.parseLong(id));
        Reclamation_Statut status = reclamationStatusRepository.findStatusByID(4);

        reclamation.setStatus(status);
        reclamation.setDate_traitement(new Date());
        reclamation.setNon_valide(response);
        reclamationRepository.save(reclamation);

        if(status != null ){
            reclamationHistoStatutRepository.updateOldStatusByReclamation(reclamation);
            Reclamation_Historique_Statut histo = new Reclamation_Historique_Statut();
            histo.setReclamation(reclamation);
            histo.setDescription(response);
            histo.setActual(true);
            histo.setStatus(status);
            histo.setDate(new Date());
            reclamationHistoStatutRepository.save(histo);
        }

        added = "ok";

        if(reclamation.getResponsable() != null){
            Notification notification = new Notification();
            notification.setModule("RECLAMATION");
            notification.setTitre("Réclamation a été annulé");
            notification.setObj_id(reclamation.getId());
            notification.setUrl("/maintenances/reclamations_list?reclamation_id="+reclamation.getId());
            notificationsRepo.save(notification);

            Utilisateur responsable = reclamation.getResponsable();
            responsable.getNotifications().add(notification);
            utilisateurRepo.save(responsable);
        }

        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER LE STATUT DE LA RéCLAMATION : "+reclamation.getTitre()+" EN NON VALIDE");
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        String action = request.getParameter("action");
        if(action != null && "detail".equals(action)){
            return new RedirectView("reclamations_detail/"+reclamation.getId()+"?nonvalide="+added);

        }else{
            return new RedirectView("reclamations_list?nonvalide="+added);

        }
    }
    @PostMapping(value = "/reclamations_open")
    public RedirectView reopenReclamation(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");
        String response = request.getParameter("cause");
        Reclamation reclamation = reclamationRepository.findArticleByID(Long.parseLong(id));
        Reclamation_Statut status = reclamationStatusRepository.findStatusByID(2);

        reclamation.setStatus(status);
        reclamation.setDate_traitement(new Date());
        reclamation.setResponse(response);
        reclamationRepository.save(reclamation);

        if(status != null ){
            reclamationHistoStatutRepository.updateOldStatusByReclamation(reclamation);
            Reclamation_Historique_Statut histo = new Reclamation_Historique_Statut();
            histo.setReclamation(reclamation);
            histo.setDescription(response);
            histo.setActual(true);
            histo.setStatus(status);
            histo.setDate(new Date());
            reclamationHistoStatutRepository.save(histo);
        }


        if(reclamation.getResponsable() != null){
            Notification notification = new Notification();
            notification.setModule("RECLAMATION");
            notification.setTitre("Réclamation a été réouverte");
            notification.setObj_id(reclamation.getId());
            notification.setUrl("/maintenances/reclamations_list?reclamation_id="+reclamation.getId());
            notificationsRepo.save(notification);

            Utilisateur responsable = reclamation.getResponsable();
            responsable.getNotifications().add(notification);
            utilisateurRepo.save(responsable);
        }

        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("RéOUVRIR LA RéCLAMATION : "+reclamation.getTitre());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        String action = request.getParameter("action");
        if(action != null && "detail".equals(action)){
            return new RedirectView("reclamations_detail/"+reclamation.getId()+"?reopened="+added);

        }else{
            return new RedirectView("reclamations_list?reopened="+added);

        }
    }
    @PostMapping(value = "/reclamations_demarrer")
    public RedirectView demarrerReclamation(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");
        Reclamation reclamation = reclamationRepository.findArticleByID(Long.parseLong(id));
        Reclamation_Statut status = reclamationStatusRepository.findStatusByID(2);

        reclamation.setStatus(status);
        reclamationRepository.save(reclamation);

        if(status != null ){
            reclamationHistoStatutRepository.updateOldStatusByReclamation(reclamation);
            Reclamation_Historique_Statut histo = new Reclamation_Historique_Statut();
            histo.setReclamation(reclamation);
            histo.setStatus(status);
            histo.setActual(true);
            histo.setDate(new Date());
            reclamationHistoStatutRepository.save(histo);
        }


        if(reclamation.getResponsable() != null){
            Notification notification = new Notification();
            notification.setModule("RECLAMATION");
            notification.setTitre("Le traitement d'une réclamation est commencé");
            notification.setObj_id(reclamation.getId());
            notification.setUrl("/maintenances/reclamations_list?reclamation_id="+reclamation.getId());
            notificationsRepo.save(notification);

            Utilisateur responsable = reclamation.getResponsable();
            responsable.getNotifications().add(notification);
            utilisateurRepo.save(responsable);
        }
        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER LE STATUT DE LA RéCLAMATION : "+reclamation.getTitre()+" EN COURS");
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        String action = request.getParameter("action");
        if(action != null && "detail".equals(action)){
            return new RedirectView("reclamations_detail/"+reclamation.getId()+"?demarre="+added);

        }else{
            return new RedirectView("reclamations_list?demarre="+added);

        }
    }
    @PostMapping(value = "/reclamations_addPJ")
    public RedirectView addPJ(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model,@RequestParam(name = "file",required = false) MultipartFile file) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");
        Reclamation reclamation = reclamationRepository.findArticleByID(Long.parseLong(id));
        String chemin = chemin_pj+"ged/";
        File folder_pj = new File(chemin);
        if (!folder_pj.exists()) {
            folder_pj.mkdirs();
        }

        chemin = chemin_pj+"ged/reclamation/";
        folder_pj = new File(chemin);
        if (!folder_pj.exists()) {
            folder_pj.mkdirs();
        }

        chemin = chemin_pj+"ged/reclamation/"+reclamation.getId()+"/";
        folder_pj = new File(chemin);
        if (!folder_pj.exists()) {
            folder_pj.mkdirs();
        }

        if (!file.isEmpty()) {
            try {
                String imageFolder2 = chemin;
                File pt = new File(imageFolder2 + file.getOriginalFilename());
                File p = pt.getParentFile();

                if (!p.exists()) {
                    p.mkdirs();
                }

                String pj = file.getOriginalFilename();
                String nom_piece = pj.substring(0,pj.lastIndexOf("."));
                String extension = pj.substring(pj.lastIndexOf("."),pj.length());
                int m=1;
                while (pt.exists()) {

                    pj= nom_piece + " (" +m+")"+extension;
                    pt = new File (imageFolder2+pj);
                    m++;
                }

                file.transferTo(pt);
                Reclamation_PJ piece = new Reclamation_PJ();
                piece.setReclamation(reclamation);
                piece.setDate_ajout(new Date());
                piece.setNom_pj(pj);
                piece.setTaille_pj(file.getSize());
                piece.setAuteur(utilisateur.getFirstName()+" "+utilisateur.getLastName());
                reclamation_pjRepository.save(piece);


            } catch (IllegalStateException | IOException e) {
                System.out.println("IllegalStateException");
                e.printStackTrace();
            }
        }




        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER UNE PIÉCE JOINTE á LA RéCLAMATION : "+reclamation.getTitre());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("reclamations_detail/"+reclamation.getId()+"?added="+added);
    }
    @PostMapping(value = "/reclamations_deletePJ")
    public RedirectView deletePJ(HttpServletRequest request, @AuthenticationPrincipal User currentUser, Model model) throws ParseException {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(currentUser.getUsername());
        String added = "nok";

        String id = request.getParameter("id");
        Reclamation_PJ pj = reclamation_pjRepository.findReclamation_PByID(Long.parseLong(id));

        pj.setEtatSupp(true);
        reclamation_pjRepository.save(pj);


        added = "ok";


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER LA PJ : "+pj.getNom_pj()+" DE LA RECLAMATION : "+pj.getReclamation().getTitre());
        history.setDateCreation(new Date());
        history.setModule("MAINTENANCES");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new RedirectView("reclamations_detail/"+pj.getReclamation().getId()+"?deleted="+added);
    }
    @GetMapping("/reclamations_detail/{id}")
    public String detail(@PathVariable(name = "id") Long id, Model model, @AuthenticationPrincipal User user, HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_ANOMALIE");

        Reclamation reclamation = reclamationRepository.findArticleByID(id);
        List<Reclamation_PJ> pjs = reclamation_pjRepository.findAllReclamationsByReclamation(false,reclamation);
        boolean canDelete=true;
        boolean canUpdate=true;
        if(pjs.size()>0){
            canDelete=false;
            canUpdate=false;
        }
        reclamation.setCanDelete(canDelete);
        reclamation.setCanUpdate(canUpdate);

        model.addAttribute("privilege", privilege);
        model.addAttribute("reclamation", reclamation);
        model.addAttribute("pjs", pjs);


        if (request.getParameter("added") != null)
            model.addAttribute("added", request.getParameter("added"));
        if (request.getParameter("updated") != null)
            model.addAttribute("updated", request.getParameter("updated"));
        if (request.getParameter("started") != null)
            model.addAttribute("started", request.getParameter("started"));
        if (request.getParameter("finished") != null)
            model.addAttribute("finished", request.getParameter("finished"));
        if (request.getParameter("deleted") != null)
            model.addAttribute("deleted", request.getParameter("deleted"));
        if (request.getParameter("traite") != null)
            model.addAttribute("traite", request.getParameter("traite"));
        if (request.getParameter("demarre") != null)
            model.addAttribute("demarre", request.getParameter("demarre"));



        /**  ? menue active */

        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_ANOMALIE");


        return "maintenances/reclamations_detail";
    }
    @GetMapping(value = "/reclamations_showPJ")
    public void showPJ(HttpServletResponse response, @RequestParam("id") long id, @RequestParam("pj_id") long pj_id) throws IOException {
        response.setContentType("image/png");
        //response.setHeader("Content-Disposition", "attachment; filename=\"demo.pdf\"");
        Reclamation_PJ pj = reclamation_pjRepository.findReclamation_PByID(pj_id);

        InputStream inputStream = new FileInputStream(new File(chemin_pj +"ged/reclamation/"+id+"/"+pj.getNom_pj()));
        int nRead;
        while ((nRead = inputStream.read()) != -1) {
            response.getWriter().write(nRead);
        }
    }
    @GetMapping("/reclamations_status/{id}")
    public String reclamations_status(@PathVariable(name = "id") Long id,Model model, @AuthenticationPrincipal User user, HttpSession session,HttpServletRequest request) {
        Utilisateur currentUser = utilisateurRepo.findByUsername(user.getUsername());
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), "MAINTENANCE_FRN_ST");
        Reclamation reclamation = reclamationRepository.findArticleByID(id);
        List<Reclamation_Historique_Statut> reclamations_status = reclamationHistoStatutRepository.findAllByReclamation(reclamation);

        model.addAttribute("reclamations_status", reclamations_status);
        model.addAttribute("reclamation", reclamation);
        model.addAttribute("privilege", privilege);


        /**  ? menue active */
        model.addAttribute("sousMenuActive", "MAINTENANCE_ANOMALIE");


        return "maintenances/reclamations_status";
    }



    @RequestMapping(value={"/getEquipements/{id}"}, method={RequestMethod.GET})
    @ResponseBody
    public List<Equipement> getEquipements(@PathVariable("id") long id) {
        Emplacement emplacement = emplacementRepository.findEmplacementByID(id);
        List<Equipement> equipements = equipementRepository.findAllEquipementsByEmplacement(emplacement,false);
        return equipements;
    }


    @RequestMapping(value={"/getFournisseurs/{equipement_id}/{type_id}"}, method={RequestMethod.GET})
    @ResponseBody
    public List<Fournisseur_Service> getFournisseurs(@PathVariable("equipement_id") long equipement_id,@PathVariable("type_id") long type_id) {
        Equipement equipement = equipementRepository.findEquipementByID(equipement_id);
        Maintenance_Type type = maintenanceTypeRepository.findTypeByID(type_id);
        List<Fournisseur_Service> fournisseurs = contratMaintenanceDetailRepository.findAllFournisseursByEquipement(false,equipement,type);
        return fournisseurs;
    }

}

