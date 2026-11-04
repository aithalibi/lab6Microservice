package com.example.studentmanagement.service;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.exception.ResourceNotFoundException;
import com.example.studentmanagement.repository.StudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires de la couche service avec Mockito : le repository est mocke,
 * aucune connexion MySQL n'est necessaire.
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private static Student etudiant(int id, String nom) {
        return new Student(id, nom, "Prenom", new Date(85, 8, 1));
    }

    @Test
    @DisplayName("save() delegue a repository.save()")
    void testSave() {
        Student aSauver = new Student("LACHGAR", "Mohamed", new Date(85, 8, 1));
        when(studentRepository.save(aSauver)).thenReturn(etudiant(1, "LACHGAR"));

        Student resultat = studentService.save(aSauver);

        assertNotNull(resultat);
        assertEquals(1, resultat.getId());
        verify(studentRepository).save(aSauver);
    }

    @Test
    @DisplayName("delete() renvoie true et supprime l'entite si elle existe")
    void testDeleteExistant() {
        Student existant = etudiant(5, "SAFI");
        when(studentRepository.findById(5)).thenReturn(existant);

        boolean supprime = studentService.delete(5);

        assertTrue(supprime);
        verify(studentRepository).delete(existant);
    }

    @Test
    @DisplayName("delete() renvoie false et ne supprime rien si l'entite est absente")
    void testDeleteInexistant() {
        when(studentRepository.findById(404)).thenReturn(null);

        boolean supprime = studentService.delete(404);

        assertFalse(supprime);
        verify(studentRepository, never()).delete(any(Student.class));
    }

    @Test
    @DisplayName("findAll() delegue a repository.findAll()")
    void testFindAll() {
        List<Student> liste = Arrays.asList(etudiant(1, "A"), etudiant(2, "B"));
        when(studentRepository.findAll()).thenReturn(liste);

        List<Student> resultat = studentService.findAll();

        assertEquals(2, resultat.size());
        assertSame(liste, resultat);
    }

    @Test
    @DisplayName("countStudents() delegue a repository.count()")
    void testCountStudents() {
        when(studentRepository.count()).thenReturn(7L);

        assertEquals(7L, studentService.countStudents());
    }

    @Test
    @DisplayName("findNbrStudentByYear() delegue a la requete JPQL")
    void testFindNbrStudentByYear() {
        Collection<Object[]> donnees = List.<Object[]>of(new Object[]{1985, 2L});
        when(studentRepository.findNbrStudentByYear()).thenReturn(donnees);

        Collection<Object[]> resultat = studentService.findNbrStudentByYear();

        assertEquals(1, resultat.size());
        assertEquals(1985, resultat.iterator().next()[0]);
    }

    @Test
    @DisplayName("findById() leve ResourceNotFoundException si absent")
    void testFindByIdInexistant() {
        when(studentRepository.findById(11)).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> studentService.findById(11));
        assertTrue(ex.getMessage().contains("11"));
    }

    @Test
    @DisplayName("findById() renvoie l'etudiant si present")
    void testFindById() {
        Student existant = etudiant(2, "RAMI");
        when(studentRepository.findById(2)).thenReturn(existant);

        assertSame(existant, studentService.findById(2));
    }

    @Test
    @DisplayName("findByNom() delegue a la requete derivee")
    void testFindByNom() {
        when(studentRepository.findByNomIgnoreCase("SAFI")).thenReturn(List.of(etudiant(3, "SAFI")));

        List<Student> resultat = studentService.findByNom("SAFI");

        assertEquals(1, resultat.size());
        verify(studentRepository).findByNomIgnoreCase("SAFI");
    }

    @Test
    @DisplayName("save() d'un etudiant existant met a jour la ligne")
    void testSaveMiseAJour() {
        Student existant = etudiant(9, "ANCIEN");
        existant.setNom("NOUVEAU");
        when(studentRepository.save(existant)).thenReturn(existant);

        Student resultat = studentService.save(existant);

        assertEquals("NOUVEAU", resultat.getNom());
        assertEquals(9, resultat.getId());
        verify(studentRepository, never()).deleteById(anyInt());
    }
}
