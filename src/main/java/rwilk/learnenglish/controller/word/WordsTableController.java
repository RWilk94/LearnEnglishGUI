package rwilk.learnenglish.controller.word;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import rwilk.learnenglish.controller.audio.AudioController;
import rwilk.learnenglish.controller.scrapper.SentenceScrapperController;
import rwilk.learnenglish.controller.sentence.SentenceFormController;
import rwilk.learnenglish.model.entity.Course;
import rwilk.learnenglish.model.entity.Lesson;
import rwilk.learnenglish.model.entity.Sentence;
import rwilk.learnenglish.model.entity.Word;
import rwilk.learnenglish.repository.CourseRepository;
import rwilk.learnenglish.repository.LessonRepository;
import rwilk.learnenglish.repository.SentenceRepository;
import rwilk.learnenglish.repository.WordRepository;
import rwilk.learnenglish.util.AlertDialog;

@Slf4j
@Component
public class WordsTableController implements Initializable {

  // public ComboBox comboBoxFilterCourse;
  // public ComboBox comboBoxFilterLesson;
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
  public CheckBox checkBoxWholeWord;
  public TextField textFieldSearchLesson;
  public TextField textFieldSearchCourse;

  public TextField textFieldSearch2;
  public TextField textFieldSearchLesson2;
  public TableView tableWords2;
  public TextField textFieldSearchCourse2;
  public TableColumn columnId2;
  public TableColumn columnWordEn2;
  public TableColumn columnWordPl2;
  public TableColumn columnPartOfSpeech2;
  public TableColumn columnLesson2;
  public TextField textFieldSearchPl;
  public CheckBox checkBoxAnd;
  public TableColumn columnOrder;
  public TextField textFieldOrder;
  @Getter
  public HBox HBoxGroupViewPane;
  @Getter
  public SplitPane splitPaneHorizontal;
  public TextField textFieldOriginalId;
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
  @Autowired
  private AudioController audioController;
  @Autowired
  private SentenceRepository sentenceRepository;
  @Getter
  private List<Word> words;
  private List<Word> words2;

  private String regex = "[^a-zA-Z0-9]";

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();
    fillInTableView();
    // initCourseComboBox();

