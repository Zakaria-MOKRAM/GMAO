package jway.gmao.dao;

import jway.gmao.model.Forme_Juridique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FormeJuridiqueRepository extends JpaRepository<Forme_Juridique, Integer> {

    @Query("select p from Forme_Juridique p where p.etatSupp = ?1 and p.active is true")
    List<Forme_Juridique> findAllForme_Juridiques(boolean etatSupp);

    @Query("select p from Forme_Juridique p where p.etatSupp = ?1 and p.fk_pays=?2  and p.active is true order by p.libelle asc")
    List<Forme_Juridique> findAllForme_JuridiquesByPays(boolean etatSupp,int id);
    
    @Query("select p from Forme_Juridique p where p.id = ?1")
    Forme_Juridique findForme_JuridiqueByID(int etatSupp);
}
