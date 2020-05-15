package rwilk.learnenglish.controller.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerException;

import ealvatag.audio.AudioFile;
import ealvatag.audio.AudioFileIO;
import ealvatag.audio.exceptions.CannotReadException;
import ealvatag.audio.exceptions.InvalidAudioFrameException;
import ealvatag.tag.FieldKey;
import ealvatag.tag.TagException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import rwilk.learnenglish.controller.word.WordFormController;
import rwilk.learnenglish.controller.word.WordsTableController;

@Slf4j
@Component
public class AudioController extends StreamPlayer implements Initializable {

  @Autowired
  private WordFormController wordFormController;
  @Autowired
  private WordsTableController wordsTableController;
  public TextField textFieldSound;
  public ListView listViewSounds;
  public TextField textFieldSearch;
  @Getter
  private List<String> result;
  @Getter
  private List<String> addedSounds;

  public static void main(String[] args) throws TagException, CannotReadException, InvalidAudioFrameException, IOException {

    File inputFile = new File("En-us-abandon.flac");
    AudioFile audioFile = AudioFileIO.read(inputFile);

    final String title = audioFile.getTag().get().getValue(FieldKey.TITLE).or("");
    // System.out.println(title);

    try (Stream<Path> walk = Files.walk(Paths.get("C:\\Users\\rawi\\IdeaProjects\\LearnEnglishGUI\\audio\\Wiki"))) {

      List<String> result = walk.filter(Files::isRegularFile)
          .map(Path::toString)
          .map(path -> path.substring(path.lastIndexOf("\\") + 1))
          .collect(Collectors.toList());

      result.forEach(System.out::println);

    } catch (IOException e) {
      e.printStackTrace();
    }
    //    File inputFile2 = new File("En-us-abandon.flac");
    //    final Media media = new Media("name.mp3");
    //    final MediaPlayer mediaPlayer = new MediaPlayer(media);
    //    mediaPlayer.play();

    String bip = "name.mp3";
    Media hit = new Media(new File(bip).toURI().toString());
    MediaPlayer mediaPlayer = new MediaPlayer(hit);
    mediaPlayer.play();

    // Player

    //    new Thread(new Runnable() {
    //      // The wrapper thread is unnecessary, unless it blocks on the
    //      // Clip finishing; see comments.
    //      public void run() {
    //        try {
    //          Clip clip = AudioSystem.getClip();
    //          AudioInputStream inputStream = AudioSystem.getAudioInputStream(
    //              AudioController.class.getResourceAsStream("name.mp3"));
    //          clip.open(inputStream);
    //          clip.start();
    //        } catch (Exception e) {
    //          System.err.println(e.getMessage());
    //        }
    //      }
    //    }).start();

  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initSounds();
    initListView();

    textFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> listViewSounds.setItems(
        FXCollections.observableArrayList(
            result.stream()
                .map(path -> path.substring(path.lastIndexOf("\\") + 1))
                .filter(path -> path.toLowerCase().contains(newValue.toLowerCase()))
                .collect(Collectors.toList())
        )
    ));
  }

  public void listViewSoundsOnMouseClicked(MouseEvent mouseEvent) throws StreamPlayerException {
    String sound = (String) listViewSounds.getSelectionModel().getSelectedItem();
    if (StringUtils.isNotEmpty(sound)) {
      textFieldSound.setText(sound);
      sound = "audio\\wiki\\" + sound;
      File file = new File(sound);
      open(file);
      play();
    }
  }

  public void buttonSetSoundOnAction(ActionEvent event) throws IOException {
    String path = textFieldSound.getText();
    if (StringUtils.isNotEmpty(path) && StringUtils.isNotEmpty(wordFormController.textFieldId.getText())) {
      File source = new File("audio\\wiki\\" + path);
      File dest = new File("audio\\Android\\" + wordFormController.textFieldId.getText() + ".flac");
      copyFileUsingStream(source, dest);
    }
  }

  private void initSounds() {
    try (Stream<Path> walk = Files.walk(Paths.get("C:\\Users\\rawi\\IdeaProjects\\LearnEnglishGUI\\audio\\Wiki"))) {
      result = walk.filter(Files::isRegularFile)
          .map(Path::toString)
          .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }
    initAddedSounds();
  }

  private void initAddedSounds() {
    try (Stream<Path> walk = Files.walk(Paths.get("C:\\Users\\rawi\\IdeaProjects\\LearnEnglishGUI\\audio\\Android"))) {
      addedSounds = walk.filter(Files::isRegularFile)
          .map(Path::toString)
          .map(path -> path.substring(path.lastIndexOf("\\") + 1))
          .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initListView() {
    List<String> names = result.stream()
        .map(path -> path.substring(path.lastIndexOf("\\") + 1))
        .collect(Collectors.toList());
    listViewSounds.setItems(FXCollections.observableArrayList(names));
  }

  private void copyFileUsingStream(File source, File dest) throws IOException {
    InputStream inputStream = null;
    OutputStream outputStream = null;
    try {
      inputStream = new FileInputStream(source);
      outputStream = new FileOutputStream(dest);
      byte[] buffer = new byte[4096];
      int length;
      while ((length = inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, length);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      inputStream.close();
      outputStream.close();
    }
    initAddedSounds();
    wordsTableController.tableWords.refresh();
    // wordsTableController.fillInTableView();
  }

  public void setSearchField(String text) {
    textFieldSearch.setText(text.trim().replaceAll("[^a-zA-Z0-9 ]", "").replaceAll(" ", "_"));
  }
}
