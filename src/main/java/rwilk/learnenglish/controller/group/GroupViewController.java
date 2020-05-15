package rwilk.learnenglish.controller.group;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import rwilk.learnenglish.controller.course.CourseFormController;
import rwilk.learnenglish.controller.word.WordFormController;
import rwilk.learnenglish.model.entity.Course;
import rwilk.learnenglish.model.entity.Lesson;
import rwilk.learnenglish.repository.CourseRepository;
import rwilk.learnenglish.repository.LessonRepository;
import rwilk.learnenglish.repository.WordRepository;

@Slf4j
@Data
@Component
public class GroupViewController implements Initializable {

  public TextField textFieldRows;
  public TextField textFieldColumns;
  public Button buttonBuild;
  public VBox vBoxItems;
  public Button buttonBuildAll;
  @Autowired
  private LessonRepository lessonRepository;
  @Autowired
  private WordRepository wordRepository;
  @Autowired
  private CourseRepository courseRepository;
  @Autowired
  private WordFormController wordFormController;

  private List<GroupViewItemController> groupViewItemControllerList = new ArrayList<>();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    log.info("init GroupViewController");
  }

  public void buttonBuildOnMouseClicked(MouseEvent mouseEvent) throws IOException {

    int rows = Integer.valueOf(textFieldRows.getText());
    int columns = Integer.valueOf(textFieldColumns.getText());

    vBoxItems.getChildren().clear();
    groupViewItemControllerList = new ArrayList<>();

    for (int i = 1; i <= rows; i++) {
      HBox hBox = new HBox();
      vBoxItems.getChildren().add(hBox);
      for (int j = 1; j <= columns; j++) {

        FXMLLoader fxmlLoader = new FXMLLoader();
        VBox vBox = fxmlLoader.load(getClass().getResource("/scene/group_view_item.fxml").openStream());

        GroupViewItemController item = fxmlLoader.getController();
        item.init(wordRepository, lessonRepository, wordFormController);
        groupViewItemControllerList.add(item);

        hBox.getChildren().add(vBox);
      }
    }
  }

  public void buttonBuildAllOnMouseClicked(MouseEvent mouseEvent) {
    vBoxItems.getChildren().clear();
    groupViewItemControllerList = new ArrayList<>();

    for (int id = 33; id <= 37; id++) {
      Optional<Course> courseOptional = courseRepository.findById((long) id);
      courseOptional.ifPresent(course -> {

        List<Lesson> lessons = lessonRepository.findAllByCourse(course);
        lessons.sort(Comparator.comparing(Lesson::getOrder).thenComparing(Lesson::getId));
        AtomicInteger atomicInteger = new AtomicInteger(0);

        int size = lessons.size() + 1;
        int columns = 3;
        int rows = size / columns + 1;

        for (int i = 1; i <= rows; i++) {
          HBox hBox = new HBox();
          vBoxItems.getChildren().add(hBox);
          for (int j = 1; j <= columns; j++) {

            FXMLLoader fxmlLoader = new FXMLLoader();
            VBox vBox = null;
            try {
              vBox = fxmlLoader.load(getClass().getResource("/scene/group_view_item.fxml").openStream());
              GroupViewItemController item = fxmlLoader.getController();
              item.init(wordRepository, lessonRepository, wordFormController);
              groupViewItemControllerList.add(item);
              if (atomicInteger.get() < lessons.size()) {
                item.setLesson(lessons.get(atomicInteger.getAndIncrement()));
              } else {
                // set empty lesson
              }
              hBox.getChildren().add(vBox);
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }

//        for (Lesson lesson : lessons) {
//
//        }
//
//        wordRepository.findAllByLesson(lessons);
//        wordRepository.findAllByLesson_Course(course);

      });

    }
  }
}
