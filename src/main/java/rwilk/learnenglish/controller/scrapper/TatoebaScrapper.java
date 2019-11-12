package rwilk.learnenglish.controller.scrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;

@Component
public class TatoebaScrapper {

  @Autowired
  private SentenceScrapperController sentenceScrapperController;
  private List<String> exampleSentenceList = new ArrayList<>();
  private String englishWord;
  private String language = "&from=eng&to=pol";
  private String languageEnToPl = "&from=eng&to=pol";
  private String languagePlToEn = "&from=pol&to=eng";
  private String languageEnToUn = "&from=eng&to=und";
  private String wholeWord = "%3D";

  public void refresh() {
    exampleSentenceList = new ArrayList<>();
  }

  public void setLanguageEnToPl() {
    language = languageEnToPl;
  }

  public void setLanguagePlToEn() {
    language = languagePlToEn;
  }

  public void setLanguageEnToUn() {
    language = languageEnToUn;
  }

  public void setWholeWord(boolean whole) {
    if (whole) {
      wholeWord = "%3D";
    } else {
      wholeWord = "";
    }
  }

  public void webScraps(String englishWord) {
    this.englishWord = englishWord;
    exampleSentenceList = new ArrayList<>();

    try {
      // add paging
      Document document =
          Jsoup.connect("https://tatoeba.org/pol/sentences/search?query=" + wholeWord + englishWord + language).timeout(20000).get();
      // search whole words

      Elements mainElement = document.select("div#main_content"); //get main content
      Elements pagesElement = mainElement.select("ul.paging"); // get pages navigation content

      Integer pagesNumber = 1;
      if (!pagesElement.isEmpty()) {
        pagesNumber = pagesElement.get(0).children().stream()
            .map(child -> child.children().text())
            .collect(Collectors.toList())
            .stream()
            .filter(StringUtils::isNumeric)
            .map(Integer::valueOf)
            .max(Integer::compareTo).orElse(1);
      }
      Elements translations = mainElement.select("div.sentence-and-translations"); //get 10 translations

      if (pagesNumber > 1) {
        if (pagesNumber > 4) {
          pagesNumber = 4;
        }
        for (int i = 2; i <= pagesNumber; i++) {
          document =
              Jsoup.connect("https://tatoeba.org/pol/sentences/search?query=" + englishWord + "&from=eng&to=pol&page=" + i).timeout(10000)
                  .get();
          mainElement = document.select("div#main_content"); //get main content
          translations.addAll(mainElement.select("div.sentence-and-translations"));
        }
      }
      /*translations.get(0).select("div").select("div.text").text(); //get english and polish translations
      translations.get(0).select("div").select("div.sentence").select("div.text").text(); //get english translations
      translations.get(0).select("div").select("div.translation").select("div.text").text(); //get polish translations*/

      exampleSentenceList = translations.stream()
          .map(translation -> translation.select("div").select("div.sentence").select("div.text").text()
              + "("
              + translation.select("div").select("div.translation").select("div.text").text()
              + ")"
          ).collect(Collectors.toList());

      // sentenceScrapperController.initListViewTranslateSentence(FXCollections.observableArrayList(exampleSentenceList));

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public List<String> getExampleSentenceList() {
    return exampleSentenceList;
  }

  public String getEnglishWord() {
    return englishWord;
  }

}

