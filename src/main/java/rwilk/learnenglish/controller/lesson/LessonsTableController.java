package rwilk.learnenglish.controller.lesson;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

  @Autowired
  private LessonRepository lessonRepository;
  @Autowired
  private LessonFormController lessonFormController;

  public TextField textFieldSearch;
  public Button buttonSearch;
  public TableView tableLessons;
  public TableColumn columnId;
  public TableColumn columnEnName;
  public TableColumn columnPlName;
  public TableColumn columnCourse;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();
    fillInTableView();
  }

  private void initializeTableView() {
    columnId.prefWidthProperty().bind(tableLessons.widthProperty().multiply(0.05));
    columnEnName.prefWidthProperty().bind(tableLessons.widthProperty().multiply(0.30));
    columnPlName.prefWidthProperty().bind(tableLessons.widthProperty().multiply(0.30));
    columnCourse.prefWidthProperty().bind(tableLessons.widthProperty().multiply(0.35));
  }

  public void fillInTableView() {
    tableLessons.setItems(FXCollections.observableArrayList(lessonRepository.findAll()));
  }

  public void tableViewOnMouseClicked(MouseEvent mouseEvent) {
    if (!tableLessons.getSelectionModel().isEmpty()) {
      Lesson selectedLesson = (Lesson) tableLessons.getSelectionModel().getSelectedItem();
      lessonFormController.setLessonForm(selectedLesson);
    }
  }
}
