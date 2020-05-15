package rwilk.learnenglish.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Forvo {

  private String languageVersion;
  private String word;
  private String language;
  private String id;
  private long rate;

}
