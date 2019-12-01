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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import rwilk.learnenglish.controller.scrapper.MemriseCourseScrapper;
import rwilk.learnenglish.controller.scrapper.MemriseScrapper;
import rwilk.learnenglish.controller.scrapper.SentenceScrapperController;
import rwilk.learnenglish.controller.sentence.SentenceFormController;
import rwilk.learnenglish.model.MemriseWord;
import rwilk.learnenglish.model.entity.Course;
import rwilk.learnenglish.model.entity.Lesson;
import rwilk.learnenglish.model.entity.Word;
import rwilk.learnenglish.repository.CourseRepository;
import rwilk.learnenglish.repository.LessonRepository;
import rwilk.learnenglish.repository.WordRepository;

@Slf4j
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
  public TextField textFieldPartOfSpeech;
  public Button buttonMemrise;
  public ListView listViewWords;
  public ComboBox comboBoxCourse2;
  public ComboBox comboBoxLesson2;
  public Button buttonLowerPL;
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
  @Autowired
  private MemriseScrapper memriseScrapper;
  @Autowired
  private MemriseCourseScrapper memriseCourseScrapper;
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

    textFieldPlWord.textProperty().addListener((observable, oldValue, newValue) -> {
      if (checkBoxFilterTable.isSelected()) {
        wordsTableController.textFieldSearchPl.setText(newValue);
      }
    });
  }

  public void initializeShortcut() {
    KeyCombination keyCombination = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
    Runnable runnable = () -> {
      System.out.println("Ctrl + L");
      buttonToLowerEnOnAction(null);
      buttonToLowerPlOnAction(null);
    };
    buttonMemrise.getScene().getAccelerators().put(keyCombination, runnable);

    keyCombination = new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN);
    runnable = () -> {
      System.out.println("Ctrl + E");
      buttonEditOnAction(null);
    };
    buttonMemrise.getScene().getAccelerators().put(keyCombination, runnable);

    keyCombination = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);
    runnable = () -> {
      System.out.println("Ctrl + A");
      buttonAddOnAction(null);
    };
    buttonMemrise.getScene().getAccelerators().put(keyCombination, runnable);

    keyCombination = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
    runnable = () -> {
      System.out.println("Ctrl + R");
      buttonRemoveItemOnAction(null);
    };

    keyCombination = new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN);
    runnable = () -> {
      System.out.println("Ctrl + D");
      buttonDeleteOnAction(null);
    };

    buttonMemrise.getScene().getAccelerators().put(keyCombination, runnable);
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

      if (sentenceScrapperController.checkBoxTranslate.isSelected()) {
        sentenceScrapperController.setWordToTranslate(textFieldEnWord.getText());
        sentenceFormController.buttonClearOnAction(null);
        sentenceScrapperController.radioButtonEnToPl.selectedProperty().setValue(true);
        sentenceScrapperController.getTatoebaScrapper().setLanguageEnToPl();
      }

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
      wordRepository.findById(Long.valueOf(textFieldId.getText())).ifPresent(word -> {
        wordRepository.delete(word);
        int index = wordsTableController.findWordById(word.getId());
        wordsTableController.getWords().remove(index);
      });
      // buttonClearOnAction(event);
