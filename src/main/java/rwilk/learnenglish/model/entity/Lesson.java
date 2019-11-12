package rwilk.learnenglish.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
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
@Table(name = "lessons")
public class Lesson implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  Long id;
  @Column(name = "english_name")
  String enName;
  @Column(name = "polish_name")
  String plName;
  @Column(name = "image")
  String image;
  @Column(name = "is_custom")
  Integer isCustom;
  @Column(name = "is_ready")
  Integer isReady;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "course_id", nullable = false, referencedColumnName = "id")
  Course course;

  @OneToMany(mappedBy = "lesson", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH}) //lesson is name of column declared in Word model.
  private List<Word> words = new ArrayList<>();

  @Override
  public String toString() {
    return "Lesson{" +
        "id=" + id +
        ", enName='" + enName + '\'' +
        ", plName='" + plName + '\'' +
        ", isReady=" + isReady +
        '}';
  }
}
