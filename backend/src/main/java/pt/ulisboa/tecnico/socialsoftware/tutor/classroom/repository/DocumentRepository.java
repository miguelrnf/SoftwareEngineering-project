package pt.ulisboa.tecnico.socialsoftware.tutor.classroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.domain.Classroom;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.domain.Document;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
}

