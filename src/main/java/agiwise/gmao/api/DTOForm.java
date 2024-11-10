package jway.gmao.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class DTOForm {
    private Long id;
    private Integer user_id;
    private String username;
    private String password;
    private String oldpassword;
    private String confirmedPassword;
    private String ADMIN;
    private String response;
    private String text;
    private String date;
    private String urgent;
    private Long emplacement_id;
    private Long equipement_id;
    private Long service_id;
    private Long responsable_id;
    private Long reclamation_id;

    private String libelle;
    private String titre;
    private List<Map<String, Object>> products;
    private String status;
}
