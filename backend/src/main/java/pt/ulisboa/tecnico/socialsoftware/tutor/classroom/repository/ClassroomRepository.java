package pt.ulisboa.tecnico.socialsoftware.tutor.classroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.domain.Classroom;

import java.util.List;

public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {
    @Query(value = "SELECT * FROM classrooms c WHERE c.course_execution_id = :courseExecutionId", nativeQuery = true)
    List<Classroom> findClassrooms(int courseExecutionId);

}
