package rwilk.learnenglish.controller.word;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
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

@Component
public class WordFormController implements Initializable {

  public TextField textFieldExtractWord;
  public Button buttonExtract;
  public TextField textFieldId;
  public TextField textFieldEnWord;
  public TextField textFieldPlWord;
  public TextField textFieldEnSentence;
  public TextField textFieldPlSentence;
  public ComboBox comboBoxPartOfSpeech;
  public ComboBox comboBoxLevel;
  public ComboBox comboBoxCourse;
  public ComboBox comboBoxLesson;
  public TextArea textAreaEn;
  public TextArea textAreaPl;
  public CheckBox checkBoxFilterTable;
  @Autowired
  private CourseRepository courseRepository;
  @Autowired
  private LessonRepository lessonRepository;
  @Autowired
  private WordRepository wordRepository;
  @Autowired
  private WordsTableController wordsTableController;
  @Autowired
  private SentenceScrapperController sentenceScrapperController;
  @Autowired
  private SentenceFormController sentenceFormController;
  private List<String> partOfSpeechList;
  private Course selectedCourse;
  private Lesson selectedLesson;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializePartOfSpeechComboBox();
    initializeLevelComboBox();
    initializeCourseComboBox();

    textFieldEnWord.textProperty().addListener(
        (observable, oldValue, newValue) -> {
          sentenceScrapperController.textFieldWordToTranslate.setText(newValue);
          if (checkBoxFilterTable.isSelected()) {
            wordsTableController.textFieldSearch.setText(newValue);
          }
        });
  }

  public void buttonExtractOnMouseClicked(MouseEvent mouseEvent) {
    clearForm();
    List<String> enLines = new ArrayList<>(Arrays.asList(textAreaEn.getText().split("\n")));
    List<String> plLines = new ArrayList<>(Arrays.asList(textAreaPl.getText().split("\n")));
    if (!enLines.isEmpty() && !plLines.isEmpty()) {
      textFieldEnWord.setText(enLines.get(0).trim());
      textFieldPlWord.setText(plLines.get(0).trim());
      enLines.remove(0);
      plLines.remove(0);

      StringBuilder stringBuilderEn = new StringBuilder();
      enLines.forEach(text -> stringBuilderEn.append(text).append("\n"));
      textAreaEn.setText(stringBuilderEn.toString());
      StringBuilder stringBuilderPl = new StringBuilder();
      plLines.forEach(text -> stringBuilderPl.append(text).append("\n"));
      textAreaPl.setText(stringBuilderPl.toString());

      sentenceScrapperController.setWordToTranslate(textFieldEnWord.getText());
      sentenceFormController.buttonClearOnAction(null);
      sentenceScrapperController.radioButtonEnToPl.selectedProperty().setValue(true);
      sentenceScrapperController.getTatoebaScrapper().setLanguageEnToPl();
    }
  }

  public void comboBoxCourseOnAction(ActionEvent event) {
    if (!comboBoxCourse.getSelectionModel().isEmpty()) {
      selectedCourse = (Course) comboBoxCourse.getSelectionModel().getSelectedItem();
      initializeLessonComboBox();
    }
  }

  public void buttonRefreshOnAction(ActionEvent event) {
    comboBoxCourse.getItems().clear();
    initializeCourseComboBox();
    comboBoxLesson.getItems().clear();
    refreshTableView();
  }

  public void buttonClearOnAction(ActionEvent event) {
    clearForm();
    comboBoxPartOfSpeech.getSelectionModel().select(null);
    comboBoxLevel.getSelectionModel().select(null);
    selectedCourse = null;
    selectedLesson = null;
    comboBoxCourse.getSelectionModel().select(null);
    comboBoxLesson.getItems().clear();
  }

  public void buttonDeleteOnAction(ActionEvent event) {
    if (!textFieldId.getText().isEmpty()) {
      wordRepository.findById(Long.valueOf(textFieldId.getText())).ifPresent(word -> wordRepository.delete(word));
      buttonClearOnAction(event);
      refreshTableView();
    }
  }

  public void buttonEditOnAction(ActionEvent event) {
    if (!textFieldId.getText().isEmpty()
        && !textFieldEnWord.getText().isEmpty()
        && !textFieldPlWord.getText().isEmpty()
        && !comboBoxPartOfSpeech.getSelectionModel().isEmpty()
        && !comboBoxLevel.getSelectionModel().isEmpty()
        && ((!textFieldEnSentence.getText().isEmpty() && !textFieldPlSentence.getText().isEmpty()) ||
        (textFieldEnSentence.getText().isEmpty() && textFieldPlSentence.getText().isEmpty()))
        && !comboBoxLesson.getSelectionModel().isEmpty()) {
      Optional<Word> wordOptional = wordRepository.findById(Long.valueOf(textFieldId.getText()));
      wordOptional.ifPresent(word -> {
        word.setEnWord(textFieldEnWord.getText().trim());
        word.setPlWord(textFieldPlWord.getText().trim());
        word.setPartOfSpeech(comboBoxPartOfSpeech.getSelectionModel().getSelectedItem().toString());
        word.setLevel((Integer) comboBoxLevel.getSelectionModel().getSelectedItem());
        word.setEnSentence(textFieldEnSentence.getText().trim());
        word.setPlSentence(textFieldPlSentence.getText().trim());
        word.setLesson((Lesson) comboBoxLesson.getSelectionModel().getSelectedItem());
        word.setIsReady(0);
        setWordForm(wordRepository.save(word));
        refreshTableView();
      });
    }
  }

  public void buttonAddOnAction(ActionEvent event) {
    if (!textFieldEnWord.getText().isEmpty()
        && !textFieldPlWord.getText().isEmpty()
        && !comboBoxPartOfSpeech.getSelectionModel().isEmpty()
        && !comboBoxLevel.getSelectionModel().isEmpty()
        && ((!textFieldEnSentence.getText().isEmpty() && !textFieldPlSentence.getText().isEmpty()) ||
        (textFieldEnSentence.getText().isEmpty() && textFieldPlSentence.getText().isEmpty()))
        && !comboBoxLesson.getSelectionModel().isEmpty()) {
      Word word = Word.builder()
          .enWord(textFieldEnWord.getText().trim())
          .plWord(textFieldPlWord.getText().trim())
          .partOfSpeech(comboBoxPartOfSpeech.getSelectionModel().getSelectedItem().toString())
          .level((Integer) comboBoxLevel.getSelectionModel().getSelectedItem())
          .enSentence(textFieldEnSentence.getText().trim())
          .plSentence(textFieldPlSentence.getText().trim())
          .lesson((Lesson) comboBoxLesson.getSelectionModel().getSelectedItem())
          .isReady(0)
          .build();
      word = wordRepository.save(word);
      setWordForm(word);
      refreshTableView();
      sentenceFormController.textFieldWordId.setText(word.getId().toString());
    }
  }

  public void setWordForm(Word word) {
    textFieldId.setText(word.getId().toString());
    textFieldEnWord.setText(word.getEnWord());
    textFieldPlWord.setText(word.getPlWord());
    comboBoxPartOfSpeech.getSelectionModel().select(word.getPartOfSpeech());
    comboBoxLevel.getSelectionModel().select(word.getLevel());
    textFieldEnSentence.setText(word.getEnSentence());
    textFieldPlSentence.setText(word.getPlSentence());
    word.getLesson().getCourse().setLessons(null);
    comboBoxCourse.getSelectionModel().select(word.getLesson().getCourse());
    initializeLessonComboBox();
    List<Lesson> items = (List<Lesson>) comboBoxLesson.getItems();
    items.stream().filter(item -> item.getId() == word.getLesson().getId()).findFirst()
        .ifPresent(item -> comboBoxLesson.getSelectionModel().select(item));
    //    comboBoxLesson.getSelectionModel().select(word.getLesson());
  }

  public void setTranslateWord(String selectedItem) {
    textFieldPlWord.setText(selectedItem);
  }

  private void clearForm() {
    textFieldId.clear();
    textFieldEnWord.clear();
    textFieldPlWord.clear();
    textFieldEnSentence.clear();
    textFieldPlSentence.clear();
  }

  private void initializePartOfSpeechComboBox() {
    partOfSpeechList = new ArrayList<>();
    partOfSpeechList.add("");
    partOfSpeechList.add("Noun (Rzeczownik)");
    partOfSpeechList.add("Adjective (Przymiotnik)");
    partOfSpeechList.add("Verb (Czasownik)");
    partOfSpeechList.add("Adverb (Przysłówek)");
    partOfSpeechList.add("Other (Inny)");
    partOfSpeechList.add("Sentence (Zdanie)");

    comboBoxPartOfSpeech.setItems(FXCollections.observableArrayList(partOfSpeechList));
  }

  private void initializeLevelComboBox() {
    comboBoxLevel.setItems(FXCollections.observableArrayList(Arrays.asList(1, 2, 3, 4)));
  }

  private int getPartOfSpeechIndex(String partOfSpeech) {
    List<String> partOfSpeeches = partOfSpeechList.stream()
        .filter(StringUtils::isNotEmpty)
        .map(item -> item.substring(item.indexOf("(") + 1, item.indexOf(")")).toLowerCase()).collect(Collectors.toList());
    return partOfSpeeches.indexOf(partOfSpeech.toLowerCase()) + 1;
  }

  private void initializeCourseComboBox() {
    List<Course> courses = courseRepository.findAll();
    comboBoxCourse.setItems(FXCollections.observableArrayList(courses));
  }

  private void initializeLessonComboBox() {
    Optional.ofNullable(selectedCourse).ifPresent(course -> {
      List<Lesson> lessons = lessonRepository.findAllByCourse(course);
      lessons.forEach(lesson -> lesson.getCourse().setLessons(null));
      //      List<String> lessonNames = lessons.stream().map(Lesson::getPlName).collect(Collectors.toList());
      comboBoxLesson.setItems(FXCollections.observableArrayList(lessons));
    });
  }

  private void refreshTableView() {
    wordsTableController.fillInTableView();
  }
}
