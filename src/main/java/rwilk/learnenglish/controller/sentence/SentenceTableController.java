package rwilk.learnenglish.controller.sentence;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import rwilk.learnenglish.model.entity.Sentence;
import rwilk.learnenglish.repository.SentenceRepository;

@Component
public class SentenceTableController implements Initializable {

  public TextField textFieldSearch;
  public Button buttonSearch;
  public TableView tableSentences;
  public TableColumn columnId;
  public TableColumn columnSentenceEn;
  public TableColumn columnSentencePl;
  public TableColumn columnWord;
  @Autowired
  private SentenceRepository sentenceRepository;
  @Autowired
  private SentenceFormController sentenceFormController;
  private List<Sentence> sentences;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();

    textFieldSearch.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        tableSentences.setItems(
            FXCollections.observableArrayList(
                sentences.stream().filter(sentence -> sentence.toString().toLowerCase().contains(newValue.toLowerCase())).collect(Collectors.toList())
            )
        );
      }
    });

    // fillInTableView();
  }

  private void initializeTableView() {
    columnId.prefWidthProperty().bind(tableSentences.widthProperty().multiply(0.05));
    columnSentenceEn.prefWidthProperty().bind(tableSentences.widthProperty().multiply(0.30));
    columnSentencePl.prefWidthProperty().bind(tableSentences.widthProperty().multiply(0.30));
    columnWord.prefWidthProperty().bind(tableSentences.widthProperty().multiply(0.35));
  }

  public void fillInTableView(Long wordId) {
    this.sentences = sentenceRepository.findAllByWord_Id(wordId);
    tableSentences.setItems(FXCollections.observableArrayList(sentences));
  }

  public void tableViewOnMouseClicked(MouseEvent mouseEvent) {
    if (!tableSentences.getSelectionModel().isEmpty()) {
      Sentence selectedSentence = (Sentence) tableSentences.getSelectionModel().getSelectedItem();
      sentenceFormController.setSentenceForm(selectedSentence);
    }
  }

}
