package jway.gmao.api;

import jway.gmao.dao.*;
import jway.gmao.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/apiMobile/statistic")
public class ApiStatistiqueController {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private InstallationRepository installationRepository;
    @Autowired
    private ContratMaintenanceRepository contratMaintenanceRepository;
    @Autowired
    private EquipementRepository equipementRepository;
    @Autowired
    private InterventionRepository interventionRepository;
    @Autowired
    private InterventionTacheRepository interventionTacheRepository;
    @Autowired
    private InterventionStatutRepository interventionStatutRepository;
    @Autowired
    private InterventionTypeRepository interventionTypeRepository;
    @Autowired
    private SousTraitanceRepository sousTraitanceRepository;
    @Autowired
    private BonSortieDetailRepository bonSortieDetailRepository;
    @Autowired
    private UtilisateurRepo userRepository;
    @Autowired
    private ReclamationRepository reclamationRepository;
 @Autowired
    private ArticleStockRepository articleStockRepository;
 @Autowired
    private EntrepotRepository entrepotRepository;
@Autowired
    private ArticleRepository articleRepository;


    @GetMapping(value="/getStatistics")
    public ResponseEntity<?> getStatistics(@RequestHeader(value="header") String token) throws ParseException {
        if (userRepository.findToken(token) == null) {
            return new ResponseEntity("Vous n'avez pas le droit d'accéder à cette page", HttpStatus.UNAUTHORIZED);
        }
        List<Object> responseData = new ArrayList<Object>();
        List<Object[]> equipements_status = equipementRepository.findCountEquipementsByStatus(false);
        List<Object[]> installations_status = installationRepository.findCountInstallationsByStatus(false);
        double totalST= sousTraitanceRepository.findCoutTotalST(false) != null ? sousTraitanceRepository.findCoutTotalST(false)  : 0;
        double totalBS= bonSortieDetailRepository.findCoutTotalBS(false) != null ? bonSortieDetailRepository.findCoutTotalBS(false) : 0;
        double totalTache= interventionTacheRepository.findCoutTotal(false) != null ? interventionTacheRepository.findCoutTotal(false)  : 0;

        List<Object[]> coutEquipement= interventionRepository.findCoutByEquipement(false) ;
        List<Object[]> coutInstallation = interventionRepository.findCoutByInstallation(false) ;

        Intervention_Status termine = interventionStatutRepository.findIntervention_StatusByID(3);
        Intervention_Type preventive = interventionTypeRepository.findIntervention_TypeByID(1);
        Intervention_Type corrective = interventionTypeRepository.findIntervention_TypeByID(2);
        Intervention_Type currative = interventionTypeRepository.findIntervention_TypeByID(3);


        double totalCout= totalBS+totalST+totalTache;
        double taux_reussite_all= 0;
        double taux_reussite_preventive= 0;
        double taux_reussite_corretive= 0;
        double taux_reussite_currative= 0;
        double taux_reponse= 0;
        double temps_moyen= interventionRepository.findTempsMoyen(false);
        double temps_max= interventionRepository.findTempsMax(false);
        double panne_recurrente= interventionRepository.findRecurrentePanne(false);
        double allreclamations = reclamationRepository.findAllReclamations(false).size();

        double allIntervention = interventionRepository.findAllInterventions(false).size();
        double intervention_reussies = interventionRepository.findAllInterventionsByStatut(false,termine).size();
        double intervention_preventive_reussies = interventionRepository.findAllInterventionsByStatutAndType(false,termine,preventive).size();
        double intervention_corrective_reussies = interventionRepository.findAllInterventionsByStatutAndType(false,termine,corrective).size();
        double intervention_currative_reussies = interventionRepository.findAllInterventionsByStatutAndType(false,termine,currative).size();

        double allIntervention_preventive = interventionRepository.findAllInterventionsByType(preventive,false).size();
        double allIntervention_corrective = interventionRepository.findAllInterventionsByType(corrective,false).size();
        double allIntervention_currative = interventionRepository.findAllInterventionsByType(currative,false).size();

        if(allIntervention > 0) taux_reussite_all=(intervention_reussies/allIntervention)*100;
        if(allIntervention_preventive > 0) taux_reussite_preventive=(intervention_preventive_reussies/allIntervention_preventive)*100;
        if(allIntervention_corrective > 0) taux_reussite_corretive=(intervention_corrective_reussies/allIntervention_corrective)*100;
        if(allIntervention_currative > 0) taux_reussite_currative=(intervention_currative_reussies/allIntervention_currative)*100;

        if(allIntervention_preventive > 0) taux_reponse=(allreclamations/allIntervention_preventive)*100;


        Map<String,Double> reussies = new HashMap<>();
        reussies.put("total",intervention_reussies);reussies.put("taux",taux_reussite_all);reussies.put("all",allIntervention);
        Map<String,Double> reussies_prev = new HashMap<>();
        reussies_prev.put("total",intervention_preventive_reussies);reussies_prev.put("taux",taux_reussite_preventive);reussies_prev.put("all",allIntervention_preventive);
        Map<String,Double> reussies_corr = new HashMap<>();
        reussies_corr.put("total",intervention_corrective_reussies);reussies_corr.put("taux",taux_reussite_corretive);reussies_corr.put("all",allIntervention_corrective);
        Map<String,Double> reussies_curr = new HashMap<>();
        reussies_curr.put("total",intervention_currative_reussies);reussies_curr.put("taux",taux_reussite_currative);reussies_curr.put("all",allIntervention_currative);

        Map<String,Double> reponses = new HashMap<>();
        reponses.put("total",allreclamations);reponses.put("taux",taux_reponse);reponses.put("all",allIntervention_preventive);

        responseData.add(installations_status); //0
        responseData.add(equipements_status);//1

        responseData.add(totalCout);//2

        responseData.add(reussies); //3
        responseData.add(reussies_prev); //4
        responseData.add(reussies_corr); //5
        responseData.add(reussies_curr); //6

        responseData.add(reponses);//7
        responseData.add(temps_moyen); //8
        responseData.add(temps_max); //9
        responseData.add(panne_recurrente); //10

        responseData.add(coutEquipement); //11
        responseData.add(coutInstallation); //12

        List<Long> products = articleStockRepository.findProducts();
        List<String> product_names = articleStockRepository.findProductsNames();

        List<Entrepot> entrepots = entrepotRepository.findAllEntrepots(false);
        List<Map<String, Object>> valeurObjs = new ArrayList<>();

        for(Entrepot e : entrepots) {
            System.out.println("*************");
            System.out.println(e.getLibelle());
            Map<String, Object> productObj = new LinkedHashMap<>();
            productObj.put("name",e.getLibelle());
            double[] totales = new double[products.size()];
            int i = 0;
            for (Long product : products) {
                System.out.println("prod : "+product);
                Double amount_bd = articleStockRepository.findValeurStockByProductAndEntrepot(e.getId(),product) != null ? articleStockRepository.findValeurStockByProductAndEntrepot(e.getId(),product) : 0;
                totales[i]=amount_bd;
                i++;
            }
            productObj.put("data",totales);
            valeurObjs.add(productObj);
        }


        responseData.add(product_names);//13
        responseData.add(valeurObjs); //14

        List<Object[]> contrats_status = contratMaintenanceRepository.findByStatus(false);
        responseData.add(contrats_status); //15


        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
