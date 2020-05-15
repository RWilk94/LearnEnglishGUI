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
@Table(name = "courses")
public class Course implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  Long id;
  @Column(name = "english_name", length = 2000)
  String enName;
  @Column(name = "polish_name", length = 2000)
  String plName;
  @Column(name = "level")
  Integer level;
  @Column(name = "image")
  String image;
  @Column(name = "is_custom")
  Integer isCustom;
  @Column(name = "is_ready")
  Integer isReady;
  @Column(name = "current_order", columnDefinition = "integer default 1000")
  Integer order;

  @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})//, cascade = {CascadeType.PERSIST/*, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH*/})
  private List<Lesson> lessons = new ArrayList<>();

  @Override
  public String toString() {
    return id + ". " + enName + " (" + plName + ")";
  }
}

