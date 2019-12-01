package rwilk.learnenglish.controller.scrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import rwilk.learnenglish.model.entity.Course;
import rwilk.learnenglish.model.entity.Lesson;
import rwilk.learnenglish.model.entity.Word;
import rwilk.learnenglish.repository.CourseRepository;
import rwilk.learnenglish.repository.LessonRepository;
import rwilk.learnenglish.repository.WordRepository;

@Component
@Slf4j
public class MemriseCourseScrapper {

  private Course course = new Course();
  private List<Lesson> lessons = new ArrayList<>();
  private List<Word> words = new ArrayList<>();

  @Autowired
  private CourseRepository courseRepository;
  @Autowired
  private LessonRepository lessonRepository;
  @Autowired
  private WordRepository wordRepository;

  public void webScrap(String link) {
    List<Lesson> lessons = new ArrayList<>();
    try {
      // String link = "https://www.memrise.com/course/1319095/angielski-1/";
      Document documentCourse = Jsoup.connect(link).timeout(6000).get();

      Elements elementLessons = documentCourse.select("a.level");
      AtomicInteger atomicInteger = new AtomicInteger(1);
      for (Element lesson : elementLessons) {
        log.info("WebScrap section " + atomicInteger.get());
        Document documentLesson = Jsoup.connect(link + atomicInteger.getAndIncrement()).timeout(6000).get();
        Elements elementWords = documentLesson.select("div.central-column").select("div.text-text");
        words = new ArrayList<>();
        for (Element elementWord : elementWords) {
          words.add(Word.builder()
              .enWord(elementWord.select("div.col_a").text())
              .plWord(elementWord.select("div.col_b").text())
              .build()
          );
        }
        lessons.add(Lesson.builder()
            .enName(lesson.select("div.level-title").text())
            .plName(lesson.select("div.level-title").text())
            .words(words)
            .build()
        );
      }
      course = Course.builder()
          .enName(documentCourse.select("h1.course-name").text())
          .plName(documentCourse.select("h1.course-name").text())
          .lessons(lessons)
          .build();

      System.out.println(course);

      Course addedCourse = courseRepository.save(course);
//      lessons.forEach(lesson -> lesson.setCourse(addedCourse));
      for (Lesson lesson : lessons) {
        lesson.setCourse(addedCourse);
        lesson = lessonRepository.save(lesson);

        for (Word word : lesson.getWords()) {
          word.setLesson(lesson);
        }
        wordRepository.saveAll(lesson.getWords());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }


  }

}
