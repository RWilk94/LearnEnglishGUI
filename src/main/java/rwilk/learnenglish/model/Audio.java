package rwilk.learnenglish.model;

import java.io.File;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Audio {

  private String path;
  private File file;

}
