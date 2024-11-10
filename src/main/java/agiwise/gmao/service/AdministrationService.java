package jway.gmao.service;

import jway.gmao.dao.MenuRepo;
import jway.gmao.dao.UserPrivilegeRepo;
import jway.gmao.model.Menu;
import jway.gmao.model.UserPrivilege;
import jway.gmao.model.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;

@Service
@Transactional
public class AdministrationService {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdfy = new SimpleDateFormat("yyyy");


    @Autowired
    private MenuRepo menuRepo;

    @Autowired
    private UserPrivilegeRepo userPrivilegeRepo;

/*

    @Autowired
    private PieceJointRepo pieceJointRepo;*/



    public List<Menu> findAllMenus(Utilisateur currentUser) {
        List<Menu> menus = menuRepo.findHeaderMenu();
        setPrivilegesRecursively(currentUser, menus);
        return menus;
    }

    private void setPrivilegesRecursively(Utilisateur currentUser, List<Menu> menus) {
        for (Menu menu : menus) {
            UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), menu.getCode());
            if (privilege != null) {
                menu.setPriv(privilege.getPrivilege());
            }

            List<Menu> children = menuRepo.findChildMenus(menu.getId());
            menu.setChildren(children);

            if (children != null && !children.isEmpty()) {
                setPrivilegesRecursively(currentUser, children);
            }
        }
    }



    public List findAllMenusChildren(Long id, Utilisateur currentUser) {
        List<Menu> children = menuRepo.findChildMenus(id);
        for (Menu child : children) {
            UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), child.getCode());
            if (privilege != null) child.setPriv(privilege.getPrivilege());

        }
        return children;
    }


    private void setPrivilegeForMenu(Utilisateur currentUser, Menu menu) {
        UserPrivilege privilege = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(currentUser.getId(), menu.getCode());
        if (privilege != null) {
            menu.setPriv(privilege.getPrivilege());
        }
    }

    public List<Menu> findAllMenusWithPrivilege(Utilisateur user) {
        List<Menu> menus = menuRepo.findHeaderMenu();
        for (Menu menu : menus) {
            List<Menu> children = menuRepo.findChildMenus(menu.getId());
            for (Menu child : children) {
                child.setPrivilege(userPrivilegeRepo.findPrivilegeByUtilisateurAndMenu(user, child));
            }
            menu.setChildren(children);
            menu.setPrivilege(userPrivilegeRepo.findPrivilegeByUtilisateurAndMenu(user, menu));
            menuRepo.save(menu);
        }
        return menus;
    }






/*    public List<PieceJoint> addPiecesJointes(String chemin_pj,String folderName,MultipartFile[] images,long objectId) {


        //===================ADD FILE =========================================//
        File folderBR = new File(chemin_pj + folderName);

        if (!folderBR.exists()) {
            folderBR.mkdirs();
        }

        String chemin = chemin_pj + folderName+"/" + objectId + "/";
        File folder_pj = new File(chemin);
        if (!folder_pj.exists()) {
            folder_pj.mkdirs();
        }

        List<PieceJoint> pieceJoints = new ArrayList<>();
        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                try {
                    File pt = new File(chemin + image.getOriginalFilename());
                    File p = pt.getParentFile();

                    if (!p.exists()) {
                        p.mkdirs();
                    }

                    String pj = image.getOriginalFilename();
                    String nom_piece = pj.substring(0, pj.lastIndexOf("."));
                    String extension = pj.substring(pj.lastIndexOf("."));
                    int m = 1;
                    while (pt.exists()) {
                        pj = nom_piece + " (" + m + ")" + extension;
                        pt = new File(chemin + pj);
                        m++;
                    }
                    image.transferTo(pt);

                    PieceJoint pieceJoint = new PieceJoint();
                    pieceJoint.setUrl(chemin);
                    pieceJoint.setFileName(pj);
                    pieceJointRepo.save(pieceJoint);

                    pieceJoints.add(pieceJoint);

                } catch (IllegalStateException | IOException e) {
                    System.out.println("IllegalStateException");
                    e.printStackTrace();
                }
            }
        }
        return pieceJoints;
    }*/


}
