package rwilk.learnenglish.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertDialog {

  public static void showErrorAlert(String title, String contentText) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setContentText(contentText);
    alert.setHeaderText(null);
    alert.showAndWait();
  }

  public static boolean showConfirmationDialog(String title, String contentText) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setContentText(contentText);
    alert.setHeaderText(null);

    Optional<ButtonType> result = alert.showAndWait();
    return result.get() == ButtonType.OK;
  }
}