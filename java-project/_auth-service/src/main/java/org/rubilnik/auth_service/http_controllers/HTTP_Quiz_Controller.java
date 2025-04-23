package org.rubilnik.auth_service.http_controllers;

import org.rubilnik.auth_service.http_controllers.HTTP_User_Controller.UserValidationInfo;
import org.rubilnik.auth_service.repositories.QuizRepository;
import org.rubilnik.auth_service.repositories.UserRepository;
import org.rubilnik.core.quiz.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@SpringBootApplication
@EnableAspectJAutoProxy
@RestController
@RequestMapping("/quiz")
@CrossOrigin("*")
public class HTTP_Quiz_Controller {

    @Autowired
    QuizRepository quizRepository;
    @Autowired
    UserRepository userRepository;

    static class PostQuizJsonBody{ // contained values can be insuffient
        public UserValidationInfo validation;
        public Quiz quiz; // not fully initialized
    }
    @PostMapping()
    ResponseEntity<?> postQuiz(@RequestBody PostQuizJsonBody body) throws Exception {
        var opt = userRepository.findById(body.validation.id);
        if (!opt.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User with such id wasn't found");
        var user = opt.get();
        if (!user.getPassword().equals(body.validation.password)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid user password");
        // without back reference from collection's objects
        // @JsonBackReference does prevent infinite loops 
        // but it doesnt link objects back
        var quiz = user.claimQuiz(body.quiz);
        for (var q : quiz.getQuestions()){
            quiz.addQuestion(q);
            for (var ch : q.getChoices()){
                q.addChoice(ch);
            }
        }
        quizRepository.save(quiz);
        return ResponseEntity.ok().body(quiz);
    }

    static class PutQuizJsonBody{
        public UserValidationInfo validation;
        public Quiz quiz;
    }
    @PutMapping()
    ResponseEntity<?> putQuiz(@RequestBody PutQuizJsonBody body) throws Exception {
        var opt = quizRepository.findById(body.quiz.getId());
        if (!opt.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Quiz with such id wasn't found");
        var quiz = opt.get();
        if (!quiz.getAuthor().getId().equals(body.validation.id)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid quiz owner");
        if (!quiz.getAuthor().getPassword().equals(body.validation.password)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid user password");
        quiz.setDateSaved(new Date());
        quizRepository.save(quiz);
        return ResponseEntity.ok().body(body.quiz); 
    }

    static class DeleteQuizJsonBody{
        public UserValidationInfo validation;
        public long id;
    }
    @DeleteMapping()
    ResponseEntity<?> deleteQuiz(@RequestBody DeleteQuizJsonBody body) throws Exception {
        var opt = quizRepository.findById(body.id);
        if (!opt.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Quiz with such id wasn't found");
        var quiz = opt.get();
        if (!quiz.getAuthor().getId().equals(body.validation.id)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid quiz owner");
        if (!quiz.getAuthor().getPassword().equals(body.validation.password)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"invalid user password"); 
        quizRepository.delete(quiz);
        return ResponseEntity.ok().build(); 
    }
}