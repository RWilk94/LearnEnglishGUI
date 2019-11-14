package rwilk.learnenglish.controller.word;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import rwilk.learnenglish.controller.scrapper.SentenceScrapperController;
import rwilk.learnenglish.controller.sentence.SentenceFormController;
import rwilk.learnenglish.model.entity.Course;
import rwilk.learnenglish.model.entity.Lesson;
import rwilk.learnenglish.model.entity.Word;
import rwilk.learnenglish.repository.CourseRepository;
import rwilk.learnenglish.repository.LessonRepository;
import rwilk.learnenglish.repository.WordRepository;
import rwilk.learnenglish.util.AlertDialog;

@Component
public class WordsTableController implements Initializable {

  public ComboBox comboBoxFilterCourse;
  public ComboBox comboBoxFilterLesson;
  public TextField textFieldSearch;
  public Button buttonSearch;
  public TableView tableWords;
  public TableColumn columnId;
  public TableColumn columnWordEn;
  public TableColumn columnWordPl;
  public TableColumn columnPartOfSpeech;
  public TableColumn columnLevel;
//  public TableColumn columnEnSentence;
//  public TableColumn columnPlSentence;
  public TableColumn columnLesson;
  @Autowired
  private WordRepository wordRepository;
  @Autowired
  private WordFormController wordFormController;
  @Autowired
  private SentenceFormController sentenceFormController;
  @Autowired
  private SentenceScrapperController sentenceScrapperController;
  @Autowired
  private CourseRepository courseRepository;
  @Autowired
  private LessonRepository lessonRepository;
  private List<Word> words;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();
    fillInTableView();
    initCourseComboBox();

    textFieldSearch.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        filterTable(newValue);
      }
    });

  }

  private void filterTable(String value) {
    tableWords.setItems(
        FXCollections.observableArrayList(
            words.stream().filter(word -> word.toString().toLowerCase().contains(value.toLowerCase())).collect(Collectors.toList())
        )
    );
  }

  public void comboBoxFilterCourseOnAction(ActionEvent event) {
    if (!comboBoxFilterCourse.getSelectionModel().isEmpty()) {
      Course selectedCourse = (Course) comboBoxFilterCourse.getSelectionModel().getSelectedItem();
      initLessonComboBox(selectedCourse);
      words = wordRepository.findAllByLesson_Course(selectedCourse);
      tableWords.setItems(FXCollections.observableArrayList(words));
      filterTable(textFieldSearch.getText());
    }
  }

  public void comboBoxFilterLessonOnAction(ActionEvent event) {
    if (!comboBoxFilterLesson.getSelectionModel().isEmpty()) {
      Lesson selectedLesson = (Lesson) comboBoxFilterLesson.getSelectionModel().getSelectedItem();
      words = wordRepository.findAllByLesson(selectedLesson);
      tableWords.setItems(FXCollections.observableArrayList(words));
      filterTable(textFieldSearch.getText());
    }
  }

  public void buttonClearFilterOnAction(ActionEvent event) {
    words = wordRepository.findAll();
    tableWords.setItems(FXCollections.observableArrayList(words));
    textFieldSearch.clear();
    comboBoxFilterCourse.getSelectionModel().select(null);
    comboBoxFilterLesson.getSelectionModel().select(null);
  }

  public void fillInTableView() {
    this.words = wordRepository.findAll();
    tableWords.setItems(FXCollections.observableArrayList(words));
    comboBoxFilterCourseOnAction(null);
    comboBoxFilterLessonOnAction(null);
    filterTable(textFieldSearch.getText());
  }

  public void tableViewOnMouseClicked(MouseEvent mouseEvent) {
    if (!tableWords.getSelectionModel().isEmpty()) {
      Word selectedWord = (Word) tableWords.getSelectionModel().getSelectedItem();
      if (!wordFormController.textFieldEnWord.getText().isEmpty() && !wordFormController.textFieldEnWord.getText()
          .equalsIgnoreCase(selectedWord.getEnWord()) && sentenceScrapperController.getReplaceDialog()) {
        boolean result = AlertDialog.showConfirmationDialog("Replace", "Do you want to replace current EN Word?");
        if (result) {
          wordFormController.setWordForm(selectedWord);
          sentenceFormController.setWordId(selectedWord.getId());
          if (sentenceScrapperController.getTranslate()) {
            sentenceScrapperController.setWordToTranslate(selectedWord.getEnWord());
          }
        }
      } else {
        wordFormController.setWordForm(selectedWord);
        sentenceFormController.setWordId(selectedWord.getId());
        if (sentenceScrapperController.getTranslate()) {
          sentenceScrapperController.setWordToTranslate(selectedWord.getEnWord());
        }
      }
    }
  }

  public void initCourseComboBox() {
    List<Course> courses = courseRepository.findAll();
    comboBoxFilterCourse.setItems(FXCollections.observableArrayList(courses));
  }

  private void initLessonComboBox(Course course) {
    List<Lesson> lessons = lessonRepository.findAllByCourse(course);
    comboBoxFilterLesson.setItems(FXCollections.observableArrayList(lessons));
  }

  private void initializeTableView() {
    columnId.prefWidthProperty().bind(tableWords.widthProperty().multiply(0.05));
    columnWordEn.prefWidthProperty().bind(tableWords.widthProperty().multiply(0.30));
    columnWordPl.prefWidthProperty().bind(tableWords.widthProperty().multiply(0.30));
    columnPartOfSpeech.prefWidthProperty().bind(tableWords.widthProperty().multiply(0.10));
    columnLevel.prefWidthProperty().bind(tableWords.widthProperty().multiply(0.05));
//    columnEnSentence.prefWidthProperty().bind(tableWords.widthProperty().multiply(0.15));
//    columnPlSentence.prefWidthProperty().bind(tableWords.widthProperty().multiply(0.15));
    columnLesson.prefWidthProperty().bind(tableWords.widthProperty().multiply(0.20));
  }
}
