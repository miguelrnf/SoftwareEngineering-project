package pt.ulisboa.tecnico.socialsoftware.tutor.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query(value = "SELECT MAX(key) FROM posts", nativeQuery = true)
    Integer getMaxPostNumber();

    @Query(value = "SELECT * FROM posts p WHERE p.key = key", nativeQuery = true)
    Post findByKey(Integer key);
}
