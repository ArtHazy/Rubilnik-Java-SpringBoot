package org.rubilnik.auth_service.services.quizMemo;

import org.rubilnik.auth_service.http_controllers.HTTP_User_Controller.UserValidationInfo;
import org.rubilnik.auth_service.repositories.QuizRepository;
import org.rubilnik.core.quiz.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service @Profile("server")
public class QuizMemoService_InDB implements QuizMemoService {

    @Autowired
    QuizRepository dbRepository;

    @Override
    public void save(Quiz quiz) {
        dbRepository.save(quiz);
    }
    @Override
    public Quiz get(long id, UserValidationInfo validation) {
        var opt = dbRepository.findById(id);
        if (!opt.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Quiz with such id wasn't found");
        var quiz = opt.get();
        if (!quiz.getAuthor().getId().equals(validation.id)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid quiz owner");
        if (!quiz.getAuthor().getPassword().equals(validation.password)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"invalid user password");
        return quiz;
    }
    @Override
    public void delete(Quiz quiz) {
        dbRepository.delete(quiz);
    }
}
