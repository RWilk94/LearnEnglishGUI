package rwilk.learnenglish.controller.group;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import rwilk.learnenglish.controller.word.WordFormController;
import rwilk.learnenglish.model.entity.Lesson;
import rwilk.learnenglish.model.entity.Word;
import rwilk.learnenglish.repository.LessonRepository;
import rwilk.learnenglish.repository.WordRepository;

@Slf4j
@Data
public class GroupViewItemController implements Initializable {

  public ListView listView;
  public TextField textFieldLesson;
  public Button buttonSetLesson;
  public Button buttonAddHere;
  public Text textHeadline;

  private LessonRepository lessonRepository;
  private WordRepository wordRepository;
  private WordFormController wordFormController;
  private Lesson lesson;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }

  public void init(WordRepository wordRepository, LessonRepository lessonRepository, WordFormController wordFormController) {
    this.wordRepository = wordRepository;
    this.lessonRepository = lessonRepository;
    this.wordFormController = wordFormController;
  }

  public void buttonSetLessonOnMouseClicked(MouseEvent mouseEvent) {
    try {
      lessonRepository.findById(Long.valueOf(textFieldLesson.getText())).ifPresent(lesson -> {
        this.lesson = lesson;
        this.textFieldLesson.setText(this.lesson.getId() + ". " + this.lesson.getEnName() + " (" + this.lesson.getPlName() + ")");
        listView.setItems(FXCollections.observableArrayList(lesson.getWords()));
        textHeadline.setText(lesson.getCourse().getEnName() + " (" + lesson.getEnName() + ")" + "[" + lesson.getWords().size() + "]");
      });
    } catch (Exception e) {
      lessonRepository.findById(lesson.getId()).ifPresent(lesson -> {
        this.lesson = lesson;
        this.textFieldLesson.setText(this.lesson.getId() + ". " + this.lesson.getEnName() + " (" + this.lesson.getPlName() + ")");
        listView.setItems(FXCollections.observableArrayList(lesson.getWords()));
        textHeadline.setText(lesson.getCourse().getEnName() + " (" + lesson.getEnName() + ")" + "[" + lesson.getWords().size() + "]");
      });
    }
  }

  public void setLesson(Lesson lesson) {
    this.lesson = lesson;
    this.textFieldLesson.setText(this.lesson.getId() + ". " + this.lesson.getEnName() + " (" + this.lesson.getPlName() + ")");
    listView.setItems(FXCollections.observableArrayList(lesson.getWords()));
    textHeadline.setText(lesson.getCourse().getEnName() + " (" + lesson.getEnName() + ")" + "[" + lesson.getWords().size() + "]");
  }

  public void buttonAddHereOnMouseClicked(MouseEvent mouseEvent) {
    Optional<Word> optionalWord = wordRepository.findById(Long.valueOf(wordFormController.textFieldId.getText()));
    optionalWord.ifPresent(word -> {

      word.setLesson(lessonRepository.findById(lesson.getId()).get());
      word = wordRepository.save(word);
      listView.getItems().add(word);
      listView.refresh();
      wordFormController.updateSpecificWord(word);
      textHeadline.setText(lesson.getCourse().getEnName() + " (" + lesson.getEnName() + ")" + "[" + lesson.getWords().size() + "]");
      // wordFormController.refreshTableView();
    });
  }

  public void listViewOnMouseClicked(MouseEvent mouseEvent) {
    Word selectedItem = (Word) listView.getSelectionModel().getSelectedItem();
    if (selectedItem != null) {
      wordFormController.setWordForm(selectedItem);
    }

  }
}
