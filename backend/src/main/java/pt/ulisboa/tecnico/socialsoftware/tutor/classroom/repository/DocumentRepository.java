package pt.ulisboa.tecnico.socialsoftware.tutor.classroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.domain.Document;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
}

