package jway.gmao.api;

import jway.gmao.dao.*;
import jway.gmao.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@CrossOrigin("*")
@RequestMapping({"/apiMobile/reclamation"})
@RestController
public class ApiReclamationController {
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private UtilisateurRepo accountService,userRepository;
    @Autowired
    private HistoryRepo historyRepo;
    @Autowired
    private ReclamationRepository reclamationRepository;
    @Autowired
    private Reclamation_PJRepository reclamation_pjRepository;
    @Autowired
    private ReclamationStatusRepository reclamationStatusRepository;
    @Autowired
    private EmplacementRepository emplacementRepository;
    @Autowired
    private ReclamationHistoStatutRepository reclamationHistoStatutRepository;
@Autowired
    private EquipementRepository equipementRepository;
@Autowired
    private ServiceRepository serviceRepository;
@Autowired
    private UtilisateurRepo utilisateurRepo;
@Autowired
    private NotificationsRepo notificationsRepo;


    @RequestMapping(value = "/getEmplacements", method = RequestMethod.GET)
    public ResponseEntity getEmplacements(@RequestHeader(value = "header") String header) {
        if (userRepository.findToken(header) == null) {
            return new ResponseEntity("Vous n'avez pas le droit d'accéder à cette page", HttpStatus.UNAUTHORIZED);
        }
        List<Emplacement> emplacements = emplacementRepository.findAllEmplacemets(false);
        List<Equipement> equipements = equipementRepository.findAllEquipements(false);
        List<Service_Travaux> services = serviceRepository.findAllService_Travauxs(false);
        List<Utilisateur> utilisateurs = utilisateurRepo.findByEtat_supp(false);

        List<Object> list = new ArrayList<>();
        list.add(emplacements);
        list.add(equipements);
        list.add(services);
        list.add(utilisateurs);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity reclamations(@RequestHeader(value = "header") String header) {
        if (userRepository.findToken(header) == null) {
            return new ResponseEntity("Vous n'avez pas le droit d'accéder à cette page", HttpStatus.UNAUTHORIZED);
        }
        List<Reclamation> reclamations = reclamationRepository.findAllReclamations(false);
        for(Reclamation r : reclamations){
            List<String> pjs = reclamation_pjRepository.findNamesByReclamation(false,r);
            r.setFileNames(pjs);
        }

        return new ResponseEntity<>(reclamations, HttpStatus.OK);
    }

    @RequestMapping(value = "/reclamationsBy/{id}", method = RequestMethod.GET)
    public ResponseEntity reclamationsBy(@RequestHeader(value = "header") String header, @PathVariable(name = "id") long id) {
        if (userRepository.findToken(header) == null) {
            return new ResponseEntity("Vous n'avez pas le droit d'accéder à cette page", HttpStatus.UNAUTHORIZED);
        }
        Utilisateur user = userRepository.findUtilisateurById(id);
        List<Reclamation> reclamations = reclamationRepository.findAllReclamationsByUser(false,user);
        for(Reclamation r:reclamations){
            List<String> pjs = reclamation_pjRepository.findNamesByReclamation(false,r);
            r.setFileNames(pjs);
        }

        return new ResponseEntity<>(reclamations, HttpStatus.OK);
    }

    @RequestMapping(value = "/reclamationsByID/{id}", method = RequestMethod.GET)
    public ResponseEntity reclamationsByID(@RequestHeader(value = "header") String header, @PathVariable(name = "id") Long id) {
        if (userRepository.findToken(header) == null) {
            return new ResponseEntity("Vous n'avez pas le droit d'accéder à cette page", HttpStatus.UNAUTHORIZED);
        }
        Reclamation r = reclamationRepository.findArticleByID(id);
        List<Reclamation_Historique_Statut> reclamations_status = reclamationHistoStatutRepository.findAllByReclamation(r);
        List<String> pjs = reclamation_pjRepository.findNamesByReclamation(false,r);
        r.setFileNames(pjs);

        List<Object> list= new ArrayList<>();
        list.add(r);
        list.add(reclamations_status);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = {"/addReclamation"}, method = {RequestMethod.POST})
    public ResponseEntity addReclamation(@RequestBody DTOForm request) throws ParseException {

        Long id = request.getId();
        String libelle = request.getLibelle();
        String titre = request.getTitre();
        String date = request.getDate();
        Long emlacement_id = request.getEmplacement_id();
        Long responsable_id = request.getResponsable_id();
        Long service_id = request.getService_id();
        Long equipement_id = request.getEquipement_id();
        boolean urgente = false;
        String urgent = request.getUrgent();
        if(urgent != null && urgent.equals("oui")) urgente = true;
        
        
        Utilisateur utilisateur = userRepository.findUtilisateurById(id);
        Reclamation_Statut status = reclamationStatusRepository.findStatusByID(1);
        Emplacement emplacement = emplacementRepository.findEmplacementByID(emlacement_id);
        Service_Travaux service_travaux = serviceRepository.findService_TravauxByID(service_id);
        Utilisateur employe = utilisateurRepo.findUtilisateurById(responsable_id);
        Equipement equipement = equipementRepository.findEquipementByID(equipement_id);

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
            Reclamation_Historique_Statut histo = new Reclamation_Historique_Statut();
            histo.setReclamation(reclamation);
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

        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("AJOUTER (M) UNE NOUVELLE RÈCLAMATION "+reclamation.getTitre());
        history.setDateCreation(new Date());
        history.setModule("RÉCLAMATION");
        history.setVersion("MOBILE");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************
        
        
        return new ResponseEntity<>(reclamation.getId(), HttpStatus.OK);
    }

    @RequestMapping(value = {"/updateReclamation/{id}"}, method = {RequestMethod.POST})
    public ResponseEntity updateReclamation( @PathVariable(name = "reclamation_id") long reclamation_id,@RequestBody DTOForm request) throws ParseException {

        Long id = request.getId();
        String libelle = request.getLibelle();
        String titre = request.getTitre();
        String date = request.getDate();
        Long emlacement_id = request.getEmplacement_id();
        Long responsable_id = request.getResponsable_id();
        Long service_id = request.getService_id();
        Long equipement_id = request.getEquipement_id();
        boolean urgente = false;
        String urgent = request.getUrgent();
        if(urgent != null && urgent.equals("oui")) urgente = true;


        Utilisateur utilisateur = userRepository.findUtilisateurById(id);
        Reclamation_Statut status = reclamationStatusRepository.findStatusByID(1);
        Emplacement emplacement = emplacementRepository.findEmplacementByID(emlacement_id);
        Service_Travaux service_travaux = serviceRepository.findService_TravauxByID(service_id);
        Utilisateur employe = utilisateurRepo.findUtilisateurById(responsable_id);
        Equipement equipement = equipementRepository.findEquipementByID(equipement_id);

        Reclamation reclamation = reclamationRepository.findArticleByID(reclamation_id);
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


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("MODIFIER (M) UNE RÈCLAMATION "+reclamation.getTitre());
        history.setDateCreation(new Date());
        history.setModule("RÉCLAMATION");
        history.setVersion("MOBILE");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new ResponseEntity<>(reclamation.getId(), HttpStatus.OK);
    }

    @RequestMapping(value = {"/traiterReclamation"}, method = {RequestMethod.POST})
    public ResponseEntity traiterReclamation(@RequestBody DTOForm request) throws ParseException {

        Long id = request.getId();
        Utilisateur utilisateur = userRepository.findUtilisateurById(id);

        Long reclamation_id = request.getReclamation_id();
        String response = request.getResponse();
        Reclamation reclamation = reclamationRepository.findArticleByID(reclamation_id);
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
        history.setAction("MODIFIER (M) LE STATUT DE LA RéCLAMATION : "+reclamation.getTitre()+" EN TRAITÉ");
        history.setDateCreation(new Date());
        history.setModule("RÉCLAMATION");
        history.setVersion("MOBILE");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new ResponseEntity<>(reclamation.getId(), HttpStatus.OK);
    }

   @RequestMapping(value = {"/nonvaliderReclamation"}, method = {RequestMethod.POST})
    public ResponseEntity nonvaliderReclamation(@RequestBody DTOForm request) throws ParseException {

        Long id = request.getId();
        Utilisateur utilisateur = userRepository.findUtilisateurById(id);

        Long reclamation_id = request.getReclamation_id();
        String response = request.getResponse();
        Reclamation reclamation = reclamationRepository.findArticleByID(reclamation_id);
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


        if(reclamation.getResponsable() != null){
            Notification notification = new Notification();
            notification.setModule("RECLAMATION");
            notification.setTitre("Réclamation a été annulée");
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
        history.setAction("MODIFIER (M) LE STATUT DE LA RéCLAMATION : "+reclamation.getTitre()+" EN NON VALIDE");
        history.setDateCreation(new Date());
        history.setModule("RÉCLAMATION");
        history.setVersion("MOBILE");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new ResponseEntity<>(reclamation.getId(), HttpStatus.OK);
    }

   @RequestMapping(value = {"/demarrerReclamation"}, method = {RequestMethod.POST})
    public ResponseEntity demarrerReclamation(@RequestBody DTOForm request) throws ParseException {

        Long id = request.getId();
        Utilisateur utilisateur = userRepository.findUtilisateurById(id);

        Long reclamation_id = request.getReclamation_id();
        Reclamation reclamation = reclamationRepository.findArticleByID(reclamation_id);
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


        //**********************************************************************
        //**********************************************************************
        History history = new History();
         history.setAction("MODIFIER (M) LE STATUT DE LA RéCLAMATION : "+reclamation.getTitre()+" EN EN COURS");
        history.setDateCreation(new Date());
        history.setModule("RÉCLAMATION");
        history.setVersion("MOBILE");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new ResponseEntity<>(reclamation.getId(), HttpStatus.OK);
    }

    @RequestMapping(value = {"/deleteReclamation"}, method = {RequestMethod.POST})
    public ResponseEntity deleteReclamation(@RequestBody DTOForm request) throws ParseException {

        Long id = request.getId();
        Utilisateur utilisateur = userRepository.findUtilisateurById(id);

        Long reclamation_id = request.getReclamation_id();
        Reclamation reclamation = reclamationRepository.findArticleByID(reclamation_id);

        reclamation.setEtatSupp(true);
        reclamationRepository.save(reclamation);


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("SUPPRIMER (M) LA RéCLAMATION : "+reclamation.getTitre());
        history.setDateCreation(new Date());
        history.setModule("RÉCLAMATION");
        history.setVersion("MOBILE");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************

        return new ResponseEntity<>(reclamation.getId(), HttpStatus.OK);
    }

    @PostMapping(value = "/openReclamation")
    public ResponseEntity openReclamation(@RequestBody DTOForm request) throws ParseException {
        Long id = request.getId();
        Utilisateur utilisateur = userRepository.findUtilisateurById(id);

        Long reclamation_id = request.getReclamation_id();
        String response = request.getText();
        Reclamation reclamation = reclamationRepository.findArticleByID(reclamation_id);
        Reclamation_Statut status = reclamationStatusRepository.findStatusByID(2);

        reclamation.setStatus(status);
        reclamation.setDate_traitement(new Date());
        reclamation.setResponse(response);
        reclamationRepository.save(reclamation);

        if(status != null ){
            Reclamation_Historique_Statut histo = new Reclamation_Historique_Statut();
            histo.setReclamation(reclamation);
            histo.setDescription(response);
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


        //**********************************************************************
        //**********************************************************************
        History history = new History();
        history.setAction("RéOUVRIR LA RéCLAMATION : "+reclamation.getTitre());
        history.setDateCreation(new Date());
        history.setModule("RÉCLAMATION");
        history.setVersion("MOBILE");
        history.setUtilisateur(utilisateur);
        historyRepo.save(history);
        //**********************************************************************
        //**********************************************************************


        return new ResponseEntity<>(reclamation.getId(), HttpStatus.OK);

    }

}
