package rwilk.learnenglish.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MainController implements Initializable {

  public TabPane tabPaneForm;
  public TabPane tabPaneTable;
  private boolean selectTabPaneForm = true; // initialize by true, because it's change to false when run the app
  private boolean selectTabPaneTable = false;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }

  public void tabFormOnSelectionChanged(Event event) {
    if (tabPaneForm != null && tabPaneTable != null) {
      if (selectTabPaneTable) {
        selectTabPaneTable = false;
        switch (((Tab) event.getSource()).getText()) {
          case "Word":
            tabPaneTable.getSelectionModel().clearAndSelect(0);
            break;
          case "Lesson":
            tabPaneTable.getSelectionModel().clearAndSelect(1);
            break;
          case "Course":
            tabPaneTable.getSelectionModel().clearAndSelect(2);
            break;
        }
      } else {
        selectTabPaneTable = true;
      }
    }
  }

  public void tabTableOnSelectionChanged(Event event) {
    if (tabPaneForm != null && tabPaneTable != null) {
      if (selectTabPaneForm) {
        selectTabPaneForm = false;
        switch (((Tab) event.getSource()).getText()) {
          case "Words":
            tabPaneForm.getSelectionModel().clearAndSelect(0);
            break;
          case "Lessons":
            tabPaneForm.getSelectionModel().clearAndSelect(1);
            break;
          case "Courses":
            tabPaneForm.getSelectionModel().clearAndSelect(2);
            break;
        }
      } else {
        selectTabPaneForm = true;
      }
    }
  }

}
