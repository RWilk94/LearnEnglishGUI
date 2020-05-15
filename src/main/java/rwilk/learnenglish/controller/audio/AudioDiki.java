package rwilk.learnenglish.controller.audio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AudioDiki {

  public static void main(String[] args) throws IOException {
    // downloadAudio("dog");
  }

  public static void downloadAudio(String name) throws IOException {
    URLConnection conn = new URL("https://www.diki.pl/images-common/en/mp3/" + name + ".mp3").openConnection();
    InputStream is = conn.getInputStream();

    OutputStream outstream = new FileOutputStream(new File("audio/Diki/diki_en_" + name + ".mp3"));
    byte[] buffer = new byte[4096];
    int len;
    while ((len = is.read(buffer)) > 0) {
      outstream.write(buffer, 0, len);
    }
    outstream.close();

    downloadAudioUs(name);
  }

  private static void downloadAudioUs(String name) {
    try {
      URLConnection conn = new URL("https://www.diki.pl/images-common/en-ame/mp3/" + name + ".mp3").openConnection();
      InputStream is = conn.getInputStream();

      OutputStream outstream = new FileOutputStream(new File("audio/Diki/diki_us_" + name + ".mp3"));
      byte[] buffer = new byte[4096];
      int len;
      while ((len = is.read(buffer)) > 0) {
        outstream.write(buffer, 0, len);
      }
      outstream.close();
    } catch (Exception e) {
      // e.printStackTrace();
      log.error(name);
    }
  }

}
