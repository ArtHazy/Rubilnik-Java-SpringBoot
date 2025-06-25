package org.rubilnik.auth_service.services.quizMemo;

import org.rubilnik.auth_service.record_classes.Records;
import org.rubilnik.auth_service.repositories.QuizRepository;
import org.rubilnik.core.quiz.Quiz;
import org.rubilnik.core.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service @Profile("server")
public class QuizMemoService_InDB implements QuizMemo {

    @Autowired
    QuizRepository repository;
    @Autowired
    PasswordEncoder encoder;

    @Override
    public void save(Quiz quiz) {
        repository.save(quiz);
    }
    @Override
    public Quiz get(long id, Records.UserValidationInfo validation) {
        var opt = repository.findById(id);
        if (!opt.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Quiz with such id wasn't found");
        var quiz = opt.get();
        if (!quiz.getAuthor().getId().equals(validation.id())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid quiz owner");
        if (!encoder.matches(validation.password(), quiz.getAuthor().getPassword())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"invalid user password");
        return quiz;
    }
    @Override
    public void delete(Quiz quiz) {
        repository.delete(quiz);
    }
    @Override
    public Quiz get(long id, User owner) throws ResponseStatusException {
        var quiz = repository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Quiz with such id wasn't found."));
        if (!quiz.getAuthor().getId().equals(owner.getId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid quiz owner.");
        return quiz;
    }
}
