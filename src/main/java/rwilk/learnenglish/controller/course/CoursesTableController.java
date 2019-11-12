package rwilk.learnenglish.controller.course;

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
import rwilk.learnenglish.model.entity.Course;
import rwilk.learnenglish.repository.CourseRepository;

@Component
public class CoursesTableController implements Initializable {

  @Autowired
  private CourseRepository courseRepository;
  @Autowired
  private CourseFormController courseFormController;

  public TextField textFieldSearch;
  public Button buttonSearch;
  public TableView tableCourses;
  public TableColumn columnId;
  public TableColumn columnEnName;
  public TableColumn columnPlName;
  public TableColumn columnLevel;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();
    fillInTableView();
  }

  private void initializeTableView() {
    columnId.prefWidthProperty().bind(tableCourses.widthProperty().multiply(0.05));
    columnEnName.prefWidthProperty().bind(tableCourses.widthProperty().multiply(0.425));
    columnPlName.prefWidthProperty().bind(tableCourses.widthProperty().multiply(0.425));
    columnLevel.prefWidthProperty().bind(tableCourses.widthProperty().multiply(0.1));
  }

  public void fillInTableView() {
    tableCourses.setItems(FXCollections.observableArrayList(courseRepository.findAll()));
  }

  public void tableViewOnMouseClicked(MouseEvent mouseEvent) {
    if (!tableCourses.getSelectionModel().isEmpty()) {
      Course selectedCourse = (Course) tableCourses.getSelectionModel().getSelectedItem();
      courseFormController.setCourseForm(selectedCourse);
    }
  }

}
