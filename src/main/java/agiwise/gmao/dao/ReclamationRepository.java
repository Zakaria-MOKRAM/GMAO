package jway.gmao.dao;

import jway.gmao.model.Reclamation;
import jway.gmao.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {

    @Query("select p from Reclamation p where p.etatSupp is ?1")
    List<Reclamation> findAllReclamations(boolean etatSupp);

     @Query("select p from Reclamation p where p.etatSupp is ?1 and p.user =?2")
    List<Reclamation> findAllReclamationsByUser(boolean etatSupp, Utilisateur user);

    @Query("select p from Reclamation p where p.id = ?1")
    Reclamation findArticleByID(long etatSupp);
}
