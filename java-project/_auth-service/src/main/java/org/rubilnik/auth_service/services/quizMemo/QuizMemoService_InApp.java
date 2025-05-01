package org.rubilnik.auth_service.services.quizMemo;

import java.util.HashMap;
import java.util.Map;

import org.rubilnik.auth_service.http_controllers.HTTP_User_Controller.UserValidationInfo;
import org.rubilnik.core.quiz.Quiz;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service @Profile("desktop")
public class QuizMemoService_InApp implements QuizMemoService {
    Map<Long,Quiz> quizzes = new HashMap<>();

    @Override
    public void save(Quiz quiz) {
        quizzes.put(quiz.getId(),quiz);
    }
    @Override
    public Quiz get(long id, UserValidationInfo validation) throws ResponseStatusException {
        var q = quizzes.get(id);
        if (q==null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No quiz with such id was found");
        return q;
    }
    @Override
    public void delete(Quiz quiz) {
        quizzes.remove(quiz.getId());
    }
}
