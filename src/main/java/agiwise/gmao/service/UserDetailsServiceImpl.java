package jway.gmao.service;


import jway.gmao.model.RoleUtilisateur;
import jway.gmao.model.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service("customUserDetailsService")
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    HttpSession session;
    @Autowired
    private AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Utilisateur u = accountService.findUserByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("Cet utilisateur" + username + " n'existe pas!");
        }

        return new User(u.getUsername(), u.getPassword(),  getGrantedAuthorities(u));
    }

    private List<GrantedAuthority> getGrantedAuthorities(Utilisateur user) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        Collection<RoleUtilisateur> roles = user.getRoles();
        for(RoleUtilisateur role:roles){
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }


        return authorities;
    }
}
