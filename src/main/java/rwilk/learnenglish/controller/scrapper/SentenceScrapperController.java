package rwilk.learnenglish.controller.scrapper;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import lombok.Data;
import rwilk.learnenglish.controller.sentence.SentenceFormController;
import rwilk.learnenglish.controller.word.WordFormController;

@SuppressWarnings("Duplicates")
@Component
@Data
public class SentenceScrapperController implements Initializable {

  public RadioButton radioButtonDiki;
  public RadioButton radioButtonWiktionary;
  public RadioButton radioButtonTatoeba;
  public ListView listViewTranslateWord;
  public ListView listViewTranslateSentence;
  public CheckBox checkBoxTranslate;
  public Button buttonClear;
  public Button buttonTranslate;
  public CheckBox checkBoxReplace;
  public RadioButton radioButtonEnToPl;
  public RadioButton radioButtonPlToEn;
  public RadioButton radioButtonEnToUn;
  public TextField textFieldWordToTranslate;
  public CheckBox checkBoxWholeWord;
  public TextField textFieldFilterSentences;
  @Autowired
  private DikiScrapper dikiScrapper;
  @Autowired
  private WiktionaryScrapper wiktionaryScrapper;
  @Autowired
  private TatoebaScrapper tatoebaScrapper;
  private String wordToTranslate;
  @Autowired
  private WordFormController wordFormController;
  @Autowired
  private SentenceFormController sentenceFormController;
  private Boolean translate = true;
  private Boolean replaceDialog = true;
  private Boolean wholeWord = true;
  private List<String> sentences;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeRadioButtons();

