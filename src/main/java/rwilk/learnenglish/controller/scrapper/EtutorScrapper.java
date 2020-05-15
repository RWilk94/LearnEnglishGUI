package rwilk.learnenglish.controller.scrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import rwilk.learnenglish.model.entity.Sentence;
import rwilk.learnenglish.model.entity.Word;
import rwilk.learnenglish.repository.CourseRepository;
import rwilk.learnenglish.repository.LessonRepository;
import rwilk.learnenglish.repository.SentenceRepository;
import rwilk.learnenglish.repository.WordRepository;

@Slf4j
@Component
public class EtutorScrapper {

  @Autowired
  private CourseRepository courseRepository;
  @Autowired
  private LessonRepository lessonRepository;
  @Autowired
  private WordRepository wordRepository;
  @Autowired
  private SentenceRepository sentenceRepository;

//  public static void main(String[] args) {
//    EtutorScrapper scrapper = new EtutorScrapper();
//    try {
//      scrapper.webScrap();
//    } catch (IOException ex) {
//      ex.printStackTrace();
//    }
//  }

  public void webScrap() throws IOException {
    final String USER_AGENT = "\"Mozilla/5.0 (Windows NT\" +\n" +
        "          \" 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2\"";

    HashMap<String, String> cookies = new HashMap<>();
    cookies.put("autoLoginToken", "grV2psyY29hby5BWEb9Wskb0hWbDu7uqiXOV2RGb");
//    cookies.put("LiveHelpSession",
//        "893fc4650913a5569361ec2cb5e472d11d999c5992866534219f2f10recykWAbRu9p6syOpwgpmJZ4yAnmjYYlytp+NrOKrcuxBh8r5ll0eE07+1tUhd8p");

    Document document = Jsoup.connect("https://www.etutor.pl/words").cookies(cookies).timeout(30000).get();
    Elements titleElements = document.select("div.words-box-title");
    Elements contentElements = document.select("ul.baseItemList");

    if (contentElements.size() != 32) {
      throw new RuntimeException();
    }

    for (int i = 0; i < contentElements.size(); i++) {
      Course course = courseRepository.save(
          Course.builder()
              .enName("ETUTOR " + titleElements.get(i).select("a").text().trim())
              .plName("ETUTOR " + titleElements.get(i).select("a").text().trim())
              .order(1000)
              .build());
      log.info("Course {} was added.", course.getEnName());

      Elements lessons = contentElements.get(i).select("li"); // lekcje w kursie

      for (Element lessonElement : lessons) {
        lessonElement.text(); // nazwa leckji // A1 1000 angielskich słówek - 01 - Podstawowe zwroty 20 elementów 10%
        String url = lessonElement.select("a").get(1).attr("href"); // /words/177022

        Lesson lesson = lessonRepository.save(
            Lesson.builder()
                .enName(lessonElement.text().trim())
                .plName(lessonElement.text().trim())
                .course(course)
                .order(1000)
                .build());
        log.info("Lesson {} was added.", lesson.getEnName());

        Document courseDocument = Jsoup.connect("https://www.etutor.pl" + url).cookies(cookies).timeout(10000).get();
        // Document courseDocument = Jsoup.connect("https://www.etutor.pl/words/learn/161).cookies(cookies).timeout(10000).get();
        // List<Word> words = new ArrayList<>();

        Elements elements = courseDocument.select("div.wordListElementScreen");
        for (Element element : elements) {
          Elements lis = element.select("ul.sentencesul").select("li");
          List<Sentence> sentences = new ArrayList<>();
          for (Element li : lis) {
            String text = li.text();
            try {
              if (li.text().contains("Dodaj notatkę Zapisz Anuluj")) {
                String enSentence = text.substring(0, li.text().indexOf("Dodaj notatkę Zapisz Anuluj")).trim();
                String plSentence =
                    text.substring(li.text().indexOf("Dodaj notatkę Zapisz Anuluj")).replace("Dodaj notatkę Zapisz Anuluj", "").trim();

                Sentence sentence = Sentence.builder()
                    .enSentence(enSentence)
                    .plSentence(plSentence)
                    .build();
                sentences.add(sentence);
              }
            } catch (Exception e) {
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

        //        for (Word word : words) {
        //          System.out.println(word.toString());
        //        }

      }
    }

    //    Document course = Jsoup.connect("https://www.etutor.pl/words").cookies(cookies).timeout(10000).get();
    // Document course = Jsoup.connect("https://www.etutor.pl/words/learn/177022").cookies(cookies).timeout(10000).get();

    // course.select("div.wordListElementScreen").get(0).select("span.hw").text(); // text EN
    // course.select("div.wordListElementScreen").get(0).select("p.phraseExplanation").text(); // text PL // tak Dodaj notatkę Zapisz Anuluj

    // course.select("div.wordListElementScreen").get(2).select("ul.sentencesul").select("li").get(0).text(); // tutaj jest text zdań
    // Sit down, please. Dodaj notatkę Zapisz Anuluj Usiądź, proszę.

    // Elements elements = course.select("div.basescolumnslist");

    // List<String> names = new ArrayList<>();

    //    for (Element element : elements) {
    //      names.add(element.select("div.words-box-title").get(0).text());
    //      names.add(element.select("div.words-box").get(0).select("li").get(0).text());
    //      // tutaj mam nazwy kursów, ale bez linków
    //    }

    // course.select("div.loginFormInputLine").get(0).child(0).appendText("login");
    // course.select("div.loginFormInputLine").get(1).child(0).appendText("password");
    // course.select("button.buttonPrimaryStyle").get(0).click();
  }

}
