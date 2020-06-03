package pt.ulisboa.tecnico.socialsoftware.tutor.classroom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.domain.Classroom;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.domain.Document;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.dto.ClassroomDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.dto.DocumentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.repository.ClassroomRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.repository.DocumentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;


import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class ClassroomService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClassroomDto createClassroom(int courseExecutionId, ClassroomDto classroomDto){
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        Classroom classroom = new Classroom(classroomDto);

        classroom.setCourseExecution(courseExecution);
        courseExecution.addClassroom(classroom);

        classroomRepository.save(classroom);

        return new ClassroomDto(classroom);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClassroomDto editClassroom(int courseExecutionId, ClassroomDto classroomDto){
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        Classroom classroom = classroomRepository.findById(classroomDto.getId()).orElseThrow(() -> new TutorException(CLASSROOM_NOT_FOUND, classroomDto.getId()));

        classroom.editClassroom(classroomDto);

        return new ClassroomDto(classroom);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ClassroomDto> getClassroomsByType(User.Role role , int courseExecutionId, String type){

        if (role.equals(User.Role.STUDENT)){
            return classroomRepository.findClassrooms(courseExecutionId).stream()
                    .filter(classroom -> classroom.getType().name().equals(type))
                    .filter(classroom -> classroom.getStatus().equals(Classroom.Status.ACTIVE))
                    .map(classroom -> new ClassroomDto(classroom))
                    .collect(Collectors.toList());
        }

        else{
                return classroomRepository.findClassrooms(courseExecutionId).stream()
                    .filter(classroom -> classroom.getType().name().equals(type))
                    .map(classroom -> new ClassroomDto(classroom))
                    .collect(Collectors.toList());
        }
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClassroomDto addDocument(int classroomId, DocumentDto documentDto){
        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow(() -> new TutorException(CLASSROOM_NOT_FOUND, classroomId));

        Document document = new Document(documentDto);
        document.editDocument(documentDto);

        classroom.addDocument(document);
        document.setClassroom(classroom);

        documentRepository.save(document);

        return new ClassroomDto(classroom);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClassroomDto addQuiz(int classroomId, QuizDto quizDto){
        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow(() -> new TutorException(CLASSROOM_NOT_FOUND, classroomId));

        Quiz quiz = quizRepository.findByKey(quizDto.getKey()).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizDto.getKey()));

        if(quiz.getCourseExecution() != classroom.getCourseExecution())
            throw new TutorException(COURSE_EXECUTION_MISMATCH);

        classroom.addQuiz(quiz);
        quiz.setClassroom(classroom);

        return new ClassroomDto(classroom);
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClassroomDto editDocument(int classroomId, DocumentDto documentDto){
        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow(() -> new TutorException(CLASSROOM_NOT_FOUND, classroomId));

        Document document = documentRepository.findById(documentDto.getId()).orElseThrow(() -> new TutorException(DOCUMENT_NOT_FOUND, documentDto.getId()));

        if(classroomId != document.getClassroom().getId()){
            throw new TutorException(INVALID_CLASSROOM_ID);
        }

        document.editDocument(documentDto);

        return new ClassroomDto(classroom);
    }














}
