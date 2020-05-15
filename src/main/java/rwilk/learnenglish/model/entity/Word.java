package rwilk.learnenglish.model.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "words")
public class Word implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  Long id;
  @Column(name = "polish_word", length = 2000)
  String plWord;
  @Column(name = "english_word", length = 2000)
  String enWord;
  @Column(name = "part_of_speech")
  String partOfSpeech;
  @Column(name = "polish_sentence")
  String plSentence;
  @Column(name = "english_sentence")
  String enSentence;
  @Column(name = "progress")
  Integer progress;
  @Column(name = "skip")
  Integer skip;
  @Column(name = "difficult")
  Integer difficult;
  @Column(name = "correct")
  Integer correct;
  @Column(name = "wrong")
  Integer wrong;
  @Column(name = "next_repeat")
  Long nextRepeat;
  @Column(name = "series")
  Integer series;
  @Column(name = "is_custom")
  Integer isCustom;
  @Column(name = "browse")
  Integer browse;
  @Column(name = "is_ready")
  Integer isReady;
  @Column(name = "level")
  Integer level; // means from which level is word (A1 - C2)
  @Column(name = "current_order", columnDefinition = "integer default 1000")
  Integer order;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "lesson_id", nullable = false, referencedColumnName = "id")
  private Lesson lesson;

  @OneToMany(mappedBy = "word", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  private List<Sentence> sentences;

  @Override
  public String toString() {
    return id + ". " + enWord + " (" + plWord + ")";
  }
}
