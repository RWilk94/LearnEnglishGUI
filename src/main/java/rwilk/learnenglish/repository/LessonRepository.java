package rwilk.learnenglish.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rwilk.learnenglish.model.entity.Course;
import rwilk.learnenglish.model.entity.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

  List<Lesson> findAllByCourse(Course course);

}
