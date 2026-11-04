package com.example.studentmanagement.repository;

import com.example.studentmanagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA (Etape 4 du TP).
 * L'annotation @Repository expose l'interface comme bean Spring et permet
 * la traduction automatique des exceptions SQL en DataAccessException.
 *
 * Le repository est egalement expose par Spring Data REST sous
 * /api/students-data (starter demande dans l'enonce, detection-strategy=annotated).
 */
@Repository
@RepositoryRestResource(path = "students-data", collectionResourceRel = "students", exported = true)
public interface StudentRepository extends JpaRepository<Student, Integer> {

    /**
     * Recherche d'un etudiant par son identifiant (signature exacte de l'enonce).
     */
    Student findById(int id);

    /**
     * Variante Optional, utilisee par le service pour un traitement null-safe
     * sans surcharger findById(int).
     */
    Optional<Student> findStudentById(int id);

    /**
     * Recherche par nom (requete derivee du nom de la methode).
     */
    List<Student> findByNomIgnoreCase(String nom);

    /**
     * Test d'unicite nom + prenom (requete derivee).
     */
    boolean existsByNomIgnoreCaseAndPrenomIgnoreCase(String nom, String prenom);

    /**
     * Requete JPQL personnalisee : nombre d'etudiants par annee de naissance.
     * La projection [annee, nombre] est renvoyee dans un Object[].
     */
    @Query("SELECT YEAR(s.dateNaissance), COUNT(s) FROM Student s GROUP BY YEAR(s.dateNaissance) ORDER BY YEAR(s.dateNaissance)")
    Collection<Object[]> findNbrStudentByYear();

    /**
     * Requete JPQL personnalisee : etudiants nes apres une annee donnee.
     */
    @Query("SELECT s FROM Student s WHERE YEAR(s.dateNaissance) > :annee ORDER BY s.dateNaissance")
    List<Student> findStudentsBornAfter(@Param("annee") int annee);
}
