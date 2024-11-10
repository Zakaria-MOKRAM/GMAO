package jway.gmao.api;

import jway.gmao.dao.HistoryRepo;
import jway.gmao.dao.RolesRepo;
import jway.gmao.dao.UserPrivilegeRepo;
import jway.gmao.dao.UtilisateurRepo;
import jway.gmao.model.*;
import jway.gmao.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@CrossOrigin("*")
@RequestMapping("apiMobile")
public class DefaultApiController {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RolesRepo rolesRepo;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserPrivilegeRepo userPrivilegeRepo;

    @Autowired
    private UtilisateurRepo userRepository;
    @Autowired
    private HistoryRepo historique_actionsRepository;


    @GetMapping("/ping")
    public ResponseEntity ping() {
        return new ResponseEntity<>("connected successfully", HttpStatus.OK);
    }
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody DTOForm authenticationRequest) {
        Utilisateur appUser = userRepository.findByUsername(authenticationRequest.getUsername());
        if (appUser == null) {
            return new ResponseEntity("Ce compte n'existe pas. Merci de réessayer avec un autre", HttpStatus.BAD_REQUEST);
        }
        if (!bCryptPasswordEncoder.matches(authenticationRequest.getPassword(),appUser.getPassword())) {
            return new ResponseEntity("Mot de passe est incorrect. Merci de réessayer", HttpStatus.BAD_REQUEST);
        }

        final String token = jwtTokenUtil.generateToken(appUser);
        appUser.setToken(token);

        boolean isAdmin=false;
        List<String> roles = new ArrayList<>();

        RoleUtilisateur admin= rolesRepo.finRoleAdmin();
        if(appUser.getRoles() != null && appUser.getRoles().contains(admin)) {
            isAdmin=true;
            roles.add("ADMIN");
        }
        List<String> codes = userPrivilegeRepo.findAssociatedCodes(appUser);

        List<Map<String,Long>> privileges = new ArrayList<>();
        List<Menu> menus = userPrivilegeRepo.findMenusAffectedToUtilisateur(appUser);
        for(Menu menu : menus){
            Map<String,Long> privilege = new HashMap<>();
            long priv =1 ; //NON AUTORISÉ
            UserPrivilege p = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(appUser.getId(), menu.getCode());
            if(p != null) priv=p.getPrivilege().getId();
            privilege.put(menu.getCode(),priv);
            privileges.add(privilege);
        }

        appUser.setCodes(codes);
        appUser.setRoles_(roles);
        appUser.setPrivileges(privileges);
        appUser.setAdmin(isAdmin);

        userRepository.save(appUser);

        return new ResponseEntity<>(appUser, HttpStatus.OK);
    }


    @GetMapping(value="/getAllUsers")
    public ResponseEntity<?> getAllUsers(@RequestHeader(value="header") String header) {
        
        if (userRepository.findToken(header) == null) {
            return new ResponseEntity("Vous n'avez pas le droit d'accéder à cette page", HttpStatus.UNAUTHORIZED);
        }
        List<Object> responseData = new ArrayList<Object>();

        List<Utilisateur> list = userRepository.findAll();
        responseData.add(list);

        return new ResponseEntity<>(responseData, HttpStatus.OK);

    }
    @GetMapping(value="/getUserById/{id}")
    public ResponseEntity<?> getUserByID(@PathVariable(value="id") long id,@RequestHeader(value="header") String header) {
        
        if (userRepository.findToken(header) == null) {
            return new ResponseEntity("Vous n'avez pas le droit d'accéder à cette page", HttpStatus.UNAUTHORIZED);
        }
        Utilisateur user = userRepository.findById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @GetMapping(value = "/getAllUsernames")
    public ResponseEntity<?> getAllUsernames(@RequestHeader(value = "header") String header) throws ParseException {
        
        if (userRepository.findToken(header) == null) {
            return new ResponseEntity("Vous n'avez pas le droit d'accéder à cette page", HttpStatus.UNAUTHORIZED);
        }
        List<String> list = userRepository.findUsernames();
        return new ResponseEntity<>(list, HttpStatus.OK);

    }
    @GetMapping(value="/getAssociatedCodes/{id}")
    public ResponseEntity<?> getAssociatedCodes(@RequestHeader(value="header") String header,@PathVariable(name = "id") int id) throws ParseException {
        if(userRepository.findToken(header) == null){
            return new ResponseEntity("Vous n'avez pas le droit d'accéder à cette page", HttpStatus.UNAUTHORIZED);
        }
        Utilisateur appUser = userRepository.findById(id);
        List<String> codes = userPrivilegeRepo.findAssociatedCodes(appUser);

        return new ResponseEntity<>(codes, HttpStatus.OK);
    }



    @GetMapping(value = "/getHisotriqueActionOptions")
    public ResponseEntity<?> getHisotriqueActionOptions(@RequestHeader(value = "header") String header) throws ParseException {
        
        if (userRepository.findToken(header) == null) {
            return new ResponseEntity("Vous n'avez pas le droit d'accéder à cette page", HttpStatus.UNAUTHORIZED);
        }

        List<Object> dataResponses = new ArrayList<>();
        List<String> modules = historique_actionsRepository.findModules();
        List<Utilisateur> users = historique_actionsRepository.findUsers();

        dataResponses.add(modules);
        dataResponses.add(users);

        return new ResponseEntity<>(dataResponses, HttpStatus.OK);

    }
    @GetMapping(value = "/getHisotriqueActionBy")
    public ResponseEntity<?> getHisotriqueActionBy(@RequestHeader(value = "header") String header,@RequestParam(value = "user_id") String user_id,@RequestParam(value = "module") String module) throws ParseException {
        
        if (userRepository.findToken(header) == null) {
            return new ResponseEntity("Vous n'avez pas le droit d'accéder à cette page", HttpStatus.UNAUTHORIZED);
        }

        SimpleDateFormat sdateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date to = sdateformat.parse(sdateformat.format(new Date()));
        Calendar c = Calendar.getInstance();
        c.setTime(to);
        c.add(Calendar.HOUR, 23);
        c.add(Calendar.MINUTE, 59);
        to = c.getTime();

        c.setTime(to);
        c.add(Calendar.DATE, -15);
        Date from = c.getTime();

        long id_user = (user_id != null && !user_id.equals("")) ? Long.parseLong(user_id) : -1;
        String modul = (module != null && !module.equals("")) ? module : null;



        List<History> logs = historique_actionsRepository.getTOP100(from, to,modul,id_user);
        return new ResponseEntity<>(logs, HttpStatus.OK);

    }
    @GetMapping(value="/getHisotriqueAction")
    public ResponseEntity<?> getHisotriqueAction (@RequestHeader(value="header") String header) throws ParseException {
        
        if (userRepository.findToken(header) == null) {
            return new ResponseEntity("Vous n'avez pas le droit d'accéder à cette page", HttpStatus.UNAUTHORIZED);
        }

        SimpleDateFormat sdateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date to = sdateformat.parse(sdateformat.format(new Date()));
        Calendar c = Calendar.getInstance();
        c.setTime(to);
        c.add(Calendar.HOUR, 23);
        c.add(Calendar.MINUTE, 59);
        to = c.getTime();

        c.setTime(to);
        c.add(Calendar.DATE, -15);
        Date from = c.getTime();

        List<History> logs = historique_actionsRepository.getTOP100(from,to);
        return new ResponseEntity<>(logs, HttpStatus.OK);

    }
    @PutMapping("/deletehistory/{id}")
    public ResponseEntity deletehistory(@PathVariable(name = "id") long id,@RequestBody DTOForm userForm) throws ParseException {

        Utilisateur admin = userRepository.findUtilisateurById(id);
        History historique = historique_actionsRepository.findByID(userForm.getId());
        historique_actionsRepository.delete(historique);
        SimpleDateFormat sdateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date to = sdateformat.parse(sdateformat.format(new Date()));
        Calendar c = Calendar.getInstance();
        c.setTime(to);
        c.add(Calendar.HOUR, 23);
        c.add(Calendar.MINUTE, 59);
        to = c.getTime();

        c.setTime(to);
        c.add(Calendar.DATE, -15);
        Date from = c.getTime();

        List<History> logs = historique_actionsRepository.getTOP100(from,to);

        return new ResponseEntity<>(logs, HttpStatus.OK);
    }
    @PostMapping("/saveHistorique")
    public ResponseEntity saveHistorique(@RequestBody DTOForm userForm) {

        Utilisateur user = userRepository.findUtilisateurById(userForm.getId());

        /***************************************
         * HISTORIQUE ACTIONS
         *********************************/
        History histo = new History();
        histo.setDateCreation(new Date());
        histo.setModule("MOBILE");
        histo.setUtilisateur(user);
        histo.setAction(userForm.getText());
        historique_actionsRepository.save(histo);

        /********************************************************************************************/

        return null;
    }
}
