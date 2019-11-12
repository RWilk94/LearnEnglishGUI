package rwilk.learnenglish.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rwilk.learnenglish.model.entity.Course;
import rwilk.learnenglish.model.entity.Lesson;
import rwilk.learnenglish.model.entity.Word;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

  List<Word> findAllByLesson_Course(Course course);

  List<Word> findAllByLesson(Lesson lesson);

}
