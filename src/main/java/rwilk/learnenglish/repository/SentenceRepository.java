package rwilk.learnenglish.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rwilk.learnenglish.model.entity.Sentence;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {

  List<Sentence> findAllByWord_Id(Long id);

}
