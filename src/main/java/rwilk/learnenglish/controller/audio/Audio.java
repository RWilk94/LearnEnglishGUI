package rwilk.learnenglish.controller.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.goxr3plus.streamplayer.stream.StreamPlayer;

import ealvatag.audio.AudioFile;
import ealvatag.audio.AudioFileIO;
import ealvatag.audio.exceptions.CannotReadException;
import ealvatag.tag.FieldKey;
import ealvatag.tag.flac.FlacTag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Audio extends StreamPlayer{

  private String sourcePath = "C:\\Users\\rawi\\IdeaProjects\\LearnEnglishGUI\\audio\\Poziom 3";
  private String destinationPath = "C:\\Users\\rawi\\IdeaProjects\\LearnEnglishGUI\\audio\\Poziom 33\\";
  private String nameAlias = "forvo";

  private String language = "";

  public Audio() throws IOException {
    try (Stream<Path> walk = Files.walk(Paths.get(sourcePath))) {
      walk.forEach(item -> {
        try {
          AudioFile audioFile = AudioFileIO.read(item.toFile());
          String title = audioFile.getTag().or(new FlacTag()).getValue(FieldKey.TITLE).or("");
          if (item.getFileName().toString().contains("_us")) {
            language = "_us_";
          } else if (item.getFileName().toString().contains("uk")) {
            language = "_uk_";
          }

          title = title.replaceAll("[^a-zA-Z0-9 ]", "");
          title = title.trim().replaceAll(" ", "_");

          File source = new File(sourcePath + "\\" + item.getFileName());
          File dest = new File(destinationPath + nameAlias + language + title + ".mp3");

          copyFileUsingStream(source, dest);
        } catch (CannotReadException e) {
          e.printStackTrace();
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    }
    log.warn("FINISH");




//
//      final String title = audioFile.getTag().get().getValue(FieldKey.TITLE).or("");
//
//
//      addedSounds = walk.filter(Files::isRegularFile)
//          .map(Path::toString)
//          .map(path -> path.substring(path.lastIndexOf("\\") + 1))
//          .collect(Collectors.toList());
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//
//
//    try {
//      open(new File(audioAbsolutePath));
//      play();
//    } catch (final Exception ex) {
//      ex.printStackTrace();
//    }

  }

  public static void main(final String[] args) throws IOException {
    new Audio();
  }

  private static void copyFileUsingStream(File source, File dest) throws IOException {
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
      if (inputStream != null) {
        inputStream.close();
      }
      if (outputStream != null) {
        outputStream.close();
      }
    }
  }

}


