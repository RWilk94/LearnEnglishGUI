package rwilk.learnenglish.controller.lesson;

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
import rwilk.learnenglish.model.entity.Lesson;
import rwilk.learnenglish.repository.LessonRepository;

@Component
public class LessonsTableController implements Initializable {

  public TextField textFieldSearch;
  public Button buttonSearch;
  public TableView tableLessons;
  public TableColumn columnId;
  public TableColumn columnEnName;
  public TableColumn columnPlName;
  public TableColumn columnCourse;
  @Autowired
  private LessonRepository lessonRepository;
  @Autowired
  private LessonFormController lessonFormController;
  private List<Lesson> lessons;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();
    fillInTableView();

    textFieldSearch.setOnMouseClicked(event -> filterTable(textFieldSearch.getText()));

    textFieldSearch.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        filterTable(newValue);
      }
    });

  }

  private void filterTable(String newValue) {
    tableLessons.setItems(
        FXCollections.observableArrayList(
            lessons.stream().filter(lesson -> lesson.toString().toLowerCase().contains(newValue.toLowerCase())
                || lesson.getCourse().toString().toLowerCase().contains(newValue.toLowerCase()))
                .collect(Collectors.toList())
        )
    );
  }

  public void fillInTableView() {
    lessons = lessonRepository.findAll();
    tableLessons.setItems(FXCollections.observableArrayList(lessons));
  }

  public void tableViewOnMouseClicked(MouseEvent mouseEvent) {
    if (!tableLessons.getSelectionModel().isEmpty()) {
      Lesson selectedLesson = (Lesson) tableLessons.getSelectionModel().getSelectedItem();
      lessonFormController.setLessonForm(selectedLesson);
    }
  }

  private void initializeTableView() {
    columnId.prefWidthProperty().bind(tableLessons.widthProperty().multiply(0.05));
    columnEnName.prefWidthProperty().bind(tableLessons.widthProperty().multiply(0.30));
    columnPlName.prefWidthProperty().bind(tableLessons.widthProperty().multiply(0.30));
    columnCourse.prefWidthProperty().bind(tableLessons.widthProperty().multiply(0.35));
  }
}
