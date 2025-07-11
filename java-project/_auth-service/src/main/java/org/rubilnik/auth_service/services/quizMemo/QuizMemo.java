package org.rubilnik.auth_service.services.quizMemo;

import org.rubilnik.auth_service.record_classes.Records;
import org.rubilnik.core.quiz.Quiz;
import org.rubilnik.core.users.User;
import org.springframework.web.server.ResponseStatusException;

public interface QuizMemo {
    void save(Quiz quiz);
    Quiz get(long id, Records.UserValidationInfo validation) throws ResponseStatusException;
    void delete(Quiz quiz);
    Quiz get(long id, User owner) throws ResponseStatusException;
}