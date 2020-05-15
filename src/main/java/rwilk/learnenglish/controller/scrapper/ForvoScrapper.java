package rwilk.learnenglish.controller.scrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import rwilk.learnenglish.model.Forvo;
import rwilk.learnenglish.model.entity.Word;

@SuppressWarnings("ALL")
@Component
@Slf4j
public class ForvoScrapper {

  private static final String PHPSESSID = "430oi658sd40m5dns822hchak6";
  private String regex = "[^a-zA-Z0-9 ]";
  @Getter
  private List<String> errors = new ArrayList<>();

//  public static void main(String[] args) {
//    ForvoScrapper forvoScrapper = new ForvoScrapper();
//    try {
//      forvoScrapper.webScrap(Word.builder().enWord("post office").partOfSpeech("rzeczownik").build());
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }

  public void webScrap(Word word) throws IOException {
    String enWord = new String(word.getEnWord());
    enWord = enWord.replaceAll(regex, "");

    if (word.getPartOfSpeech().equals("czasownik")) {
      enWord = enWord.substring(2).trim();
    }
    if (enWord.contains(" ")) {
      enWord = enWord.replaceAll(" ", "_");
    }
    try {
      List<Forvo> forvos = webScrapPhrase(enWord);
      if (forvos.size() > 0) {
        Map<String, List<Forvo>> forvoMap = forvos.stream().collect(Collectors.groupingBy(Forvo::getLanguageVersion));
        List<Forvo> us = forvoMap.get("amerykański");
        List<Forvo> uk = forvoMap.get("brytyjski");

        if (us.size() > 0) {
          downloadPhrase(us.stream().sorted(Comparator.comparing(Forvo::getRate).reversed()).collect(Collectors.toList()).get(0),
              word.getId());
        } else {
          errors.add(word.getId() + " \t" + enWord);
          log.error("Not found US {}", enWord);
        }
        if (uk.size() > 0) {
          downloadPhrase(uk.stream().sorted(Comparator.comparing(Forvo::getRate).reversed()).collect(Collectors.toList()).get(0),
              word.getId());
        } else {
          errors.add(word.getId() + " \t" + enWord);
          log.error("Not found UK {}", enWord);
        }
      }
    } catch (Exception ex) {
      errors.add(word.getId() + " \t" + enWord);
      ex.printStackTrace();
    }
    try {
      List<Forvo> forvos = webScrapWord(enWord);

      if (forvos.size() > 0) {
        Map<String, List<Forvo>> forvoMap = forvos.stream().collect(Collectors.groupingBy(Forvo::getLanguageVersion));
        List<Forvo> us = forvoMap.get("amerykański");
        List<Forvo> uk = forvoMap.get("brytyjski");

        if (us != null && us.size() > 0) {
          downloadWord(us.stream().sorted(Comparator.comparing(Forvo::getRate).reversed()).collect(Collectors.toList()).get(0),
              word.getId());
        } else {
          errors.add(word.getId() + " \t" + enWord);
          log.error("Not found US {}", enWord);
        }
        if (uk != null && uk.size() > 0) {
          downloadWord(uk.stream().sorted(Comparator.comparing(Forvo::getRate).reversed()).collect(Collectors.toList()).get(0),
              word.getId());
        } else {
          errors.add(word.getId() + " \t" + enWord);
          log.error("Not found UK {}", enWord);
        }
      }
    } catch (HttpStatusException exc) {
      errors.add(word.getId() + " \t" + enWord);
      exc.printStackTrace();
    }

  }

  public List<Forvo> webScrapWord(String word) throws IOException {
    log.warn("START WEBSCRAP WORD {}", word);
    HashMap<String, String> cookies = new HashMap<>();
    cookies.put("PHPSESSID", PHPSESSID);

    Document document = Jsoup.connect("https://pl.forvo.com/word/" + word + "/").cookies(cookies).timeout(10000).get();
    Elements elements = document.select("ul.show-all-pronunciations");
    if (elements.size() > 0) {
      Element pronunciationsElement = elements.get(0);

      String englishVersion = "";
      List<Forvo> forvos = new ArrayList<>();

      for (Element singlePronunciations : pronunciationsElement.children()) {
        if (!singlePronunciations.equals(" ")) {
          if (singlePronunciations.tag().toString().equals("header")) {
            englishVersion = singlePronunciations.text();
          } else {

            long rate = 0L;
            try {
              rate = Long.valueOf(singlePronunciations.select("span.num_votes").text()
                  .substring(singlePronunciations.select("span.num_votes").text().indexOf("Głosy: ") + 7));
            } catch (Exception e) {
            }

            forvos.add(Forvo.builder()
                .languageVersion(englishVersion)
                .word(singlePronunciations.select("p.download").select("span.ofLink").attr("data-p2"))
                .language(singlePronunciations.select("p.download").select("span.ofLink").attr("data-p3"))
                .id(singlePronunciations.select("p.download").select("span.ofLink").attr("data-p4"))
                .rate(rate)
                .build());
          }
        }
      }
      log.warn("END WEBSCRAP {}. FOUND {}", word, forvos.size());
      return forvos;
    }
    return new ArrayList<>();
  }

  public List<Forvo> webScrapPhrase(String word) throws IOException {
    log.warn("START WEBSCRAP PHRASE {}", word);
    HashMap<String, String> cookies = new HashMap<>();
    cookies.put("PHPSESSID", PHPSESSID);

    Document document = Jsoup.connect("https://pl.forvo.com/phrase/" + word + "/").cookies(cookies).timeout(10000).get();
    Elements elements = document.select("article.pronunciations").select("ul").get(0).children();

    if (elements.size() > 0) {
      String englishVersion = "";
      List<Forvo> forvos = new ArrayList<>();

      for (Element element : elements) {
        if (element.tag().toString().equals("header")) {
          englishVersion = element.text();
        } else {
          long rate = 0L;
          try {
            rate = Long.valueOf(element.select("span.num_votes").text()
                .substring(element.select("span.num_votes").text().indexOf("Głosy: ") + 7));
          } catch (Exception e) {
          }
          forvos.add(Forvo.builder()
              .languageVersion(englishVersion)
              .word(element.select("p.download").select("span.ofLink").attr("data-p2"))
              .language(element.select("p.download").select("span.ofLink").attr("data-p3"))
              .id(element.select("p.download").select("span.ofLink").attr("data-p4"))
              .rate(rate)
              .build());
        }
      }
      log.warn("END WEBSCRAP {}. FOUND {}", word, forvos.size());
      return forvos;
    }
    return new ArrayList<>();

  }

  private void downloadWord(Forvo forvo, Long id) throws IOException {
    String url = "https://pl.forvo.com/download/mp3/" + forvo.getWord() + "/" + forvo.getLanguage() + "/" + forvo.getId();
    downloadAudio(url, forvo, id);
  }

  private void downloadPhrase(Forvo forvo, Long id) throws IOException {
    String url = "https://pl.forvo.com/download/phrase/mp3/" + forvo.getWord() + "/" + forvo.getLanguage() + "/" + forvo.getId();
    downloadAudio(url, forvo, id);
  }

  private void downloadAudio(String url, Forvo forvo, Long id) throws IOException {
    HttpClient httpClient = new DefaultHttpClient();
    httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

    HttpGet httpGet = new HttpGet(url);
    httpGet.addHeader("COOKIE", "PHPSESSID=" + PHPSESSID);
    HttpResponse response = httpClient.execute(httpGet);
    InputStream in = response.getEntity().getContent();

    String english = forvo.getLanguageVersion().equals("amerykański") ? "us" : "uk";
    String file = "C:\\Users\\rawi\\IdeaProjects\\LearnEnglishGUI\\audio\\" + id + "_" + english + ".mp3";
    FileOutputStream fos = new FileOutputStream(new File(file));

    byte[] buffer = new byte[4096];
    int length;
    while ((length = in.read(buffer)) > 0) {
      fos.write(buffer, 0, length);
    }
  }
}
