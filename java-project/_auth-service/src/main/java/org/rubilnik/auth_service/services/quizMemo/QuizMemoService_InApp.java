package org.rubilnik.auth_service.services.quizMemo;

import java.util.HashMap;
import java.util.Map;

import org.rubilnik.auth_service.http_controllers.HTTP_User_Controller.UserValidationInfo;
import org.rubilnik.core.quiz.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service @Profile("desktop")
public class QuizMemoService_InApp implements QuizMemoService {
    Map<Long,Quiz> quizzes = new HashMap<>();

    private long quizIdManager=0;
    private long questionIdManager=0;
    private long choiceIdManager=0;

    private long getChoiceId() {
        choiceIdManager++;
        return choiceIdManager;
    }
    private long getQuestionId() {
        questionIdManager++;
        return questionIdManager;
    }
    private long getQuizId() {
        quizIdManager++;
        return quizIdManager;
    }

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void save(Quiz quiz) {
        // generate IDs
        if (quiz.getId()<=0) quiz.setId(getQuizId());
        quiz.getQuestions().forEach(question->{
            if (question.getId()<=0) question.setId(getQuestionId());
            question.getChoices().forEach(choice -> {
                if (choice.getId()<=0) choice.setId(getChoiceId());
            });
        });

        quizzes.put(quiz.getId(),quiz);
    }
    @Override
    public Quiz get(long id, UserValidationInfo info) throws ResponseStatusException {
        var quiz = quizzes.get(id);
        if (quiz==null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No quiz with such id was found");
        if (!quiz.getAuthor().getId().equals(info.id)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid quiz owner");
        if (!info.password.equals(quiz.getAuthor().getPassword())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid user password");

        // if (!passwordEncoder.matches(info.password, quiz.getAuthor().getPassword())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"invalid user password");
        return quiz;
    }
    @Override
    public void delete(Quiz quiz) {
        quizzes.remove(quiz.getId());
    }
}