    textFieldFilterSentences.textProperty().addListener(
        (observable, oldValue, newValue) -> listViewTranslateSentence.setItems(
            FXCollections.observableArrayList(
                sentences.stream().filter(sentence -> sentence.toLowerCase().contains(newValue.toLowerCase())).collect(Collectors.toList())
            )
        )
    );
  }

  public void radioButtonDikiOnAction(ActionEvent event) {
    Optional.ofNullable(wordToTranslate).ifPresent(word -> {
      if (dikiScrapper.getEnglishWord() == null || !dikiScrapper.getEnglishWord().equals(word)) {
        dikiScrapper.webScraps(word);
      } else {
        initListViewTranslateWord(FXCollections.observableArrayList(dikiScrapper.getTranslationsList()));
        initListViewTranslateSentence(FXCollections.observableArrayList(dikiScrapper.getExampleSentenceList()));
      }
    });
  }

  public void radioButtonWiktionaryOnAction(ActionEvent event) {
    Optional.ofNullable(wordToTranslate).ifPresent(word -> {
      if (wiktionaryScrapper.getEnglishWord() == null || !wiktionaryScrapper.getEnglishWord().equals(word)) {
        wiktionaryScrapper.webScraps(word);
      } else {
        initListViewTranslateWord(FXCollections.observableArrayList(wiktionaryScrapper.getTranslationsList()));
        initListViewTranslateSentence(FXCollections.observableArrayList(wiktionaryScrapper.getExampleSentenceList()));
      }
    });
  }

  public void radioButtonTatoebaOnAction(ActionEvent event) {
    Optional.ofNullable(wordToTranslate).ifPresent(word -> {
      if (tatoebaScrapper.getEnglishWord() == null || !tatoebaScrapper.getEnglishWord().equals(word)) {
        tatoebaScrapper.webScraps(word);
      } else {
        initListViewTranslateSentence(FXCollections.observableArrayList(tatoebaScrapper.getExampleSentenceList()));
      }
    });
  }

  public void initListViewTranslateWord(ObservableList list) {
    listViewTranslateWord.setItems(list);
  }

  public void initListViewTranslateSentence(ObservableList list) {
    textFieldFilterSentences.clear();
    sentences = list;
    listViewTranslateSentence.setItems(list);
  }

  public void listViewTranslateWordOnMouseClicked(MouseEvent mouseEvent) {
    String selectedItem = (String) listViewTranslateWord.getSelectionModel().getSelectedItem();
    wordFormController.setTranslateWord(selectedItem);
  }

  public void listViewTranslateSentenceOnMouseClicked(MouseEvent mouseEvent) {
    String selectedItem = (String) listViewTranslateSentence.getSelectionModel().getSelectedItem();

    if (StringUtils.isNotEmpty(selectedItem)) {
      String polishTranslate = "", englishTranslate = "";
      if (radioButtonDiki.isSelected() || radioButtonTatoeba.isSelected()) {
        int index = selectedItem.indexOf("(");
        int lastIndex = selectedItem.lastIndexOf(")");
        polishTranslate = selectedItem.substring(index + 1, lastIndex);
        englishTranslate = selectedItem.substring(0, index);
      } else if (radioButtonWiktionary.isSelected()) {
        int indexBracket = selectedItem.indexOf(")");
        int index = selectedItem.indexOf("→");
        englishTranslate = selectedItem.substring(indexBracket + 1, index).trim();
        polishTranslate = selectedItem.substring(index + 1).trim();
      }
      sentenceFormController.setTranslateWord(polishTranslate, englishTranslate);
    }
  }

  public String getWordToTranslate() {
    return wordToTranslate;
  }

  public void setWordToTranslate(String wordToTranslate) {
    this.wordToTranslate = wordToTranslate.toLowerCase().trim();
    radioButtonDiki.selectedProperty().setValue(true);

    radioButtonDikiOnAction(null);
    new Thread(() -> radioButtonWiktionaryOnAction(null)).start();
    new Thread(() -> radioButtonTatoebaOnAction(null)).start();

    //    if (radioButtonDiki.isSelected()) {
    //      radioButtonDikiOnAction(null);
    //    } else if (radioButtonWiktionary.isSelected()) {
    //      radioButtonWiktionaryOnAction(null);
    //    } else if (radioButtonTatoeba.isSelected()) {
    //      radioButtonTatoebaOnAction(null);
    //    }
  }

  public void checkBoxTranslateOnAction(ActionEvent event) {
    this.translate = checkBoxTranslate.selectedProperty().get();
  }

  public void buttonClearOnMouseClicked(MouseEvent mouseEvent) {
    listViewTranslateWord.setItems(null);
    listViewTranslateSentence.setItems(null);
  }

  public void buttonTranslateOnMouseClicked(MouseEvent mouseEvent) {
    if (!wordFormController.textFieldEnWord.getText().isEmpty()) {
      wordToTranslate = wordFormController.textFieldEnWord.getText().trim().toLowerCase();
      radioButtonDikiOnAction(null);
    }
  }

  public void checkBoxReplaceOnAction(ActionEvent event) {
    this.replaceDialog = checkBoxReplace.selectedProperty().get();
  }

  public void radioButtonEnToPlOnAction(ActionEvent event) {
    tatoebaScrapper.setWholeWord(wholeWord);
    tatoebaScrapper.refresh();
    tatoebaScrapper.setLanguageEnToPl();
    tatoebaScrapper.webScraps(textFieldWordToTranslate.getText().trim());
    radioButtonTatoeba.selectedProperty().setValue(true);
    initListViewTranslateSentence(FXCollections.observableArrayList(tatoebaScrapper.getExampleSentenceList()));

  }

  public void radioButtonPlToEnOnAction(ActionEvent event) {
    tatoebaScrapper.setWholeWord(wholeWord);
    tatoebaScrapper.refresh();
    tatoebaScrapper.setLanguagePlToEn();
    tatoebaScrapper.webScraps(textFieldWordToTranslate.getText().trim());
    radioButtonTatoeba.selectedProperty().setValue(true);
    initListViewTranslateSentence(FXCollections.observableArrayList(tatoebaScrapper.getExampleSentenceList()));
  }

  public void radioButtonEnToUnOnAction(ActionEvent event) {
    tatoebaScrapper.setWholeWord(wholeWord);
    tatoebaScrapper.refresh();
    tatoebaScrapper.setLanguageEnToUn();
    tatoebaScrapper.webScraps(textFieldWordToTranslate.getText().trim());
    radioButtonTatoeba.selectedProperty().setValue(true);
    initListViewTranslateSentence(FXCollections.observableArrayList(tatoebaScrapper.getExampleSentenceList()));
  }

  public void checkBoxWholeWordOnAction(ActionEvent event) {
    this.wholeWord = checkBoxWholeWord.selectedProperty().get();
    if (radioButtonEnToPl.isSelected()) {
      radioButtonEnToPlOnAction(event);
    } else if (radioButtonPlToEn.isSelected()) {
      radioButtonPlToEnOnAction(event);
    } else {
      radioButtonEnToUnOnAction(event);
    }
  }

  public Boolean getTranslate() {
    return translate;
  }

  public Boolean getReplaceDialog() {
    return replaceDialog;
  }

  private void initializeRadioButtons() {
    ToggleGroup group = new ToggleGroup();
    radioButtonDiki.setToggleGroup(group);
    radioButtonWiktionary.setToggleGroup(group);
    radioButtonTatoeba.setToggleGroup(group);
    radioButtonDiki.setSelected(true);

    ToggleGroup toggleGroup = new ToggleGroup();
    radioButtonEnToPl.setToggleGroup(toggleGroup);
    radioButtonPlToEn.setToggleGroup(toggleGroup);
    radioButtonEnToUn.setToggleGroup(toggleGroup);
    radioButtonEnToPl.setSelected(true);
  }
}
