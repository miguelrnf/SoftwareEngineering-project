package pt.ulisboa.tecnico.socialsoftware.tutor.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostComment;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface PostCommentRepository  extends JpaRepository<PostComment, Integer> {
    @Query(value = "SELECT MAX(key) FROM comments", nativeQuery = true)
    Integer getMaxPostNumber();

    @Query(value = "SELECT * FROM comments c WHERE c.key = :key", nativeQuery = true)
    Optional<PostComment> findByKey(Integer key);

    @Query(value = "SELECT * FROM comments c WHERE c.comment LIKE :comment", nativeQuery = true)
    Optional<PostComment> findByComment(String comment);
}
