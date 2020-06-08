package pt.ulisboa.tecnico.socialsoftware.tutor.classroom;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.domain.Classroom;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.domain.Document;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.dto.ClassroomDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.dto.DocumentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.repository.ClassroomRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.repository.DocumentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;


import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.print.Doc;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
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

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

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

        if (classroom.getCourseExecution() != courseExecution)
            throw new TutorException(COURSE_EXECUTION_MISMATCH);


        classroom.editClassroom(classroomDto);

        if (classroomDto.getQuizzes().size()>0) {
            List<Integer> statementQuizDtoList = classroomDto.getQuizzes();
            statementQuizDtoList.stream().map(statementQuizDto -> quizRepository.findById(statementQuizDto).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, statementQuizDto))).
                    forEach(quiz -> classroom.addQuiz(quiz));
        }


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
                    .map(ClassroomDto::new)
                    .sorted(Comparator.comparing(ClassroomDto::getAvailableDate, Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
        }

        else{
                return classroomRepository.findClassrooms(courseExecutionId).stream()
                    .filter(classroom -> classroom.getType().name().equals(type))
                    .map(ClassroomDto::new)
                        .sorted(Comparator.comparing(ClassroomDto::getAvailableDate, Comparator.nullsLast(Comparator.naturalOrder())))
                        .collect(Collectors.toList());
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StatementQuizDto> getClassroomAvailableQuizzes(int userId, int executionId, int classroomId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        LocalDateTime now = DateHandler.now();

        Set<Integer> studentQuizIds = user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getCourseExecution().getId() == executionId)
                .map(QuizAnswer::getQuiz)
                .map(Quiz::getId)
                .collect(Collectors.toSet());

        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow(() -> new TutorException(CLASSROOM_NOT_FOUND, classroomId));

        // create QuizAnswer for quizzes
        classroom.getQuizzes().stream()
                .filter(quiz -> !quiz.isQrCodeOnly())
                .filter(quiz -> !quiz.getType().equals(Quiz.QuizType.GENERATED) && !quiz.getType().equals(Quiz.QuizType.TOURNAMENT))
                .filter(quiz -> quiz.getAvailableDate() == null || quiz.getAvailableDate().isBefore(now))
                .filter(quiz -> !studentQuizIds.contains(quiz.getId()))
                .forEach(quiz -> {
                    if (quiz.getConclusionDate() == null || quiz.getConclusionDate().isAfter(now)) {
                        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);
                        quizAnswerRepository.save(quizAnswer);
                    }
                });

        return user.getQuizAnswers().stream()
                .filter(quizAnswer -> !quizAnswer.isCompleted())
                .filter(quizAnswer -> !quizAnswer.getQuiz().getType().equals(Quiz.QuizType.TOURNAMENT))
                .filter(quizAnswer -> !quizAnswer.getQuiz().isOneWay() || quizAnswer.getCreationDate() == null)
                .filter(quizAnswer -> quizAnswer.getQuiz().getCourseExecution().getId() == executionId)
                .filter(quizAnswer -> quizAnswer.getQuiz().getConclusionDate() == null || DateHandler.now().isBefore(quizAnswer.getQuiz().getConclusionDate()))
                .filter(quizAnswer -> quizAnswer.getQuiz().getAvailableDate().isBefore(now))
                .map(StatementQuizDto::new)
                .sorted(Comparator.comparing(StatementQuizDto::getAvailableDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public DocumentDto addDocument(int classroomId, DocumentDto documentDto){

        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow(() -> new TutorException(CLASSROOM_NOT_FOUND, classroomId));

        Document document = new Document(documentDto);
        document.editDocument(documentDto);

        classroom.addDocument(document);
        document.setClassroom(classroom);

        documentRepository.save(document);

        return new DocumentDto(document);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public byte[] upload(int executionId, int documentId, byte[] b){

        Document document = documentRepository.findById(documentId).orElseThrow(() -> new TutorException(DOCUMENT_NOT_FOUND, documentId));

        document.setPdf(b);

        return document.getPdf();
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public byte[] getFile(int executionId, int documentId){

        Document document = documentRepository.findById(documentId).orElseThrow(() -> new TutorException(DOCUMENT_NOT_FOUND, documentId));

        return document.getPdf();
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClassroomDto addQuiz(int courseExecutionId, int classroomId, StatementQuizDto statementQuizDto){
        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow(() -> new TutorException(CLASSROOM_NOT_FOUND, classroomId));

        Quiz quiz = quizRepository.findById(statementQuizDto.getId()).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, statementQuizDto.getId()));

        if(quiz.getCourseExecution() != classroom.getCourseExecution())
            throw new TutorException(COURSE_EXECUTION_MISMATCH);

        classroom.addQuiz(quiz);

        return new ClassroomDto(classroom);
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public DocumentDto editDocument(int classroomId, DocumentDto documentDto){

        Document document = documentRepository.findById(documentDto.getId()).orElseThrow(() -> new TutorException(DOCUMENT_NOT_FOUND, documentDto.getId()));

        if(classroomId != document.getClassroom().getId()){
            throw new TutorException(INVALID_CLASSROOM_ID);
        }

        document.editDocument(documentDto);

        return new DocumentDto(document);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClassroomDto changeStatus(int courseExecutionId, ClassroomDto classroomDto){
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        Classroom classroom = classroomRepository.findById(classroomDto.getId()).orElseThrow(() -> new TutorException(CLASSROOM_NOT_FOUND, classroomDto.getId()));

        if (classroom.getCourseExecution() != courseExecution)
            throw new TutorException(COURSE_EXECUTION_MISMATCH);

        classroom.setStatus(Classroom.Status.valueOf(classroomDto.getStatus()));

        return new ClassroomDto(classroom);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClassroomDto removeClassroom(int courseExecutionId, int classroomId){
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow(() -> new TutorException(CLASSROOM_NOT_FOUND, classroomId));

        if (classroom.getCourseExecution() != courseExecution)
            throw new TutorException(COURSE_EXECUTION_MISMATCH);



        classroom.remove();

        classroom.getDocuments().forEach(document -> documentRepository.delete(document));
        classroom.getDocuments().clear();

        classroomRepository.delete(classroom);

        return new ClassroomDto(classroom);
    }


    public DocumentDto removeDocument(int courseExecutionId, int classroomId, int documentId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow(() -> new TutorException(CLASSROOM_NOT_FOUND, classroomId));

        Document document = documentRepository.findById(documentId).orElseThrow(() -> new TutorException(DOCUMENT_NOT_FOUND));

        DocumentDto d = new DocumentDto(document);
        classroom.getDocuments().remove(document);
        document.setClassroom(null);

        documentRepository.delete(document);

        return d ;
    }

}
