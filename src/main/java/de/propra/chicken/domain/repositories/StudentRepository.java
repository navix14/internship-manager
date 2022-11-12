package de.propra.chicken.domain.repositories;

import de.propra.chicken.domain.model.Student;

import java.util.List;

public interface StudentRepository {
    Student findStudentById(Long id);

    void saveStudent(Student student);

    Student findStudentByGithubId(Long githubId);

    List<Student> findAlleStudenten();
}
