package rwilk.learnenglish.controller.scrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import rwilk.learnenglish.model.entity.Course;
import rwilk.learnenglish.model.entity.Lesson;
import rwilk.learnenglish.model.entity.Sentence;
import rwilk.learnenglish.model.entity.Word;
import rwilk.learnenglish.repository.CourseRepository;
import rwilk.learnenglish.repository.LessonRepository;
import rwilk.learnenglish.repository.SentenceRepository;
import rwilk.learnenglish.repository.WordRepository;

@SuppressWarnings("Duplicates")
@Slf4j
@Component
public class EtutorScrapper2 {

  @Autowired
  private CourseRepository courseRepository;
  @Autowired
  private LessonRepository lessonRepository;
  @Autowired
  private WordRepository wordRepository;
  @Autowired
  private SentenceRepository sentenceRepository;

//  public static void main(String[] args) {
//    EtutorScrapper2 scrapper = new EtutorScrapper2();
//    try {
//      scrapper.webScrap();
//    } catch (IOException ex) {
//      ex.printStackTrace();
//    }
//  }

  public void webScrap() throws IOException {
    HashMap<String, String> cookies = new HashMap<>();
    cookies.put("autoLoginToken", "Wt8bpASp84XmYDz0wRAcjUn8HU5QylhRRXKgem95");
    cookies.put("LiveHelpSession",
        "893fc4650913a5569361ec2cb5e472d11d999c5992866534219f2f10recykWAbRu9p6syOpwgpmJZ4yAnmjYYlytp+NrOKrcuxBh8r5ll0eE07+1tUhd8p");

    Course course = courseRepository.findById(13L).get();

    Lesson lesson = lessonRepository.save(
        Lesson.builder()
            .enName("Nazwy geograficzne")
            .plName("Nazwy geograficzne")
            .course(course)
            .build());

    log.info("Lesson {} was added.", lesson.getEnName());

    //        Document courseDocument = Jsoup.connect("https://www.etutor.pl" + url).cookies(cookies).timeout(10000).get();
    Document courseDocument = Jsoup.connect("https://www.etutor.pl/words/learn/161").cookies(cookies).timeout(10000).get();
    // List<Word> words = new ArrayList<>();

    Elements elements = courseDocument.select("div.wordListElementScreen");
    for (Element element : elements) {
      Elements lis = element.select("ul.sentencesul").select("li");
      List<Sentence> sentences = new ArrayList<>();
      for (Element li : lis) {
        String text = li.text();
        try {
          Sentence sentence = new Sentence();
          if (text.contains("Dodaj notatkę Zapisz Anuluj")) {
            String enSentence = text.substring(0, li.text().indexOf("Dodaj notatkę Zapisz Anuluj")).trim();
            String plSentence =
                text.substring(li.text().indexOf("Dodaj notatkę Zapisz Anuluj")).replace("Dodaj notatkę Zapisz Anuluj", "").trim();
            sentence = Sentence.builder()
                .enSentence(enSentence)
                .plSentence(plSentence)
                .build();
          } else {
            sentence = Sentence.builder()
                .enSentence(text)
                .plSentence(text)
                .build();
          }

          sentences.add(sentence);
        } catch (StringIndexOutOfBoundsException e) {
          System.out.println(text);
          e.printStackTrace();
        }
      }

      // System.out.println();
      Word word = wordRepository.save(
          Word.builder()
              .enWord(element.select("span.hw").text())
              .plWord(element.select("p.phraseExplanation").text().replace("Dodaj notatkę Zapisz Anuluj", "").trim())
              .lesson(lesson)
              .order(1000)
              .nextRepeat(0L)
              .build());
      // words.add(word);
      for (Sentence sentence : sentences) {
        sentence.setWord(word);
        sentenceRepository.save(sentence);
      }
    }
  }

}
