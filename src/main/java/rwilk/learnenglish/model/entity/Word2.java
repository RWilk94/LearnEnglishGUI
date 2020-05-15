package rwilk.learnenglish.model.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Word2 {

  Long id;
  String plName;
  String enName; // British Name
  String usName; // American Name
  String otherNames; // rest of variants
  String partOfSpeech;
  String article;
  String grammarType; // [TRANSITIVE] – czasowniki przechodnie;                [INTRANSITIVE] – czasowniki nieprzechodnie;
                      // [COUNTABLE] – rzeczowniki policzalne;                 [UNCOUNTABLE] – rzeczowniki niepoliczalne;
                      // [SINGULAR] – rzeczowniki tylko w liczbie pojedynczej; [PLURAL] – rzeczowniki tylko w liczbie mnogiej
  String comparative; // stopień wyższy
  String superlative; // stopień najwyższy
  String pastTense; // 2 kolumna
  String pastParticiple; // 3 kolumna
  String plural;

  String enName;
  String usName;
  List<String> otherNames = new ArrayList<>();
  String comparative; // stopień wyższy
  String superlative; // stopień najwyższy
  String pastTense; // 2 kolumna
  String pastParticiple; // 3 kolumna
  String plural;
  List<Meaning> meanings = new ArrayList<>();

  String partOfSpeech;
  String plName;
  String grammarType;
  List<String> sentences;


}
