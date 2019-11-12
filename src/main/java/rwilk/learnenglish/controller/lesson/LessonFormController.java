package rwilk.learnenglish.controller.lesson;

import java.net.URL;
import java.util.List;
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
import rwilk.learnenglish.model.entity.Lesson;
import rwilk.learnenglish.repository.CourseRepository;
import rwilk.learnenglish.repository.LessonRepository;

@Component
public class LessonFormController implements Initializable {

  public TextField textFieldId;
  public TextField textFieldEnName;
  public TextField textFieldPlName;
  public ComboBox comboBoxCourse;
  @Autowired
  private LessonRepository lessonRepository;
  @Autowired
  private CourseRepository courseRepository;
  @Autowired
  private LessonsTableController lessonsTableController;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeCourseComboBox();
  }

  public void buttonClearOnAction(ActionEvent event) {
    textFieldEnName.clear();
    textFieldPlName.clear();
    textFieldId.clear();
    comboBoxCourse.getSelectionModel().select(null);
//    comboBoxCourse.getItems().clear();
  }

  public void buttonDeleteOnAction(ActionEvent event) {
    if (!textFieldId.getText().isEmpty()) {
      lessonRepository.findById(Long.valueOf(textFieldId.getText())).ifPresent(lesson -> lessonRepository.delete(lesson));
      buttonClearOnAction(event);
      refreshTableView();
    }
  }

  public void buttonEditOnAction(ActionEvent event) {
    if (!textFieldId.getText().isEmpty() && !textFieldEnName.getText().isEmpty() && !textFieldPlName.getText().isEmpty() && !comboBoxCourse
        .getSelectionModel().isEmpty()) {
      Optional<Lesson> lessonOptional = lessonRepository.findById(Long.valueOf(textFieldId.getText()));
      lessonOptional.ifPresent(lesson -> {
        lesson.setEnName(textFieldEnName.getText().trim());
        lesson.setPlName(textFieldPlName.getText().trim());
        lesson.setCourse((Course) comboBoxCourse.getSelectionModel().getSelectedItem());
        setLessonForm(lessonRepository.save(lesson));
        refreshTableView();
      });
    }
  }

  public void buttonAddOnAction(ActionEvent event) {
    if (!textFieldEnName.getText().isEmpty() && !textFieldPlName.getText().isEmpty() && !comboBoxCourse.getSelectionModel().isEmpty()) {
      Lesson lesson = Lesson.builder()
          .enName(textFieldEnName.getText().trim())
          .plName(textFieldPlName.getText().trim())
          .isReady(0)
          .course(courseRepository.findById(((Course) comboBoxCourse.getSelectionModel().getSelectedItem()).getId()).get())
          .build();
      lesson = lessonRepository.save(lesson);
      setLessonForm(lesson);
      refreshTableView();
    }
  }

  public void setLessonForm(Lesson lesson) {
    textFieldEnName.setText(lesson.getEnName());
    textFieldPlName.setText(lesson.getPlName());
    textFieldId.setText(lesson.getId().toString());
    comboBoxCourse.getSelectionModel().select(lesson.getCourse());
  }

  public void buttonRefreshOnAction(ActionEvent event) {
    comboBoxCourse.getItems().clear();
    initializeCourseComboBox();
    refreshTableView();
  }

  private void refreshTableView() {
    lessonsTableController.fillInTableView();
  }

  private void initializeCourseComboBox() {
    List<Course> courses = courseRepository.findAll();
    comboBoxCourse.setItems(FXCollections.observableArrayList(courses));
  }
}
