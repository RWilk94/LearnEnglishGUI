package rwilk.learnenglish.controller.sentence;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import rwilk.learnenglish.model.entity.Sentence;
import rwilk.learnenglish.repository.SentenceRepository;
import rwilk.learnenglish.repository.WordRepository;

@Component
public class SentenceFormController implements Initializable {

  public TextField textFieldId;
  public TextField textFieldEnSentence;
  public TextField textFieldPlSentence;
  public TextField textFieldWordId;
  @Autowired
  private SentenceRepository sentenceRepository;
  @Autowired
  private WordRepository wordRepository;
  @Autowired
  private SentenceTableController sentenceTableController;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void buttonRefreshOnAction(ActionEvent event) {
  }

  public void buttonClearOnAction(ActionEvent event) {
    textFieldId.clear();
    textFieldEnSentence.clear();
    textFieldPlSentence.clear();
    textFieldWordId.clear();
  }

  public void buttonDeleteOnAction(ActionEvent event) {
    if (!textFieldId.getText().isEmpty()) {
      sentenceRepository.findById(Long.valueOf(textFieldId.getText())).ifPresent(sentence -> sentenceRepository.delete(sentence));
      refreshTableView();
      buttonClearOnAction(event);
    }
  }

  public void buttonEditOnAction(ActionEvent event) {
    if (!textFieldId.getText().isEmpty() && !textFieldEnSentence.getText().isEmpty() && !textFieldPlSentence.getText().isEmpty()
        && !textFieldWordId.getText().isEmpty()) {
      Optional<Sentence> sentenceOptional = sentenceRepository.findById(Long.valueOf(textFieldId.getText()));
      sentenceOptional.ifPresent(sentence -> {
        sentence.setPlSentence(textFieldPlSentence.getText());
        sentence.setEnSentence(textFieldEnSentence.getText());
        wordRepository.findById(Long.valueOf(textFieldWordId.getText())).ifPresent(sentence::setWord);
        setSentenceForm(sentenceRepository.save(sentence));
        refreshTableView();
      });
    }
  }

  public void buttonAddOnAction(ActionEvent event) {
    if (!textFieldEnSentence.getText().isEmpty() && !textFieldPlSentence.getText().isEmpty() && !textFieldWordId.getText().isEmpty()) {
      Sentence sentence = Sentence.builder()
          .enSentence(textFieldEnSentence.getText().trim())
          .plSentence(textFieldPlSentence.getText().trim())
          .word(wordRepository.findById(Long.valueOf(textFieldWordId.getText())).get())
          .isReady(0)
          .build();
      sentence = sentenceRepository.save(sentence);
      setSentenceForm(sentence);
      refreshTableView();
    }
  }

  public void setWordId(Long id) {
    textFieldWordId.setText(id.toString());
    refreshTableView();
  }

  public void setTranslateWord(String polishSentence, String englishSentence) {
    textFieldPlSentence.setText(polishSentence);
    textFieldEnSentence.setText(englishSentence);
  }

  public void buttonReplaceOnAction(ActionEvent event) {
    String enText = textFieldEnSentence.getText();
    textFieldEnSentence.setText(textFieldPlSentence.getText());
    textFieldPlSentence.setText(enText);
  }

  private void refreshTableView() {
    if (!textFieldWordId.getText().isEmpty()) {
      sentenceTableController.fillInTableView(Long.valueOf(textFieldWordId.getText()));
    }
  }

  public void setSentenceForm(Sentence sentence) {
    textFieldId.setText(sentence.getId().toString());
    textFieldPlSentence.setText(sentence.getPlSentence());
    textFieldEnSentence.setText(sentence.getEnSentence());
    sentence.getWord().setSentences(null);
  }
}
