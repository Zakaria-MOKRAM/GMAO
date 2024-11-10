package jway.gmao.service;


import jway.gmao.model.RoleUtilisateur;
import jway.gmao.model.Utilisateur;

import java.util.List;


public interface AccountService {
    public Utilisateur saveUser(Utilisateur user);
    public RoleUtilisateur saveRole(RoleUtilisateur role);
    public void addRoleToUser(String username,Long roleId);
    public void removeRoleFromUser(String username,String roleName);
    public Utilisateur findUserByUsername(String username);
    public Utilisateur findUserByID(long id);
    public List<Utilisateur> findAllUsers();
}
