package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion;

import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.dto.SuggestionDto;

@Service
public class SuggestionService {

    public SuggestionDto submitSuggestion(SuggestionDto s){
        return s;
    }
}
