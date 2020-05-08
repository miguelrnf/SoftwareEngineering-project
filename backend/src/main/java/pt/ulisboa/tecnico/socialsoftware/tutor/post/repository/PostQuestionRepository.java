package pt.ulisboa.tecnico.socialsoftware.tutor.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostQuestion;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface PostQuestionRepository extends JpaRepository<PostQuestion, Integer> {
    @Query(value = "SELECT * FROM post_questions WHERE user_id = :userid", nativeQuery = true)
    List<PostQuestion> findByUser(Integer userid);
    @Query(value = "SELECT * FROM post_questions WHERE question_id=:qid", nativeQuery = true)
    Optional<List<PostQuestion>> findByQuestion(Integer qid);
}

