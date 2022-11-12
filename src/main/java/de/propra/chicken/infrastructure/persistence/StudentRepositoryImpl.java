package de.propra.chicken.infrastructure.persistence;

import de.propra.chicken.domain.model.Zeitraum;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;
import de.propra.chicken.domain.repositories.StudentRepository;
import de.propra.chicken.infrastructure.persistence.daos.StudentDao;
import de.propra.chicken.infrastructure.persistence.entities.KlausurRef;
import de.propra.chicken.infrastructure.persistence.entities.StudentEntity;
import de.propra.chicken.infrastructure.persistence.entities.UrlaubEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class StudentRepositoryImpl implements StudentRepository {
    private final StudentDao studentDao;

    public StudentRepositoryImpl(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    @Override
    public Student findStudentById(Long id) {
        Optional<StudentEntity> student = studentDao.findById(id);
        return student.map(this::toStudent).orElse(null);
    }

    @Override
    public Student findStudentByGithubId(Long githubId) {
        Optional<StudentEntity> student = studentDao.findByGithubId(githubId);
        return student.map(this::toStudent).orElse(null);
    }

    @Override
    public List<Student> findAlleStudenten() {
        return studentDao.findAll().stream().map(this::toStudent).collect(Collectors.toList());
    }

    @Override
    public void saveStudent(Student student) {
        Set<KlausurRef> klausurRefs = student.getKlausurIds().stream().map(KlausurRef::new).collect(Collectors.toSet());

        Set<UrlaubEntity> urlaube = student.getUrlaube()
                .stream()
                .map(u -> toUrlaubEntity(student.getStudentId(), u))
                .collect(Collectors.toSet());

        StudentEntity studentEntity = new StudentEntity(student.getStudentId(), student.getGithubId(),
                student.getHandle(), student.getSummeUrlaub(), klausurRefs, urlaube);

        studentDao.save(studentEntity);
    }

    private Student toStudent(StudentEntity studentEntity) {
        List<Long> klausurIds = studentEntity.getKlausurbuchungen().stream().map(KlausurRef::getKlausurId).collect(Collectors.toList());

        List<Urlaub> urlaube = studentEntity.getUrlaube()
                .stream()
                .map(this::toUrlaub)
                .collect(Collectors.toList());

        return new Student(studentEntity.getId(),
                studentEntity.getGithubId(),
                studentEntity.getHandle(),
                klausurIds, urlaube,
                studentEntity.getGebuchterUrlaub());
    }

    private Urlaub toUrlaub(UrlaubEntity urlaubEntity) {
        Zeitraum urlaubZeitraum = new Zeitraum(urlaubEntity.getStart(), urlaubEntity.getEnde());
        return new Urlaub(urlaubZeitraum, urlaubEntity.getDatum());
    }

    private UrlaubEntity toUrlaubEntity(Long studentId, Urlaub u) {
        return new UrlaubEntity(null, studentId, u.getTag(), u.getZeitraum().getStart(), u.getZeitraum().getEnde());
    }


}
