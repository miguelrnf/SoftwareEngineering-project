package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.domain.Suggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class StatsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatsDto getStats(int userId, int executionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        StatsDto statsDto = new StatsDto();

        int totalQuizzes = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .count();

        int totalAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .map(QuizAnswer::getQuestionAnswers)
                .mapToLong(Collection::size)
                .sum();

        int uniqueQuestions = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getQuizQuestion)
                .map(QuizQuestion::getQuestion)
                .map(Question::getId)
                .distinct().count();

        int correctAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getOption)
                .filter(Objects::nonNull)
                .filter(Option::getCorrect).count();

        int uniqueCorrectAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .sorted(Comparator.comparing(QuizAnswer::getAnswerDate).reversed())
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingInt(questionAnswer -> questionAnswer.getQuizQuestion().getQuestion().getId()))),
                        ArrayList::new)).stream()
                .map(QuestionAnswer::getOption)
                .filter(Objects::nonNull)
                .filter(Option::getCorrect)
                .count();

        Course course = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId)).getCourse();



        statsDto.setApproveSuggestions(user.getNumberApprovedSuggestions());
        statsDto.setPendingSuggestions(user.getNumberToApproveSuggestions());
        statsDto.setRejectedSuggestions(user.getNumberRejectedSuggestions());
        statsDto.setTournamentDone(user.getNumberTournamentsDone());
        statsDto.setPostSubmitted(user.getNumberPostsSubmitted());

        statsDto.setTotalQuizzes(totalQuizzes);
        statsDto.setTotalAnswers(totalAnswers);
        statsDto.setTotalUniqueQuestions(uniqueQuestions);
        if (totalAnswers != 0) {
            statsDto.setCorrectAnswers(((float)correctAnswers)*100/totalAnswers);
            statsDto.setImprovedCorrectAnswers(((float)uniqueCorrectAnswers)*100/uniqueQuestions);
        }
        return statsDto;
    }

    public StatsDto getStatsEval(int userId, int executionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        StatsDto statsDto = new StatsDto();

        int totalQuizzes = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .filter(quizAnswer -> quizAnswer.getQuiz().isEvaluation())
                .count();

        int totalAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .filter(quizAnswer -> quizAnswer.getQuiz().isEvaluation())
                .map(QuizAnswer::getQuestionAnswers)
                .mapToLong(Collection::size)
                .sum();

        int uniqueQuestions = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .filter(quizAnswer -> quizAnswer.getQuiz().isEvaluation())
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getQuizQuestion)
                .map(QuizQuestion::getQuestion)
                .map(Question::getId)
                .distinct().count();

        int correctAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .filter(quizAnswer -> quizAnswer.getQuiz().isEvaluation())
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getOption)
                .filter(Objects::nonNull)
                .filter(Option::getCorrect).count();

        int uniqueCorrectAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .filter(quizAnswer -> quizAnswer.getQuiz().isEvaluation())
                .sorted(Comparator.comparing(QuizAnswer::getAnswerDate).reversed())
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingInt(questionAnswer -> questionAnswer.getQuizQuestion().getQuestion().getId()))),
                        ArrayList::new)).stream()
                .map(QuestionAnswer::getOption)
                .filter(Objects::nonNull)
                .filter(Option::getCorrect)
                .count();

        Course course = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId)).getCourse();

        int totalAvailableQuestions = questionRepository.getAvailableQuestionsSize(course.getId());


        statsDto.setApproveSuggestions(user.getNumberApprovedSuggestions());
        statsDto.setPendingSuggestions(user.getNumberToApproveSuggestions());
        statsDto.setRejectedSuggestions(user.getNumberRejectedSuggestions());
        statsDto.setTournamentDone(user.getNumberTournamentsDone());
        statsDto.setPostSubmitted(user.getNumberPostsSubmitted());

        statsDto.setTotalQuizzes(totalQuizzes);
        statsDto.setTotalAnswers(totalAnswers);
        statsDto.setTotalUniqueQuestions(uniqueQuestions);
        statsDto.setTotalAvailableQuestions(totalAvailableQuestions);
        if (totalAnswers != 0) {
            statsDto.setCorrectAnswers(((float)correctAnswers)*100/totalAnswers);
            statsDto.setImprovedCorrectAnswers(((float)uniqueCorrectAnswers)*100/uniqueQuestions);
        }
        return statsDto;
    }




    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<UserDto> getFinalEval(int userId, int courseExecutionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        if (userRepository.findById(userId).get().getRole() != User.Role.TEACHER)
            throw new TutorException(USER_HAS_WRONG_ROLE);

        courseExecution.getUsers().forEach(user -> multiplier(courseExecution, getStatsEval(user.getId(), courseExecutionId), user));

        return courseExecution.getUsers().stream().map(user -> new UserDto(user)).collect(Collectors.toList());
    }

    private int multiplier(CourseExecution courseExecution, StatsDto statsDto, User user){
        double a,b,c;

        if(statsDto.getTotalAnswers() == 0)
            a = 0;
        else
            a = ((statsDto.getCorrectAnswers() / statsDto.getTotalAnswers()) * (courseExecution.getQuizWeight() / 100.0));

        if(courseExecution.getTournaments().size()==0)
            b = courseExecution.getTournamentWeight()/100.0;
        else
            b = (statsDto.getTournamentDone() / courseExecution.getTournaments().size()) * ( (courseExecution.getTournamentWeight()) / 100.0);

        if (courseExecution.getSuggestions().size()==0)
            c = 0;
        else {
            List<Suggestion> l = courseExecution.getSuggestions().stream().filter(suggestion -> suggestion.getStatus()== Suggestion.Status.APPROVED).collect(Collectors.toList());
            if(statsDto.getApproveSuggestions()<=20)
                c = (statsDto.getApproveSuggestions() / 20) * (courseExecution.getSuggWeight() / 100.0);
            else
                c = courseExecution.getSuggWeight() / 100.0;
        }

        double res = ( a + b + c) * (courseExecution.getScale());

        user.setGrade((int) res);

        return (int) res;
    }
}
