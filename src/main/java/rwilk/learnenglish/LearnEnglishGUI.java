package rwilk.learnenglish;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SpringBootApplication
public class LearnEnglishGUI extends Application {

  private ConfigurableApplicationContext context;
  private Parent rootNode;

  @Override
  public void init() throws Exception {
    SpringApplicationBuilder builder = new SpringApplicationBuilder(LearnEnglishGUI.class);
    context = builder.run(getParameters().getRaw().toArray(new String[0]));

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/scene/main.fxml"));
    loader.setControllerFactory(context::getBean);
    rootNode = loader.load();
    super.init();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("LearnEnglishGUI");
    primaryStage.setScene(new Scene(rootNode, 1366, 766));
    primaryStage.centerOnScreen();
    primaryStage.show();
  }

  @Override
  public void stop() throws Exception {
    context.close();
  }

}
