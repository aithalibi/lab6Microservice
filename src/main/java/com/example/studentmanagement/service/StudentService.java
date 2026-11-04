package com.example.studentmanagement.service;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.exception.ResourceNotFoundException;
import com.example.studentmanagement.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Couche service (Etape 5 du TP) : logique metier entre le controleur REST
 * et le repository. Le controleur n'a aucune connaissance du stockage.
 */
@Service
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Enregistre un etudiant. Si l'id est deja present, la ligne est mise a jour,
     * sinon un nouvel enregistrement est cree (comportement de JpaRepository.save).
     */
    @Transactional
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    /**
     * Supprime un etudiant par son identifiant.
     *
     * @return true si l'etudiant existait et a ete supprime, false sinon.
     */
    @Transactional
    public boolean delete(int id) {
        Optional<Student> studentOptional = Optional.ofNullable(studentRepository.findById(id));
        if (studentOptional.isPresent()) {
            studentRepository.delete(studentOptional.get());
            return true;
        }
        return false;
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public long countStudents() {
        return studentRepository.count();
    }

    /**
     * Nombre d'etudiants par annee de naissance : collection de paires [annee, nombre].
     */
    public Collection<Object[]> findNbrStudentByYear() {
        return studentRepository.findNbrStudentByYear();
    }

    public Student findById(int id) {
        Student student = studentRepository.findById(id);
        if (student == null) {
            throw new ResourceNotFoundException("Aucun etudiant avec l'id " + id);
        }
        return student;
    }

    public List<Student> findByNom(String nom) {
        return studentRepository.findByNomIgnoreCase(nom);
    }

    public List<Student> findStudentsBornAfter(int annee) {
        return studentRepository.findStudentsBornAfter(annee);
    }
}
