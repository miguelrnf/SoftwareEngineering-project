package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.domain.Suggestion;

import java.util.Optional;

@Repository
@Transactional
public interface SuggestionRepository extends JpaRepository<Suggestion, Integer> {
    @Query(value = "SELECT * FROM suggestions s WHERE s.suggeston_key = :key", nativeQuery = true)
    Optional<Suggestion> findByKey(Integer key);

    @Query(value = "SELECT MAX(key) FROM suggestions", nativeQuery = true)
    Integer getMaxSuggestionNumber();
}
