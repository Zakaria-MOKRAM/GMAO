package jway.gmao.service;

import jway.gmao.dao.*;
import jway.gmao.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DashboardService {

    @Autowired
    private InterventionRepository interventionRepository;

    @Autowired
    private EquipementRepository equipementRepository;

    @Autowired
    private InstallationRepository installationRepository;


    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        List<Intervention> interventions = interventionRepository.findAll();
        List<Equipement> equipements = equipementRepository.findAll();
        List<Installation> installations = installationRepository.findAll();

//        data.put("coutTotal", calculateCoutTotal(interventions));
//        data.put("pannesRecurrentes", calculatePannesRecurrentes(interventions));
//        data.put("totalInterventions", calculateTotalInterventions(interventions));
        data.put("interventionsParStatut", calculateInterventionsParStatut(interventions));
        data.put("equipementsParStatut", calculateEquipementsParStatut(equipements));
        data.put("installationsParStatut", calculateInstallationsParStatut(installations));
        return data;
    }

//    private double calculateCoutTotal(List<Intervention> interventions) {
//        // Votre logique ici
//    }
//
//    private long calculatePannesRecurrentes(List<Intervention> interventions) {
//        // Votre logique ici
//    }
//
//    private long calculateTotalInterventions(List<Intervention> interventions) {
//        // Votre logique ici
//    }

    private Map<String, Long> calculateInterventionsParStatut(List<Intervention> interventions) {
        Map<String, Long> interventionsParStatut = new HashMap<>();
        for (Intervention intervention : interventions) {
            String statut = intervention.getStatus().getLibelle();
            interventionsParStatut.put(statut, interventionsParStatut.getOrDefault(statut, 0L) + 1);
        }
        return interventionsParStatut;
    }

    private Map<String, Long> calculateEquipementsParStatut(List<Equipement> equipements) {
        Map<String, Long> equipementsParStatut = new HashMap<>();
        for (Equipement equipement : equipements) {
            String statut = equipement.getStatus().getLibelle();
            equipementsParStatut.put(statut, equipementsParStatut.getOrDefault(statut, 0L) + 1);
        }
        return equipementsParStatut;
    }

    private Map<String, Long> calculateInstallationsParStatut(List<Installation> installations) {
        Map<String, Long> installationsParStatut = new HashMap<>();
        for (Installation installation : installations) {
            String statut = installation.getStatus().getLibelle();
            installationsParStatut.put(statut, installationsParStatut.getOrDefault(statut, 0L) + 1);
        }
        return installationsParStatut;
    }
}
