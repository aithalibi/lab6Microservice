package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.StudentStatistics;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controleur REST (Etape 5 du TP) : expose les endpoints /students/*.
 * Aucune logique metier ici : le controleur recoit la requete, appelle le
 * service et construit la reponse HTTP via ResponseEntity.
 *
 * Les annotations @Operation / @ApiResponse documentent l'API dans Swagger UI
 * (Etape 9 du TP).
 */
@RestController
@RequestMapping("/students")
@Tag(name = "Etudiants", description = "Gestion des etudiants (CRUD, comptage, statistiques par annee)")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(summary = "Enregistrer un etudiant",
            description = "Cree un nouvel etudiant ou met a jour celui dont l'id est fourni.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Etudiant enregistre"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/save")
    public ResponseEntity<Student> save(@Valid @RequestBody Student student) {
        Student savedStudent = studentService.save(student);
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }

    @Operation(summary = "Supprimer un etudiant par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Etudiant supprime"),
            @ApiResponse(responseCode = "404", description = "Etudiant inexistant", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifiant de l'etudiant", example = "1")
            @PathVariable("id") int id) {
        boolean deleted = studentService.delete(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Recuperer tous les etudiants")
    @ApiResponse(responseCode = "200", description = "Liste complete des etudiants")
    @GetMapping("/all")
    public ResponseEntity<List<Student>> findAll() {
        List<Student> students = studentService.findAll();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @Operation(summary = "Nombre total d'etudiants")
    @ApiResponse(responseCode = "200", description = "Nombre total d'etudiants enregistres")
    @GetMapping("/count")
    public ResponseEntity<Long> countStudent() {
        long count = studentService.countStudents();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @Operation(summary = "Nombre d'etudiants par annee de naissance",
            description = "Renvoie une collection de paires [annee, nombre], par ex. [[1985, 2], [1990, 1]].")
    @ApiResponse(responseCode = "200", description = "Repartition par annee de naissance")
    @GetMapping("/byYear")
    public ResponseEntity<Collection<?>> findByYear() {
        Collection<?> studentsByYear = studentService.findNbrStudentByYear();
        return new ResponseEntity<>(studentsByYear, HttpStatus.OK);
    }

    @Operation(summary = "Recuperer un etudiant par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Etudiant trouve"),
            @ApiResponse(responseCode = "404", description = "Etudiant inexistant", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Student> findById(
            @Parameter(description = "Identifiant de l'etudiant", example = "1")
            @PathVariable("id") int id) {
        return new ResponseEntity<>(studentService.findById(id), HttpStatus.OK);
    }

    @Operation(summary = "Rechercher des etudiants par nom")
    @ApiResponse(responseCode = "200", description = "Etudiants dont le nom correspond")
    @GetMapping("/search")
    public ResponseEntity<List<Student>> search(
            @Parameter(description = "Nom recherche (insensible a la casse)", example = "LACHGAR")
            @RequestParam("nom") String nom) {
        return new ResponseEntity<>(studentService.findByNom(nom), HttpStatus.OK);
    }

    @Operation(summary = "Etudiants nes apres une annee donnee")
    @ApiResponse(responseCode = "200", description = "Etudiants nes apres l'annee indiquee")
    @GetMapping("/bornAfter")
    public ResponseEntity<List<Student>> bornAfter(
            @Parameter(description = "Annee de reference", example = "1985")
            @RequestParam("annee") int annee) {
        return new ResponseEntity<>(studentService.findStudentsBornAfter(annee), HttpStatus.OK);
    }

    @Operation(summary = "Statistiques globales",
            description = "Nombre total d'etudiants et repartition par annee, dans un seul objet JSON.")
    @ApiResponse(responseCode = "200", description = "Statistiques calculees")
    @GetMapping("/statistics")
    public ResponseEntity<StudentStatistics> statistics() {
        Collection<Object[]> parAnnee = studentService.findNbrStudentByYear();
        Map<Integer, Long> repartition = new LinkedHashMap<>();
        for (Object[] ligne : parAnnee) {
            repartition.put(((Number) ligne[0]).intValue(), ((Number) ligne[1]).longValue());
        }
        StudentStatistics statistiques = new StudentStatistics(studentService.countStudents(), repartition);
        return new ResponseEntity<>(statistiques, HttpStatus.OK);
    }
}