    textFieldSearch.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        filterTable(newValue);
      }
    });
    textFieldSearch.setOnMouseClicked(e -> filterTable(textFieldSearch.getText()));

    textFieldSearchPl.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        filterTable(newValue);
      }
    });

    textFieldSearchPl.setOnMouseClicked(e -> filterTable(textFieldSearchPl.getText()));

    textFieldSearchCourse.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        filterTableByCourse(newValue);
      }
    });

    textFieldSearchCourse.setOnMouseClicked(e -> filterTableByCourse(textFieldSearchCourse.getText()));

    textFieldSearchLesson.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        filterTableByLesson(newValue);
      }
    });

    textFieldSearchLesson.setOnMouseClicked(e -> filterTableByLesson(textFieldSearchLesson.getText()));

    textFieldSearch2.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        filterTable2(newValue);
      }
    });

    textFieldSearch2.setOnMouseClicked(e -> filterTable2(textFieldSearch2.getText()));

    textFieldSearchCourse2.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        filterTableByCourse2(newValue);
      }
    });

    textFieldSearchCourse2.setOnMouseClicked(e -> filterTableByCourse2(textFieldSearchCourse2.getText()));

    textFieldSearchLesson2.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        filterTableByLesson2(newValue);
      }
    });

    textFieldSearchLesson2.setOnMouseClicked(e -> filterTableByLesson2(textFieldSearchLesson2.getText()));

    tableWords.setRowFactory(row -> new TableRow<Word>() {
      @Override
      protected void updateItem(Word item, boolean empty) {
        super.updateItem(item, empty);
        updateRowItem(item, this);
      }
    });

    tableWords2.setRowFactory(row -> new TableRow<Word>() {
      @Override
      protected void updateItem(Word item, boolean empty) {
        super.updateItem(item, empty);
        updateRowItem(item, this);

      }
    });

  }

  public void buttonClearFilterOnAction(ActionEvent event) {
    words = wordRepository.findAll();
    this.words = words.stream()
        .filter(word -> word.getLesson().getCourse().getEnName().contains("ETUTOR")
            || word.getLesson().getCourse().getEnName().contains("POZIOM")
            || word.getLesson().getCourse().getEnName().contains("RELEASE"))
        .collect(Collectors.toList());
    tableWords.setItems(FXCollections.observableArrayList(words));
    textFieldSearch.clear();
    // comboBoxFilterCourse.getSelectionModel().select(null);
    // comboBoxFilterLesson.getSelectionModel().select(null);

    // Remove duplicated words
    //    List<Word> releaseCourse =
    //        words.stream().filter(word -> word.getLesson().getCourse().getId().compareTo(28L) == 0).collect(Collectors.toList());
    //
    //    this.words.removeAll(releaseCourse);
    //    List<Word> duplicated = new ArrayList<>(this.words);
    //
    //    List<Word> wordsToDelete = new ArrayList<>();
    //    for (Word word : duplicated) {
    //      words.stream().forEach(w -> {
    //        if (word.getPlWord().equalsIgnoreCase(w.getPlWord()) && word.getEnWord().equalsIgnoreCase(w.getEnWord()) && w.getId().compareTo(word.getId()) != 0) {
    //          wordsToDelete.add(w);
    //        }
    //      });
    //    }
    //
    //    List<Word> wordsToDelete2 = wordsToDelete.stream().filter(w -> w.getSentences().isEmpty()).collect(Collectors.toList());
    //    for (Word wordToDelete : wordsToDelete2) {
    //      wordRepository.delete(wordToDelete);
    //    }

  }

  //  public void comboBoxFilterCourseOnAction(ActionEvent event) {
  //    if (!comboBoxFilterCourse.getSelectionModel().isEmpty()) {
  //      Course selectedCourse = (Course) comboBoxFilterCourse.getSelectionModel().getSelectedItem();
  //      initLessonComboBox(selectedCourse);
  //      words = wordRepository.findAllByLesson_Course(selectedCourse);
  //      tableWords.setItems(FXCollections.observableArrayList(words));
  //      filterTable(textFieldSearch.getText());
  //    }
  //  }
  //
  //  public void comboBoxFilterLessonOnAction(ActionEvent event) {
  //    if (!comboBoxFilterLesson.getSelectionModel().isEmpty()) {
  //      Lesson selectedLesson = (Lesson) comboBoxFilterLesson.getSelectionModel().getSelectedItem();
  //      words = wordRepository.findAllByLesson(selectedLesson);
  //      tableWords.setItems(FXCollections.observableArrayList(words));
  //      filterTable(textFieldSearch.getText());
  //    }
  //  }

  public void fillInTableView() {
    //if (this.words == null) {
    if (this.words == null) {
      this.words = wordRepository.findAll();
    }
    this.words = words.stream()
        .filter(word -> word.getLesson().getCourse().getEnName().contains("ETUTOR")
            || word.getLesson().getCourse().getEnName().contains("POZIOM")
            || word.getLesson().getCourse().getEnName().contains("RELEASE"))
        .collect(Collectors.toList());
    this.words.sort(Comparator.comparing(Word::getOrder).thenComparing(Word::getNextRepeat).thenComparing(Word::getId));
    //    this.words.stream().filter(word -> word.getLesson().getEnName().contains("RELEASE")).forEach(word -> word.setNextRepeat(word.getNextRepeat() + word.getNextRepeat()));
    //}

    //    List<Word> list = words.stream()
    //        .filter(word -> word.getEnWord().contains("’"))
    //        .collect(Collectors.toList());
    //    list.forEach(word -> word.setEnWord(word.getEnWord().replaceAll("’", "'")));
    //    list.forEach(word ->
    //        wordRepository.save(word));

    tableWords.setItems(FXCollections.observableArrayList(words));
    //    comboBoxFilterCourseOnAction(null);
    //    comboBoxFilterLessonOnAction(null);

    filterTable(textFieldSearch.getText());

    this.words2 = new ArrayList<>(this.words);
    this.words2.sort(Comparator.comparing(Word::getOrder).thenComparing(Word::getNextRepeat).thenComparing(Word::getId));
    this.tableWords2.setItems(FXCollections.observableArrayList(words2));
     this.filterTable2(this.textFieldSearch2.getText());
    this.filterTableByLesson2(this.textFieldSearchLesson2.getText());
    this.filterTableByCourse2(this.textFieldSearchCourse2.getText());
  }

  public void tableViewOnMouseClicked(MouseEvent event) {
    onChangeFocus(((TableView)event.getSource()).getId());
  }


