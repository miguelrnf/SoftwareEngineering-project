package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion;

import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.dto.SuggestionDto;

@Service
public class SuggestionService {

    public SuggestionDto submitSuggestion(int courseId, SuggestionDto s){
        return s;
    }

    public SuggestionDto createSuggestion(int courseId, SuggestionDto s){
        return s;
    }
}
