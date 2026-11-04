package com.example.studentmanagement.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.util.Date;
import java.util.Objects;

/**
 * Entite Student (Etape 4 du TP).
 * Chaque instance correspond a une ligne de la table "student" generee par Hibernate.
 */
@Entity
@Table(name = "student")
@Schema(description = "Etudiant enregistre en base (table student)")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant auto-incremente", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private int id;

    @NotBlank(message = "Le nom est obligatoire")
    @Schema(description = "Nom de famille", example = "LACHGAR", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nom;

    @NotBlank(message = "Le prenom est obligatoire")
    @Schema(description = "Prenom", example = "Mohamed", requiredMode = Schema.RequiredMode.REQUIRED)
    private String prenom;

    @Temporal(TemporalType.DATE)
    @Past(message = "La date de naissance doit etre dans le passe")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Paris")
    @Schema(description = "Date de naissance", example = "1985-09-01", type = "string", format = "date")
    private Date dateNaissance;

    public Student() {
    }

    public Student(String nom, String prenom, Date dateNaissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
    }

    public Student(int id, String nom, String prenom, Date dateNaissance) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Student)) {
            return false;
        }
        Student other = (Student) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", nom='" + nom + "', prenom='" + prenom
                + "', dateNaissance=" + dateNaissance + '}';
    }
}
