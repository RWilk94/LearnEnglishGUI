package rwilk.learnenglish.controller.scrapper;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;

@Component
public class WiktionaryScrapper {

  @Autowired
  private SentenceScrapperController sentenceScrapperController;
  private List<String> exampleSentenceList = new ArrayList<>();
  private List<String> translationsList = new ArrayList<>();
  private List<String> varietyList = new ArrayList<>();
  private String englishWord;

  public void webScraps(String englishWord) {
    this.englishWord = englishWord;

    translationsList = new ArrayList<>();
    exampleSentenceList = new ArrayList<>();
    varietyList = new ArrayList<>();

    try {
      Document document = Jsoup.connect("https://pl.wiktionary.org/wiki/" + englishWord + "#" +
          englishWord + "_(język_angielski)").timeout(6000).get();
      // main element
      Elements elements = document.select("div.mw-parser-output");

      for (Element element : elements) {
        // all children from main element
        Elements children = element.children();

        String language = "";
        boolean printMeaning = false;
        int firstNumber = 1;
        int secondNumber = 0;

        for (Element child : children) {

          if (child.text().contains("znaczenia:") && language.equals("język angielski")) {
            printMeaning = true;
          }

          // set language for English and find result for them
          for (Element text : child.children()) {
            if (text.text().contains("(język ")) {
              if (text.text().contains("(język angielski)")) {
                language = "język angielski";
              } else {
                language = "other";
              }
            }

            // get all example sentence for word
            if (language.equals("język angielski") && child.text().contains("przykłady:")) {
              if (!text.text().equals("przykłady:") && !text.text().isEmpty()) {
                exampleSentenceList.add(text.text());
              }
            }

            // get all meaning for word
            if (printMeaning && text.text().contains("(") && text.text().contains(".")) {
              if (text.text().substring(1, 2).charAt(0) < String.valueOf(firstNumber).charAt(0)
                  || (text.text().substring(3, 4).charAt(0) <= String.valueOf(secondNumber).charAt(0)
                  && text.text().substring(1, 2).charAt(0) == String.valueOf(firstNumber).charAt(0))) {
                printMeaning = false;
              } else if (text.text().substring(1, 2).charAt(0) >= String.valueOf(firstNumber).charAt(0)
                  && text.text().substring(3, 4).charAt(0) > String.valueOf(secondNumber).charAt(0)) {
                if (text.text().substring(1, 2).charAt(0) > String.valueOf(firstNumber).charAt(0)) {
                  secondNumber = 0;
                  firstNumber = Integer.valueOf(text.text().substring(1, 2));
                } else {
                  secondNumber = Integer.valueOf(text.text().substring(3, 4));
                }
                translationsList.add(text.text());
              } else {
                translationsList.add(text.text());
              }
            } else if (printMeaning && !text.text().contains(":")) {
              translationsList.add(text.text());
            }

            // gets the variety form
            if (text.text().equals("odmiana:") && language.equals("język angielski")) {
              int nextOpenBracket;
              int firstCloseBracket, nextCloseBracket;
              String variety = child.text();
              variety = variety.replaceAll("\n", " ");
              variety = variety.trim();
              do {
                firstCloseBracket = variety.indexOf(")");
                nextCloseBracket = variety.lastIndexOf(")");
                nextOpenBracket = variety.lastIndexOf("(");
                if (firstCloseBracket != nextCloseBracket) {
                  //more then one occurrence
                  varietyList.add(variety.substring(firstCloseBracket + 1, nextOpenBracket));
                  variety = variety.substring(nextCloseBracket + 1);
                  //varietyList.add(variety.substring(""))
                } else {
                  varietyList.add(variety.substring(nextCloseBracket + 1));
                  variety = variety.substring(nextCloseBracket + 1);
                }
              } while (firstCloseBracket != nextCloseBracket);
            }
          }
        }
      }
      // sentenceScrapperController.initListViewTranslateWord(FXCollections.observableArrayList(translationsList));
      // sentenceScrapperController.initListViewTranslateSentence(FXCollections.observableArrayList(exampleSentenceList));
    } catch (Exception e) {
      System.out.println(e.toString());
    }
    removeEmptyElements();
  }

  public List<String> getExampleSentenceList() {
    return exampleSentenceList;
  }

  public List<String> getTranslationsList() {
    return translationsList;
  }

  private void removeEmptyElements() {
    ArrayList<String> newTranslationsList = new ArrayList<>();
    for (String string : translationsList) {
      if (!string.isEmpty()) {
        newTranslationsList.add(string.trim());
      }
    }
    translationsList = newTranslationsList;

    for (String string : varietyList) {
      if (!string.isEmpty()) {
        translationsList.add(string.trim());
      }
    }
  }

  public String getEnglishWord() {
    return englishWord;
  }
}
