package org.rubilnik.auth_service.services.quizMemo;

import org.rubilnik.auth_service.http_controllers.HTTP_User_Controller.UserValidationInfo;
import org.rubilnik.core.quiz.Quiz;
import org.springframework.web.server.ResponseStatusException;

public interface QuizMemoService {
    void save(Quiz quiz);
    Quiz get(long id, UserValidationInfo validation) throws ResponseStatusException;
    void delete(Quiz quiz);
}