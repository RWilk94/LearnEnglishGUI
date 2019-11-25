package rwilk.learnenglish.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemriseWord {

  private String enWord;
  private String plWord;

  @Override
  public String toString() {
    return enWord + " (" + plWord + ")";
  }
}
