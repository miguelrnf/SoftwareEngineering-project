package pt.ulisboa.tecnico.socialsoftware.tutor.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostQuestion;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface PostQuestionRepository extends JpaRepository<PostQuestion, Integer> {
}

