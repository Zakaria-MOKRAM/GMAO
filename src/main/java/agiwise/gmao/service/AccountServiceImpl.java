package jway.gmao.service;

import jway.gmao.dao.RolesRepo;
import jway.gmao.dao.UtilisateurRepo;
import jway.gmao.model.RoleUtilisateur;
import jway.gmao.model.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    @Autowired
    private UtilisateurRepo userRepository;
    @Autowired
    private RolesRepo roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Utilisateur saveUser(Utilisateur u) {
        u.setPassword(bCryptPasswordEncoder.encode(u.getPassword()));
        return userRepository.save(u);
    }

    @Override
    public RoleUtilisateur saveRole(RoleUtilisateur r) {
        return roleRepository.save(r);
    }

    @Override
    public Utilisateur findUserByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    @Override
    public Utilisateur findUserByID(long id) {
        return userRepository.findById(id);
    }

    @Override
    public void addRoleToUser(String username, Long roleId) {
        Utilisateur user = userRepository.findByUsername(username);
        RoleUtilisateur role = roleRepository.findById(roleId).get();
        Collection<RoleUtilisateur> roles = user.getRoles();
        roles.add(role);
        user.setRoles(roles);
    }

    @Override
    public List<Utilisateur> findAllUsers() {
        return userRepository.findByEtat_supp(false);
    }

    @Override
    public void removeRoleFromUser(String username, String roleName) {
        Utilisateur user = userRepository.findByUsername(username);
        RoleUtilisateur role = roleRepository.findByName(roleName);
        user.setRoles(null);
    }
}