//      clearForm();
      refreshTableView();
    }
  }

  public void buttonEditOnAction(ActionEvent event) {
    Lesson lesson;
    if (!comboBoxLesson2.getSelectionModel().isEmpty()) {
      lesson = (Lesson) comboBoxLesson2.getSelectionModel().getSelectedItem();
    } else {
      lesson = (Lesson) comboBoxLesson.getSelectionModel().getSelectedItem();
    }
    String partOfSpeech = Optional.ofNullable(textFieldPartOfSpeech).map(TextInputControl::getText).orElse("inny");

    if (!textFieldId.getText().isEmpty()
        && !textFieldEnWord.getText().isEmpty()
        && !textFieldPlWord.getText().isEmpty()
        // && !comboBoxPartOfSpeech.getSelectionModel().isEmpty()
        // && !textFieldPartOfSpeech.getText().isEmpty()
        // && !comboBoxLevel.getSelectionModel().isEmpty()
        //&& ((!textFieldEnSentence.getText().isEmpty() && !textFieldPlSentence.getText().isEmpty()) ||
        //(textFieldEnSentence.getText().isEmpty() && textFieldPlSentence.getText().isEmpty()))
        && lesson != null) {
      // && !comboBoxLesson.getSelectionModel().isEmpty()) {
      Optional<Word> wordOptional = wordRepository.findById(Long.valueOf(textFieldId.getText()));
      wordOptional.ifPresent(word -> {
        word.setEnWord(textFieldEnWord.getText().trim());
        word.setPlWord(textFieldPlWord.getText().trim());
        // word.setPartOfSpeech(comboBoxPartOfSpeech.getSelectionModel().getSelectedItem().toString());
        word.setPartOfSpeech(partOfSpeech.trim());
        // word.setLevel((Integer) comboBoxLevel.getSelectionModel().getSelectedItem());
        //word.setEnSentence(textFieldEnSentence.getText().trim());
        //word.setPlSentence(textFieldPlSentence.getText().trim());
        word.setNextRepeat(System.currentTimeMillis());
        word.setLesson(lesson);
        word.setIsReady(0);
        word = wordRepository.save(word);

        int index = wordsTableController.findWordById(word.getId());
        wordsTableController.getWords().set(index, word);

        setWordForm(word);
        refreshTableView();
      });
    }
  }

  public void buttonAddOnAction(ActionEvent event) {
    Lesson lesson;
    if (!comboBoxLesson2.getSelectionModel().isEmpty()) {
      lesson = (Lesson) comboBoxLesson2.getSelectionModel().getSelectedItem();
    } else {
      lesson = (Lesson) comboBoxLesson.getSelectionModel().getSelectedItem();
    }

    String partOfSpeech = Optional.ofNullable(textFieldPartOfSpeech).map(TextInputControl::getText).orElse("inny");

    if (!textFieldEnWord.getText().isEmpty()
        && !textFieldPlWord.getText().isEmpty()
        // && !comboBoxPartOfSpeech.getSelectionModel().isEmpty()
//        && !textFieldPartOfSpeech.getText().isEmpty()
       // && !comboBoxLevel.getSelectionModel().isEmpty()
        //&& ((!textFieldEnSentence.getText().isEmpty() && !textFieldPlSentence.getText().isEmpty()) ||
        // (textFieldEnSentence.getText().isEmpty() && textFieldPlSentence.getText().isEmpty()))
        // && !comboBoxLesson.getSelectionModel().isEmpty()) {
        && lesson != null) {
      Word word = Word.builder()
          .enWord(textFieldEnWord.getText().trim())
          .plWord(textFieldPlWord.getText().trim())
          // .partOfSpeech(comboBoxPartOfSpeech.getSelectionModel().getSelectedItem().toString())
          .partOfSpeech(partOfSpeech.trim())
          //.level((Integer) comboBoxLevel.getSelectionModel().getSelectedItem())
          //.enSentence(textFieldEnSentence.getText().trim())
          //.plSentence(textFieldPlSentence.getText().trim())
          .nextRepeat(System.currentTimeMillis())
          .lesson(lesson)
          .isReady(0)
          .build();
      word = wordRepository.save(word);

      wordsTableController.getWords().add(word);

      setWordForm(word);
      refreshTableView();
      sentenceFormController.textFieldWordId.setText(word.getId().toString());
    }
  }

  public void setWordForm(Word word) {
    if (word != null) {
      textFieldId.setText(word.getId().toString());
      textFieldEnWord.setText(word.getEnWord());
      textFieldPlWord.setText(word.getPlWord());
      // comboBoxPartOfSpeech.getSelectionModel().select(word.getPartOfSpeech().toLowerCase());
      if (word.getPartOfSpeech() != null && !word.getPartOfSpeech().isEmpty()) {
        textFieldPartOfSpeech.setText(word.getPartOfSpeech());
      }
      comboBoxLevel.getSelectionModel().select(word.getLevel());

      textFieldEnSentence.setText(word.getEnSentence());
      textFieldPlSentence.setText(word.getPlSentence());

      word.getLesson().getCourse().setLessons(null);
      comboBoxCourse.getSelectionModel().select(word.getLesson().getCourse());
      selectedCourse = word.getLesson().getCourse();
      initializeLessonComboBox();
      List<Lesson> items = (List<Lesson>) comboBoxLesson.getItems();
      items.stream().filter(item -> item.getId().compareTo(word.getLesson().getId()) == 0).findFirst()
          .ifPresent(
              item -> comboBoxLesson.getSelectionModel().select(item));
      //    comboBoxLesson.getSelectionModel().select(word.getLesson());
    }
  }

  public void setTranslateWord(String selectedItem) {
    if (selectedItem.startsWith("[")) {
      textFieldPartOfSpeech.setText(selectedItem.substring(selectedItem.indexOf("[") + 1, selectedItem.indexOf("]")));
    } else {
      textFieldPlWord.setText(selectedItem);
    }
  }

  public void buttonMemriseOnMouseClicked(MouseEvent mouseEvent) {
    memriseScrapper.webScrap(textFieldExtractWord.getText());

    memriseScrapper.getEnWords().forEach(word -> textAreaEn.setText(textAreaEn.getText() + word + "\n"));
    memriseScrapper.getPlWords().forEach(word -> textAreaPl.setText(textAreaPl.getText() + word + "\n"));

    List<MemriseWord> memriseWords = new ArrayList<>();
    for (int i = 0; i < memriseScrapper.getEnWords().size(); i++) {
      memriseWords.add(MemriseWord.builder()
          .enWord(memriseScrapper.getEnWords().get(i))
          .plWord(memriseScrapper.getPlWords().get(i))
          .build());
    }
    listViewWords.setItems(FXCollections.observableArrayList(memriseWords));

    listViewWords.setCellFactory(row -> new ListCell<MemriseWord>() {
      @Override
      protected void updateItem(MemriseWord item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty && item != null) {
          setText(item.getEnWord() + " (" + item.getPlWord() + ")");
          setStyle(null);
          for (Word word : wordsTableController.getWords()) {
            if (word.getEnWord().equals(item.getEnWord()) && word.getPlWord().equals(item.getPlWord())) {
              if (word.getLesson().getCourse().toString().contains("Memrise")) {
                setStyle("-fx-background-color: green");
                break;
              } else {
                setStyle("-fx-background-color: lightgreen");
                break;
              }
            } else if (word.getEnWord().equals(item.getEnWord()) || word.getPlWord().equals(item.getPlWord())) {
              setStyle("-fx-background-color: orange");
            }
          }

//          wordsTableController.getWords().forEach(word -> {
//
//          });
        } else {
          setText(null);
          setStyle(null);
        }
      }
    });
    initializeShortcut();
  }

  public void listViewWordsOnMouseClicked(MouseEvent mouseEvent) {
    MemriseWord memriseWord = (MemriseWord) listViewWords.getSelectionModel().getSelectedItem();
    if (memriseWord != null) {
      textFieldEnWord.setText(memriseWord.getEnWord().trim());
      textFieldPlWord.setText(memriseWord.getPlWord().trim());
    }
    textFieldId.clear();

    if (sentenceScrapperController.checkBoxTranslate.isSelected()) {
      sentenceScrapperController.setWordToTranslate(textFieldEnWord.getText());
      sentenceFormController.buttonClearOnAction(null);
      sentenceScrapperController.radioButtonEnToPl.selectedProperty().setValue(true);
      sentenceScrapperController.getTatoebaScrapper().setLanguageEnToPl();
    }
  }

  public void buttonRemoveItemOnAction(ActionEvent event) {
    int index = listViewWords.getSelectionModel().getSelectedIndex();

    List<MemriseWord> memriseWords = (List<MemriseWord>) listViewWords.getItems();
    MemriseWord memriseWord = (MemriseWord) listViewWords.getSelectionModel().getSelectedItem();
    memriseWords.remove(memriseWord);
    listViewWords.setItems(FXCollections.observableArrayList(memriseWords));

    StringBuilder stringBuilderEn = new StringBuilder();
    memriseWords.forEach(text -> stringBuilderEn.append(text.getEnWord()).append("\n"));
    textAreaEn.setText(stringBuilderEn.toString());
    StringBuilder stringBuilderPl = new StringBuilder();
    memriseWords.forEach(text -> stringBuilderPl.append(text.getPlWord()).append("\n"));
    textAreaPl.setText(stringBuilderPl.toString());

    if (listViewWords.getItems().size() >= index && index != 0) {
      listViewWords.getSelectionModel().select(index - 1);
    } else {
      listViewWords.getSelectionModel().select(0);
    }
    listViewWordsOnMouseClicked(null);

  }

  public void comboBoxCourseOnAction2(ActionEvent event) {
    if (!comboBoxCourse2.getSelectionModel().isEmpty()) {
      //      selectedCourse = (Course) comboBoxCourse2.getSelectionModel().getSelectedItem();
      initializeLessonComboBox2((Course) comboBoxCourse2.getSelectionModel().getSelectedItem());
    }
  }

  public void buttonToLowerEnOnAction(ActionEvent event) {
    textFieldEnWord.setText(textFieldEnWord.getText().toLowerCase());
  }

  public void buttonToLowerPlOnAction(ActionEvent event) {
    textFieldPlWord.setText(textFieldPlWord.getText().toLowerCase());
  }

  public void buttonAllToLowerEnOnAction(ActionEvent event) {
    List<MemriseWord> memriseWords = (List<MemriseWord>) listViewWords.getItems();
    memriseWords.forEach(memriseWord -> {
      memriseWord.setEnWord(memriseWord.getEnWord().toLowerCase());
      memriseWord.setPlWord(memriseWord.getPlWord().toLowerCase());
    });
    listViewWords.setItems(null);
    listViewWords.setItems(FXCollections.observableArrayList(memriseWords));

    StringBuilder stringBuilderEn = new StringBuilder();
    memriseWords.forEach(text -> stringBuilderEn.append(text.getEnWord()).append("\n"));
    textAreaEn.setText(stringBuilderEn.toString());
    StringBuilder stringBuilderPl = new StringBuilder();
    memriseWords.forEach(text -> stringBuilderPl.append(text.getPlWord()).append("\n"));
    textAreaPl.setText(stringBuilderPl.toString());
  }

  public void buttonDeleteAllOnAction(ActionEvent event) {
    List<Word> words = wordsTableController.tableWords.getItems();
    for (Word word : words) {
      if (!word.getLesson().getEnName().contains("RELEASE")) {
        log.info("Delete word: " + word.toString());
        wordRepository.delete(word);
        int index = wordsTableController.findWordById(word.getId());
        wordsTableController.getWords().remove(index);
      }
    }
    refreshTableView();
  }

  public void buttonReplaceOnAction(ActionEvent event) {
    String text = textFieldEnWord.getText();
    textFieldEnWord.setText(textFieldPlWord.getText());
    textFieldPlWord.setText(text);
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
    partOfSpeechList.add("noun (rzeczownik)");
    partOfSpeechList.add("adjective (przymiotnik)");
    partOfSpeechList.add("verb (czasownik)");
    partOfSpeechList.add("adverb (przysłówek)");
    partOfSpeechList.add("other (inny)");
    partOfSpeechList.add("sentence (zdanie)");
    partOfSpeechList.add("phrasal verb (czasownik frazowy)");

    comboBoxPartOfSpeech.setItems(FXCollections.observableArrayList(partOfSpeechList));

    comboBoxPartOfSpeech.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
      @Override
      public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        if (newValue != null && !newValue.toString().isEmpty() && newValue.toString().contains("(") && newValue.toString().contains(")")) {
          textFieldPartOfSpeech
              .setText(newValue.toString().substring(newValue.toString().indexOf("(") + 1, newValue.toString().indexOf(")")));
        } else {
          textFieldPartOfSpeech.setText(newValue.toString());
        }
      }
    });
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
    comboBoxCourse2.setItems(FXCollections.observableArrayList(courses));
  }

  private void initializeLessonComboBox() {
    Optional.ofNullable(selectedCourse).ifPresent(course -> {
      List<Lesson> lessons = lessonRepository.findAllByCourse(course);
      lessons.forEach(lesson -> lesson.getCourse().setLessons(null));
      //      List<String> lessonNames = lessons.stream().map(Lesson::getPlName).collect(Collectors.toList());
      comboBoxLesson.setItems(FXCollections.observableArrayList(lessons));
    });
  }

  private void initializeLessonComboBox2(Course course) {
    Optional.ofNullable(course).ifPresent(c -> {
      List<Lesson> lessons = lessonRepository.findAllByCourse(c);
      lessons.forEach(lesson -> lesson.getCourse().setLessons(null));
      //      List<String> lessonNames = lessons.stream().map(Lesson::getPlName).collect(Collectors.toList());
      comboBoxLesson2.setItems(FXCollections.observableArrayList(lessons));
    });
  }

  private void refreshTableView() {
    wordsTableController.fillInTableView();
  }

  public void buttonMemriseCourseOnMouseClicked(MouseEvent mouseEvent) {
    memriseCourseScrapper.webScrap(textFieldExtractWord.getText());
  }
}
