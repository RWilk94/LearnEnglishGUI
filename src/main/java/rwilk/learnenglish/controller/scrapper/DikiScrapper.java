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
public class DikiScrapper {

  private String englishWord;
  private ArrayList<String> translationsList = new ArrayList<>();
  private ArrayList<String> exampleSentenceList = new ArrayList<>();
  @Autowired
  private SentenceScrapperController sentenceScrapperController;


  public void webScraps(String englishWord) {
    this.englishWord = englishWord;
    ArrayList<String> partOfSpeechList = new ArrayList<>();
    translationsList = new ArrayList<>();
    exampleSentenceList = new ArrayList<>();

    try {
      Document document = Jsoup.connect("http://www.diki.pl/slownik-angielskiego?q=" + englishWord).timeout(6000).get();
      Elements elements = document.select("div.diki-results-left-column"); //select("span.partOfSpeech");


      Elements wordVersions = elements.get(0).select("div.hws").get(0).select("span.hw");
      Elements englishVersions = elements.get(0).select("div.hws").get(0).select("a.languageVariety");
      // z tego foreach
      if (wordVersions.size() == englishVersions.size()) {
        for (int i = 0; i < wordVersions.size(); i++) {
          translationsList.add(wordVersions.get(i).text() + " (" + mapWordVersion(englishVersions.get(i).text()) + ")");
        }
      }


//      Elements wordVersions = elements.get(0).select("div.hws").select("span.hw");
////      Elements englishVersions = elements.get(0).select("span.recordingsAndTranscriptions");
//      Elements englishVersions = elements.get(0).select("a.languageVariety");
//      if (wordVersions.size() <= englishVersions.size()) {
//        for (int i = 0; i < wordVersions.size(); i++) {
//          translationsList.add(wordVersions.get(i).text() + " (" + mapWordVersion(elements.get(0).select("span.recordingsAndTranscriptions").get(i).child(0).attr("title")) + ")");
//        }
//      }

      for (Element element : elements.select("span.partOfSpeech")) {
        partOfSpeechList.add(element.text());
      }
      elements = document.select("ol.foreignToNativeMeanings");
      int iCountPartOfSpeech = 0;

      // Elements represented all translations for all part of speech
      // Element represented all translations for the single part of speech
      for (Element element : elements) {
        String lastPartOfSpeech = "";
        String tempTranslation = "";

        //Children is single translation.
        for (Element children : element.children()) {
          tempTranslation = "";
          if (lastPartOfSpeech.isEmpty()) {
            if (partOfSpeechList.isEmpty() || iCountPartOfSpeech >= partOfSpeechList.size()) {
              lastPartOfSpeech = "inne";
            } else {
              lastPartOfSpeech = partOfSpeechList.get(iCountPartOfSpeech);
            }
            translationsList.add("[" + lastPartOfSpeech + "]");
          }

          List<String> tempList = children.select("span.hw").eachText();
          for (String translation : tempList) {
            if (tempTranslation.isEmpty()) {
              tempTranslation = translation;
            } else {
              tempTranslation += ", " + translation;
            }
          }

          translationsList.add(tempTranslation);
          Elements exampleSentence = children.select("div.exampleSentence");
          for (Element elementExampleSentence : exampleSentence) {
            exampleSentenceList.add(elementExampleSentence.text());
          }
        }
        iCountPartOfSpeech++;
      }
      sentenceScrapperController.initListViewTranslateWord(FXCollections.observableArrayList(translationsList));
      sentenceScrapperController.initListViewTranslateSentence(FXCollections.observableArrayList(exampleSentenceList));
    } catch (Exception e) {
      System.err.println(e.toString());
    }
  }

  public ArrayList<String> getTranslationsList() {
    return translationsList;
  }

  public ArrayList<String> getExampleSentenceList() {
    return exampleSentenceList;
  }

  public String getEnglishWord() {
    return englishWord;
  }

  private String mapWordVersion(String wordVersion) {
    if (wordVersion.equals("British&nbsp;English") || wordVersion.equals("British English")) {
      return "BrE";
    } else if (wordVersion.equals("American&nbsp;English") || wordVersion.equals("American English")) {
      return "AmE";
    } else {
      return wordVersion;
    }
  }

}
