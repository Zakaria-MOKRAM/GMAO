package jway.gmao.dao;

import jway.gmao.model.Attribute_Dynamique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AttributesRepository extends JpaRepository<Attribute_Dynamique, Long> {

    @Query("select p from Attribute_Dynamique p")
    List<Attribute_Dynamique> findAllAttribute_Dynamique();

    @Query("select p from Attribute_Dynamique p where p.id = ?1")
    Attribute_Dynamique findAttribute_DynamiqueByID(long id);
}
