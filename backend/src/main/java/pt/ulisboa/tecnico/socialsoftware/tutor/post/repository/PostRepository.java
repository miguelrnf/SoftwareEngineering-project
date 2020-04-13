package pt.ulisboa.tecnico.socialsoftware.tutor.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query(value = "SELECT MAX(key) FROM posts", nativeQuery = true)
    Integer getMaxPostNumber();

    @Query(value = "SELECT * FROM posts p WHERE p.key = :key", nativeQuery = true)
    Optional<Post> findByKey(Integer key);

    @Query(value = "SELECT * FROM posts LIMIT :perPage OFFSET :offset ", nativeQuery = true)
    Optional<List<Post>> findByPage(Integer perPage, Integer offset);

    @Query(value = "SELECT COUNT(*) FROM posts", nativeQuery = true)
    Integer getTotalPosts();
}

