package rwilk.learnenglish.controller.scrapper;

import java.util.ArrayList;
import java.util.List;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class MemriseScrapper {

  private List<String> plWords = new ArrayList<>();
  private List<String> enWords = new ArrayList<>();

  public void webScrap(String link) {
    try {
      plWords = new ArrayList<>();
      enWords = new ArrayList<>();

      Document document = Jsoup.connect(link).timeout(6000).get();
      Elements elements = document.select("div.central-column").select("div.text-text");

      for (Element element : elements) {
        enWords.add(element.select("div.col_a").text());
        plWords.add(element.select("div.col_b").text());
      }

    } catch (Exception e) {
      System.err.println(e.toString());
    }

  }

}
