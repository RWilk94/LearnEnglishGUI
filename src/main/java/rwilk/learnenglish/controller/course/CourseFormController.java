package rwilk.learnenglish.controller.course;

import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import rwilk.learnenglish.model.entity.Course;
import rwilk.learnenglish.repository.CourseRepository;

@Component
public class CourseFormController implements Initializable {

  @Autowired
  private CourseRepository courseRepository;
  @Autowired
  private CoursesTableController coursesTableController;

  public TextField textFieldId;
  public TextField textFieldEnName;
  public TextField textFieldPlName;
  public ComboBox comboBoxLevel;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeLevelComboBox();
  }

  /**
   * Method clear all fields in form.
   * @param event an ActionEvent
   */
  public void buttonClearOnAction(ActionEvent event) {
    textFieldId.clear();
    textFieldEnName.clear();
    textFieldPlName.clear();
    comboBoxLevel.getSelectionModel().select(null);
//    comboBoxLevel.getSelectionModel().clearSelection();
  }

  /**
   * Method remove course and clear form fields.
   * @param event an ActionEvent
   */
  public void buttonDeleteOnAction(ActionEvent event) {
    if (!textFieldId.getText().isEmpty()) {
      courseRepository.findById(Long.valueOf(textFieldId.getText())).ifPresent(course -> courseRepository.delete(course));
      buttonClearOnAction(event);
      refreshTableView();
    }
  }

  /**
   * Method update course, replace form by updated values and refresh table view.
   * @param event an ActionEvent
   */
  public void buttonEditOnAction(ActionEvent event) {
    if (!textFieldId.getText().isEmpty() && !textFieldEnName.getText().isEmpty() && !textFieldPlName.getText().isEmpty() && !comboBoxLevel.getSelectionModel().isEmpty()) {
      Optional<Course> courseOptional = courseRepository.findById(Long.valueOf(textFieldId.getText()));
      courseOptional.ifPresent(course -> {
        course.setEnName(textFieldEnName.getText().trim());
        course.setPlName(textFieldPlName.getText().trim());
        course.setLevel(Integer.valueOf(comboBoxLevel.getSelectionModel().getSelectedItem().toString()));
        setCourseForm(courseRepository.save(course));
        refreshTableView();
      });
    }
  }

  /**
   * Method insert a course, fill form by inserted values and refresh table view.
   * @param event an ActionEvent
   */
  public void buttonAddOnAction(ActionEvent event) {
    if (!textFieldEnName.getText().isEmpty() && !textFieldPlName.getText().isEmpty() && !comboBoxLevel.getSelectionModel().isEmpty()) {
      Course course = Course.builder()
          .enName(textFieldEnName.getText().trim())
          .plName(textFieldPlName.getText().trim())
          .isReady(0)
          .level(Integer.valueOf(comboBoxLevel.getSelectionModel().getSelectedItem().toString()))
          .build();
      course = courseRepository.save(course);
      setCourseForm(course);
      refreshTableView();
    }
  }

  /**
   * Method initialize all fields in form value of course properties.
   * @param course a Course object which values will be used to initialize form fields
   */
  public void setCourseForm(Course course) {
    textFieldEnName.setText(course.getEnName());
    textFieldPlName.setText(course.getPlName());
    textFieldId.setText(course.getId().toString());
    comboBoxLevel.getSelectionModel().select(course.getLevel());
  }

  /**
   * Method refresh table view. The method calling fillInTableView from coursesTableController only, but its name looks better.
   */
  private void refreshTableView() {
    coursesTableController.fillInTableView();
  }

  private void initializeLevelComboBox() {
    comboBoxLevel.setItems(FXCollections.observableArrayList(Arrays.asList(1, 2, 3, 4, 5)));
  }

}
