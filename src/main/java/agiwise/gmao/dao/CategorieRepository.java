package jway.gmao.dao;

import jway.gmao.model.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategorieRepository extends JpaRepository<Categorie, Long> {

    @Query("select p from Categorie p where p.etatSupp = ?1")
    List<Categorie> findAllCategories(boolean etatSupp);

    @Query("select p from Categorie p where p.id = ?1")
    Categorie findCategorieByID(long categorie);
}
