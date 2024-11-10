package jway.gmao.dao;

import jway.gmao.model.Contrat_Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContratTypeRepository extends JpaRepository<Contrat_Type, Long> {

    @Query("select p from Contrat_Type p where p.etatSupp is ?1")
    List<Contrat_Type> findAllTypes(boolean etatSupp);

    @Query("select p from Contrat_Type p where p.id = ?1")
    Contrat_Type findContrat_TypeByID(long etatSupp);
}
