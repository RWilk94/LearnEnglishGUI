package rwilk.learnenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rwilk.learnenglish.model.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

}