//  public void tableViewOnSwipeDown(SwipeEvent event) {
//    onChangeFocus(((TableView)event.getSource()).getId());
//  }

  public void tableViewOnKeyPressed(KeyEvent event) {
    onChangeFocus(((TableView)event.getSource()).getId());
  }

  private void onChangeFocus(String id) {
    Word selectedWord;
    if (id.equals("tableWords")) {
      selectedWord = (Word) tableWords.getSelectionModel().getSelectedItem();
    } else {
      selectedWord = (Word) tableWords2.getSelectionModel().getSelectedItem();
    }
    if (selectedWord != null) {
      audioController.setSearchField(selectedWord.getEnWord());
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
      if (selectedWord.getLesson().getCourse().getEnName().contains("POZIOM")) {
        textFieldOriginalId.setText(selectedWord.getId().toString());
      }
    } else {
      log.error("selectedWord = null");
    }
  }

  public void checkBoxWholeWordOnAction(ActionEvent event) {
    filterTable(textFieldSearch.getText());
  }

  public void upOrder(MouseEvent mouseEvent) {
    Word selectedWord = (Word) tableWords.getSelectionModel().getSelectedItem();
    List<Word> selectedWords = tableWords.getItems();
    if (selectedWord != null) {
      int index = selectedWords.indexOf(selectedWord);
      if (selectedWord.getOrder() > 1) {
        selectedWords.get(index - 1).setOrder(selectedWords.get(index - 1).getOrder() + 1);
        selectedWords.get(index).setOrder(selectedWords.get(index).getOrder() - 1);
        selectedWords.sort(Comparator.comparing(Word::getOrder).thenComparing(Word::getId));
      }
      tableWords.setItems(FXCollections.observableArrayList(selectedWords));
    }
  }

  public void downOrder(MouseEvent mouseEvent) {
    Word selectedWord = (Word) tableWords.getSelectionModel().getSelectedItem();
    List<Word> selectedWords = tableWords.getItems();
    if (selectedWord != null) {
      int index = selectedWords.indexOf(selectedWord);
      selectedWords.get(index + 1).setOrder(selectedWords.get(index + 1).getOrder() - 1);
      selectedWords.get(index).setOrder(selectedWords.get(index).getOrder() + 1);
      selectedWords.sort(Comparator.comparing(Word::getOrder).thenComparing(Word::getId));
      tableWords.setItems(FXCollections.observableArrayList(selectedWords));
    }
  }

  public void setOrder(MouseEvent mouseEvent) {
    if (textFieldOrder.getText().equals("-1") || textFieldOrder.getText().isEmpty()) {
      List<Word> selectedWords = tableWords.getItems();
      AtomicInteger atomicInteger = new AtomicInteger(1);
      selectedWords.stream().forEach(word -> word.setOrder(atomicInteger.getAndIncrement()));
//      selectedWords.sort(Comparator.comparing(Word::getOrder).thenComparing(Word::getId));
//      tableWords.setItems(null);
      tableWords.setItems(FXCollections.observableArrayList(selectedWords));
    } else {
      Word selectedWord = (Word) tableWords.getSelectionModel().getSelectedItem();
      List<Word> selectedWords = tableWords.getItems();
      selectedWords.remove(selectedWord);

      // TODO ogarnąć to...
      int orderToSet = Integer.valueOf(textFieldOrder.getText());
      selectedWord.setOrder(orderToSet);
      List<Integer> orders = selectedWords.stream().map(Word::getOrder).collect(Collectors.toList());
      int indexOfOrder = orders.indexOf(orderToSet);
      if (indexOfOrder != -1) {
        for (int i = indexOfOrder; i < selectedWords.size(); i++) {
          selectedWords.get(i).setOrder(selectedWords.get(i).getOrder() + 1);
        }
        selectedWords.add(indexOfOrder, selectedWord);
      } else {
        selectedWords.add(selectedWord);
      }
      selectedWords.sort(Comparator.comparing(Word::getOrder).thenComparing(Word::getId));
      tableWords.setItems(FXCollections.observableArrayList(selectedWords));
    }
  }

  public void saveOrder(MouseEvent mouseEvent) {
    List<Word> selectedWords = tableWords.getItems();

    for (Word selectedWord : selectedWords) {
      Optional<Word> word = wordRepository.findById(selectedWord.getId());
      word.ifPresent(word1 -> {
        word1.setOrder(selectedWord.getOrder());
        word1 = wordRepository.save(word1);
        this.words.set(findWordById(word1.getId()), word1);
      });
    }

  }

  //  public void initCourseComboBox() {
  //    List<Course> courses = courseRepository.findAll();
  //    comboBoxFilterCourse.setItems(FXCollections.observableArrayList(courses));
  //  }

  public int findWordById(Long id) {
    List<Long> ids = words.stream().map(Word::getId).collect(Collectors.toList());
    return ids.indexOf(id);
  }


  private void updateRowItem(Word item, TableRow<Word> wordTableRow) {
    if (item != null && item.getLesson() != null && item.getLesson().getCourse() != null && item.getLesson().getCourse().toString()
        .contains("RELEASE")) {
      wordTableRow.setStyle("-fx-background-color: lightgreen");
    } else if (item != null && item.getLesson() != null && item.getLesson().getCourse() != null && item.getLesson().getCourse().toString()
        .contains("POZIOM")) {
//      if (audioController.getAddedSounds() != null && !audioController.getAddedSounds().isEmpty() && audioController.getAddedSounds().contains(item.getId() + ".flac")) {
//        wordTableRow.setStyle("-fx-background-color: #117009");
//      } else {
        wordTableRow.setStyle("-fx-background-color: #55ba45");
//      }
    } else if (item != null && item.getSentences() != null && !item.getSentences().isEmpty()) {
      wordTableRow.setStyle("-fx-background-color: lightblue");
    } else {
      wordTableRow.setStyle("");
    }
  }

  private void filterTable(String value) {
    if (checkBoxWholeWord.isSelected() && !value.isEmpty()) {
      if (!checkBoxAnd.isSelected()) {
        tableWords.setItems(
            FXCollections.observableArrayList(
                words.stream().filter(word ->
                    (word.getEnWord().toLowerCase().equals(textFieldSearch.getText().toLowerCase()) || word.getEnWord().toLowerCase().equals("to " + textFieldSearch.getText().toLowerCase()))
                        || word.getPlWord().toLowerCase().equals(textFieldSearchPl.getText().toLowerCase()))
                    .collect(Collectors.toList())
            )
        );
      } else {
        tableWords.setItems(
            FXCollections.observableArrayList(
                words.stream().filter(word ->
                    (word.getEnWord().toLowerCase().equals(textFieldSearch.getText().toLowerCase()) || word.getEnWord().toLowerCase().equals("to " + textFieldSearch.getText().toLowerCase()))
                    && word.getPlWord().toLowerCase().equals(textFieldSearchPl.getText().toLowerCase()))
                    .collect(Collectors.toList()))
        );
      }
    } else if (!textFieldSearch.getText().isEmpty() || !textFieldSearchPl.getText().isEmpty()) {
      if (!checkBoxAnd.isSelected()) {
        tableWords.setItems(
            FXCollections.observableArrayList(
                words.stream().filter(word ->
                        (clearString(word.getEnWord()).toLowerCase().contains(clearString(textFieldSearch.getText().toLowerCase()))
                            && !textFieldSearch.getText().isEmpty())
                            || (clearString(word.getPlWord()).toLowerCase().contains(clearString(textFieldSearchPl.getText().toLowerCase()))
                            && !textFieldSearchPl.getText().isEmpty())
                    // you are != you're
                    //                      || (word.getEnWord().replace(regex, "").toLowerCase().contains(textFieldSearch.getText().replace(regex, ""))
                    //                      && !textFieldSearch.getText().isEmpty())
                    //                      || (word.getPlWord().replace(regex, "").toLowerCase().contains(textFieldSearchPl.getText().replace(regex, ""))
                    //                      && !textFieldSearchPl.getText().isEmpty())
                )
                    .collect(Collectors.toList())
            )
        );
      } else {
        tableWords.setItems(
            FXCollections.observableArrayList(
                words.stream().filter(word ->
                    (clearString(word.getEnWord()).toLowerCase().contains(clearString(textFieldSearch.getText().toLowerCase()))
                        && !textFieldSearch.getText().isEmpty())
                        && (clearString(word.getPlWord()).toLowerCase().contains(clearString(textFieldSearchPl.getText().toLowerCase()))
                        && !textFieldSearchPl.getText().isEmpty()))
                    .collect(Collectors.toList())
            )
        );
      }
    }
  }

  //  private boolean compareString(String s1, String s2) {
  //    if (!s1.contains("'") && !s2.contains("'")) {
  //      return s1.contains(s2);
  //    } else {
  //
  //
  //
  //    }
  //  };

  private String clearString(String text) {
    text = text.replaceAll("[^a-zA-Z0-9 ']", "");
    return text;
  }

  private void filterTable2(String value) {
    if (StringUtils.isNotEmpty(value)) {
      tableWords2.setItems(
          FXCollections.observableArrayList(
              words2.stream().filter(word ->
                  (word.getEnWord().toLowerCase().contains(value.toLowerCase()) ||
                      word.getPlWord().toLowerCase().contains(value.toLowerCase()))
                      && (textFieldSearchLesson2.getText().isEmpty() || word.getLesson().getEnName()
                      .contains(textFieldSearchLesson2.getText()))
              )
                  .collect(Collectors.toList())
          )
      );
    }
  }

  private void filterTableByLesson(String value) {
    List<Word> wordList = words.stream().filter(word -> word.getLesson().getEnName().toLowerCase().contains(value.toLowerCase()))
        .collect(Collectors.toList());
    wordList.sort(Comparator.comparing(Word::getOrder).thenComparing(Word::getNextRepeat).thenComparing(Word::getId));
    tableWords.setItems(
        FXCollections.observableArrayList(wordList)
    );
  }

  private void filterTableByLesson2(String value) {
    if (StringUtils.isNotEmpty(value)) {
      tableWords2.setItems(
          FXCollections.observableArrayList(
              words2.stream().filter(word -> word.getLesson().getEnName().toLowerCase().contains(value.toLowerCase()))
                  .collect(Collectors.toList())
          )
      );
    }
  }

  private void filterTableByCourse(String value) {
    tableWords.setItems(
        FXCollections.observableArrayList(
            words.stream().filter(word -> word.getLesson().getCourse().getEnName().toLowerCase().contains(value.toLowerCase()))
                .collect(Collectors.toList())
        )
    );
  }

  //  private void initLessonComboBox(Course course) {
  //    List<Lesson> lessons = lessonRepository.findAllByCourse(course);
  //    comboBoxFilterLesson.setItems(FXCollections.observableArrayList(lessons));
  //  }

  private void filterTableByCourse2(String value) {
    if (StringUtils.isNotEmpty(value)) {
      tableWords2.setItems(
          FXCollections.observableArrayList(
              words2.stream().filter(word -> word.getLesson().getCourse().getEnName().toLowerCase().contains(value.toLowerCase()))
                  .collect(Collectors.toList())
          )
      );
    }
  }

  private void initializeTableView() {
    columnOrder.prefWidthProperty().bind(tableWords.widthProperty().multiply(0.05));
    columnId.prefWidthProperty().bind(tableWords.widthProperty().multiply(0.05));
    columnWordEn.prefWidthProperty().bind(tableWords.widthProperty().multiply(0.20));
    columnWordPl.prefWidthProperty().bind(tableWords.widthProperty().multiply(0.20));
    columnPartOfSpeech.prefWidthProperty().bind(tableWords.widthProperty().multiply(0.075));
    columnLevel.prefWidthProperty().bind(tableWords.widthProperty().multiply(0.025));
    //    columnEnSentence.prefWidthProperty().bind(tableWords.widthProperty().multiply(0.15));
    //    columnPlSentence.prefWidthProperty().bind(tableWords.widthProperty().multiply(0.15));
    columnLesson.prefWidthProperty().bind(tableWords.widthProperty().multiply(0.85));

    columnId2.prefWidthProperty().bind(tableWords2.widthProperty().multiply(0.05));
    columnWordEn2.prefWidthProperty().bind(tableWords2.widthProperty().multiply(0.20));
    columnWordPl2.prefWidthProperty().bind(tableWords2.widthProperty().multiply(0.20));
    columnPartOfSpeech2.prefWidthProperty().bind(tableWords2.widthProperty().multiply(0.075));
    columnLesson2.prefWidthProperty().bind(tableWords2.widthProperty().multiply(0.85));
  }

  public void copySentencesOnAction(ActionEvent event) {
    String id = textFieldOriginalId.getText();
    if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(wordFormController.textFieldId.getText())) {
      Word word = wordRepository.findById(Long.valueOf(id)).get();
      Word word2 = wordRepository.findById(Long.valueOf(wordFormController.textFieldId.getText())).get();

      for (Sentence sentence : word2.getSentences()) {
        sentence.setWord(word);
        sentenceRepository.save(sentence);
      }
      word = wordRepository.findById(Long.valueOf(id)).get();
      words.set(findWordById(word.getId()), word);
      word2 = wordRepository.findById(Long.valueOf(wordFormController.textFieldId.getText())).get();
      words.set(findWordById(word2.getId()), word2);
      fillInTableView();
    }
  }

  public void deleteWordOnAction(ActionEvent event) {
    if (StringUtils.isNotEmpty(wordFormController.textFieldId.getText())) {
      Word word2 = wordRepository.findById(Long.valueOf(wordFormController.textFieldId.getText())).get();

      for (Sentence sentence : word2.getSentences()) {
        sentenceRepository.delete(sentence);
      }
      word2 = wordRepository.findById(Long.valueOf(wordFormController.textFieldId.getText())).get();
      wordRepository.delete(word2);
      words.remove(findWordById(word2.getId()));
      fillInTableView();
    }
  }

  public void buttonDeleteAllOnAction(ActionEvent event) {
    List<Word> words = tableWords.getItems();
    for (Word word : words) {
      if (!word.getLesson().getCourse().getEnName().contains("RELEASE") && !word.getLesson().getCourse().getEnName().contains("POZIOM")) {

        for (Sentence sentence : word.getSentences()) {
          sentenceRepository.delete(sentence);
        }
        word = wordRepository.findById(word.getId()).get();
        log.info("Delete word: " + word.toString());
        wordRepository.delete(word);

        int index = findWordById(word.getId());
        this.words.remove(index);
      }
    }
    fillInTableView();
  }

}
