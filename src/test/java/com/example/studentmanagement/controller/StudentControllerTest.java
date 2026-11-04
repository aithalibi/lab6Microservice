package com.example.studentmanagement.controller;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.exception.ResourceNotFoundException;
import com.example.studentmanagement.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires du controleur (Etape 8 du TP) : JUnit 5 + Mockito.
 * Aucune base de donnees n'est sollicitee : le service est mocke.
 */
@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private static Student etudiant(int id, String nom, String prenom) {
        return new Student(id, nom, prenom, new Date(85, 8, 1));
    }

    @Test
    @DisplayName("save() renvoie 201 CREATED avec l'etudiant enregistre")
    void testSaveStudent() {
        Student aEnregistrer = new Student("Mido", "Yasmine", new Date(90, 0, 15));
        when(studentService.save(any(Student.class))).thenReturn(etudiant(1, "Mido", "Yasmine"));

        ResponseEntity<Student> response = studentController.save(aEnregistrer);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Mido", response.getBody().getNom());
        assertEquals(1, response.getBody().getId());
        verify(studentService).save(aEnregistrer);
    }

    @Test
    @DisplayName("delete() renvoie 204 NO_CONTENT si l'etudiant existe")
    void testDeleteStudent() {
        when(studentService.delete(1)).thenReturn(true);

        ResponseEntity<Void> response = studentController.delete(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(studentService).delete(1);
    }

    @Test
    @DisplayName("delete() renvoie 404 NOT_FOUND si l'etudiant est absent")
    void testDeleteStudentInexistant() {
        when(studentService.delete(99)).thenReturn(false);

        ResponseEntity<Void> response = studentController.delete(99);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("findAll() renvoie 200 OK et la liste complete")
    void testFindAllStudents() {
        when(studentService.findAll()).thenReturn(Arrays.asList(etudiant(1, "A", "B"), etudiant(2, "C", "D")));

        ResponseEntity<List<Student>> response = studentController.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    @DisplayName("countStudent() renvoie 200 OK et le nombre total")
    void testCountStudents() {
        when(studentService.countStudents()).thenReturn(10L);

        ResponseEntity<Long> response = studentController.countStudent();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10L, response.getBody());
    }

    @Test
    @DisplayName("findByYear() renvoie 200 OK et une collection vide si aucune donnee")
    void testFindByYear() {
        when(studentService.findNbrStudentByYear()).thenReturn(Collections.<Object[]>emptyList());

        ResponseEntity<Collection<?>> response = studentController.findByYear();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
    }

    @Test
    @DisplayName("findByYear() renvoie les paires [annee, nombre]")
    void testFindByYearAvecDonnees() {
        Collection<Object[]> donnees = List.<Object[]>of(new Object[]{1985, 2L}, new Object[]{1990, 1L});
        when(studentService.findNbrStudentByYear()).thenReturn(donnees);

        ResponseEntity<Collection<?>> response = studentController.findByYear();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        Object[] premiere = (Object[]) response.getBody().iterator().next();
        assertEquals(1985, premiere[0]);
        assertEquals(2L, premiere[1]);
    }

    @Test
    @DisplayName("findById() propage l'exception si l'etudiant n'existe pas")
    void testFindByIdInexistant() {
        when(studentService.findById(42)).thenThrow(new ResourceNotFoundException("Aucun etudiant avec l'id 42"));

        assertThrows(ResourceNotFoundException.class, () -> studentController.findById(42));
    }

    @Test
    @DisplayName("findById() renvoie 200 OK avec l'etudiant trouve")
    void testFindById() {
        when(studentService.findById(3)).thenReturn(etudiant(3, "LACHGAR", "Mohamed"));

        ResponseEntity<Student> response = studentController.findById(3);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("LACHGAR", response.getBody().getNom());
        assertEquals("Mohamed", response.getBody().getPrenom());
    }

    @Test
    @DisplayName("search() renvoie les etudiants correspondant au nom")
    void testSearch() {
        when(studentService.findByNom("LACHGAR")).thenReturn(List.of(etudiant(1, "LACHGAR", "Mohamed")));

        ResponseEntity<List<Student>> response = studentController.search("LACHGAR");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("LACHGAR", response.getBody().get(0).getNom());
        verify(studentService).findByNom("LACHGAR");
    }

    @Test
    @DisplayName("save() ne doit jamais appeler delete()")
    void testSaveNappellePasDelete() {
        when(studentService.save(any(Student.class))).thenReturn(etudiant(1, "X", "Y"));

        studentController.save(new Student("X", "Y", new Date()));

        verify(studentService, never()).delete(anyInt());
    }
}
