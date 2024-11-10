package jway.gmao.dao;

import jway.gmao.model.Bon_Reception;
import jway.gmao.model.Fournisseur_Article;
import jway.gmao.model.Fournisseur_Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BonReceptionRepository extends JpaRepository<Bon_Reception, Long> {

    @Query("select p from Bon_Reception p where p.etatSupp = ?1")
    List<Bon_Reception> findAllBon_Receptions(boolean etatSupp);


    @Query("select p from Bon_Reception p where p.etatSupp = ?1 and p.fournisseur =?2  order by p.date desc")
    List<Bon_Reception> findAllBon_ReceptionsByFournisseur(boolean etatSupp, Fournisseur_Article soc);


    @Query("select p from Bon_Reception p where p.id = ?1")
    Bon_Reception findBon_ReceptionByID(long etatSupp);
}
