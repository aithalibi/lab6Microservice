package com.example.studentmanagement.controller;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.exception.ResourceNotFoundException;
import com.example.studentmanagement.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de la couche web avec MockMvc : verifie les codes HTTP et le JSON
 * renvoyes par les endpoints /students/* sans demarrer Tomcat ni MySQL.
 */
@WebMvcTest(StudentController.class)
class StudentControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    private static Student etudiant(int id, String nom, String prenom) {
        return new Student(id, nom, prenom, new Date(85, 8, 1));
    }

    @Test
    @DisplayName("POST /students/save renvoie 201 et le JSON de l'etudiant")
    void postSave() throws Exception {
        Student corpsEnvoye = new Student("LACHGAR", "Mohamed", new Date(85, 8, 1));
        when(studentService.save(any(Student.class))).thenReturn(etudiant(1, "LACHGAR", "Mohamed"));

        mockMvc.perform(post("/students/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(corpsEnvoye)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("LACHGAR"))
                .andExpect(jsonPath("$.prenom").value("Mohamed"))
                .andExpect(jsonPath("$.dateNaissance").value("1985-09-01"));
    }

    @Test
    @DisplayName("POST /students/save renvoie 400 quand le nom est vide")
    void postSaveValidationEchouee() throws Exception {
        Student invalide = new Student("", "Mohamed", new Date(85, 8, 1));

        mockMvc.perform(post("/students/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalide)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statut").value(400))
                .andExpect(jsonPath("$.erreurs.nom").exists());
    }

    @Test
    @DisplayName("GET /students/all renvoie 200 et la liste")
    void getAll() throws Exception {
        when(studentService.findAll()).thenReturn(Arrays.asList(etudiant(1, "A", "B"), etudiant(2, "C", "D")));

        mockMvc.perform(get("/students/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nom").value("A"));
    }

    @Test
    @DisplayName("GET /students/count renvoie 200 et le total")
    void getCount() throws Exception {
        when(studentService.countStudents()).thenReturn(3L);

        mockMvc.perform(get("/students/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(3));
    }

    @Test
    @DisplayName("GET /students/byYear renvoie les paires [annee, nombre]")
    void getByYear() throws Exception {
        Collection<Object[]> donnees = List.<Object[]>of(new Object[]{1985, 2L}, new Object[]{1990, 1L});
        when(studentService.findNbrStudentByYear()).thenReturn(donnees);

        mockMvc.perform(get("/students/byYear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0][0]").value(1985))
                .andExpect(jsonPath("$[0][1]").value(2))
                .andExpect(jsonPath("$[1][0]").value(1990))
                .andExpect(jsonPath("$[1][1]").value(1));
    }

    @Test
    @DisplayName("DELETE /students/delete/1 renvoie 204")
    void deleteExistant() throws Exception {
        when(studentService.delete(1)).thenReturn(true);

        mockMvc.perform(delete("/students/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /students/delete/999 renvoie 404")
    void deleteInexistant() throws Exception {
        when(studentService.delete(999)).thenReturn(false);

        mockMvc.perform(delete("/students/delete/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /students/{id} renvoie 200 et l'etudiant")
    void getById() throws Exception {
        when(studentService.findById(1)).thenReturn(etudiant(1, "LACHGAR", "Mohamed"));

        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("LACHGAR"));
    }

    @Test
    @DisplayName("GET /students/{id} renvoie 404 via le GlobalExceptionHandler")
    void getByIdInexistant() throws Exception {
        when(studentService.findById(anyInt())).thenThrow(new ResourceNotFoundException("Aucun etudiant avec l'id 77"));

        mockMvc.perform(get("/students/77"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statut").value(404))
                .andExpect(jsonPath("$.message").value("Aucun etudiant avec l'id 77"));
    }

    @Test
    @DisplayName("GET /students/search?nom= renvoie 200 et la liste filtree")
    void getSearch() throws Exception {
        when(studentService.findByNom(eq("LACHGAR"))).thenReturn(List.of(etudiant(1, "LACHGAR", "Mohamed")));

        mockMvc.perform(get("/students/search").param("nom", "LACHGAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("LACHGAR"));
    }

    @Test
    @DisplayName("GET /students/statistics renvoie le total et la repartition")
    void getStatistics() throws Exception {
        when(studentService.countStudents()).thenReturn(3L);
        when(studentService.findNbrStudentByYear())
                .thenReturn(List.<Object[]>of(new Object[]{1985, 2L}, new Object[]{1990, 1L}));

        mockMvc.perform(get("/students/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.parAnnee.1985").value(2))
                .andExpect(jsonPath("$.parAnnee.1990").value(1));
    }
}
