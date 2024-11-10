package jway.gmao.dao;

import jway.gmao.model.Categorie;
import jway.gmao.model.Categorie_Params;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategorieParamsRepository extends JpaRepository<Categorie_Params, Long> {

    @Query("select p from Categorie_Params p where p.categorie=?1 and p.etatSupp is ?2")
    List<Categorie_Params> findAllByCategories(Categorie cat,boolean etat);

}
