package org.rubilnik.auth_service.services.quizMemo;

import org.rubilnik.auth_service.record_classes.Records;
import org.rubilnik.auth_service.repositories.QuizRepository;
import org.rubilnik.core.quiz.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service @Profile("server")
public class QuizMemoService_InDB implements QuizMemoService {

    @Autowired
    QuizRepository dbRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void save(Quiz quiz) {
        dbRepository.save(quiz);
    }
    @Override
    public Quiz get(long id, Records.UserValidationInfo validation) {
        var opt = dbRepository.findById(id);
        if (!opt.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Quiz with such id wasn't found");
        var quiz = opt.get();
        if (!quiz.getAuthor().getId().equals(validation.id())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid quiz owner");
        if (!passwordEncoder.matches(validation.password(), quiz.getAuthor().getPassword())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"invalid user password");
        return quiz;
    }
    @Override
    public void delete(Quiz quiz) {
        dbRepository.delete(quiz);
    }
}
