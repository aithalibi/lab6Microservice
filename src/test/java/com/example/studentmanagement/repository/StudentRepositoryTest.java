package com.example.studentmanagement.repository;

import com.example.studentmanagement.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests d'integration du repository contre une vraie base MySQL/MariaDB
 * (base dediee studentdb_test, pour ne pas polluer studentdb).
 * replace = NONE : on n'utilise pas de base H2 en memoire.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:mysql://localhost:3306/studentdb_test?createDatabaseIfNotExist=true&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
        "spring.datasource.username=root",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static java.util.Date jour(String valeur) {
        try {
            return FORMAT.parse(valeur);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @BeforeEach
    void nettoyer() {
        studentRepository.deleteAll();
    }

    @Test
    @DisplayName("save() genere un identifiant automatiquement")
    void testSaveGenereId() {
        Student enregistre = studentRepository.save(new Student("LACHGAR", "Mohamed", jour("1985-09-01")));

        assertTrue(enregistre.getId() > 0);
        assertEquals(1, studentRepository.count());
    }

    @Test
    @DisplayName("findById(int) retourne l'etudiant ou null")
    void testFindById() {
        Student enregistre = studentRepository.save(new Student("SAFI", "Said", jour("1990-03-12")));

        Student trouve = studentRepository.findById(enregistre.getId());
        assertNotNull(trouve);
        assertEquals("SAFI", trouve.getNom());

        assertNull(studentRepository.findById(999_999));
    }

    @Test
    @DisplayName("findStudentById() retourne un Optional")
    void testFindStudentByIdOptional() {
        Student enregistre = studentRepository.save(new Student("RAMI", "Salima", jour("1975-09-03")));

        assertTrue(studentRepository.findStudentById(enregistre.getId()).isPresent());
        assertTrue(studentRepository.findStudentById(999_999).isEmpty());
    }

    @Test
    @DisplayName("findAll() retourne tous les etudiants")
    void testFindAll() {
        studentRepository.save(new Student("A", "A1", jour("1985-01-01")));
        studentRepository.save(new Student("B", "B1", jour("1990-01-01")));

        List<Student> tous = studentRepository.findAll();
        assertEquals(2, tous.size());
    }

    @Test
    @DisplayName("findNbrStudentByYear() regroupe par annee de naissance")
    void testFindNbrStudentByYear() {
        studentRepository.save(new Student("UN", "A", jour("1985-09-01")));
        studentRepository.save(new Student("DEUX", "B", jour("1985-12-24")));
        studentRepository.save(new Student("TROIS", "C", jour("1990-06-15")));

        Collection<Object[]> resultat = studentRepository.findNbrStudentByYear();

        assertEquals(2, resultat.size());
        Object[] premiere = resultat.iterator().next();
        assertEquals(1985, ((Number) premiere[0]).intValue());
        assertEquals(2L, ((Number) premiere[1]).longValue());
    }

    @Test
    @DisplayName("findByNomIgnoreCase() ignore la casse")
    void testFindByNomIgnoreCase() {
        studentRepository.save(new Student("LACHGAR", "Mohamed", jour("1985-09-01")));

        assertEquals(1, studentRepository.findByNomIgnoreCase("lachgar").size());
        assertEquals(0, studentRepository.findByNomIgnoreCase("INCONNU").size());
    }

    @Test
    @DisplayName("existsByNomIgnoreCaseAndPrenomIgnoreCase() detecte les doublons")
    void testExistsByNomAndPrenom() {
        studentRepository.save(new Student("LACHGAR", "Mohamed", jour("1985-09-01")));

        assertTrue(studentRepository.existsByNomIgnoreCaseAndPrenomIgnoreCase("lachgar", "mohamed"));
        assertFalse(studentRepository.existsByNomIgnoreCaseAndPrenomIgnoreCase("LACHGAR", "Autre"));
    }

    @Test
    @DisplayName("findStudentsBornAfter() filtre par annee")
    void testFindStudentsBornAfter() {
        studentRepository.save(new Student("VRAI", "A", jour("1990-06-15")));
        studentRepository.save(new Student("FAUX", "B", jour("1985-09-01")));

        List<Student> recents = studentRepository.findStudentsBornAfter(1985);
        assertEquals(1, recents.size());
        assertEquals("VRAI", recents.get(0).getNom());
    }

    @Test
    @DisplayName("delete() supprime bien la ligne")
    void testDelete() {
        Student enregistre = studentRepository.save(new Student("TEMP", "Temp", jour("1985-09-01")));
        int id = enregistre.getId();

        studentRepository.delete(enregistre);

        assertNull(studentRepository.findById(id));
        assertEquals(0, studentRepository.count());
    }
}
