package org.rubilnik.auth_service.http_controllers;

import org.rubilnik.auth_service.http_controllers.HTTP_User_Controller.UserValidationInfo;
import org.rubilnik.auth_service.services.quizMemo.QuizMemoService;
import org.rubilnik.auth_service.services.userMemo.UserMemoService;
import org.rubilnik.core.quiz.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@EnableAspectJAutoProxy
@RestController
@RequestMapping("/quiz")
@CrossOrigin("*")
public class HTTP_Quiz_Controller {

    @Autowired
    QuizMemoService memo;
    // QuizRepository quizRepository;
    @Autowired
    UserMemoService userMemo;

    static class PostQuizJsonBody{ // contained values can be insuffient
        public UserValidationInfo validation;
        public Quiz quiz; // not fully initialized
    }
    @PostMapping()
    ResponseEntity<?> postQuiz(@RequestBody PostQuizJsonBody body) {
        var user = userMemo.getValid(body.validation);
        var quiz = user.createQuiz(null);
        // without back reference from collection's objects
        // @JsonBackReference does prevent infinite loops 
        // but it doesnt link objects back
        quiz.updateFrom(body.quiz);
        for (var q : quiz.getQuestions()){
            quiz.addQuestion(q);
            for (var ch : q.getChoices()){
                q.addChoice(ch);
            }
        }
        
        memo.save(quiz);
        return ResponseEntity.ok().body(quiz);
    }

    static class PutQuizJsonBody{
        public UserValidationInfo validation;
        public Quiz quiz;
    }
    @PutMapping()
    ResponseEntity<?> putQuiz(@RequestBody PutQuizJsonBody body) {
        var quiz = memo.get(body.quiz.getId(), body.validation);
        quiz.updateFrom(body.quiz);
        quiz.setDateSaved(new Date());
        memo.save(quiz);
        return ResponseEntity.ok().body(quiz); 
    }

    static class DeleteQuizJsonBody{
        public UserValidationInfo validation;
        public long id;
    }
    @DeleteMapping()
    ResponseEntity<?> deleteQuiz(@RequestBody DeleteQuizJsonBody body) {
        var quiz = memo.get(body.id, body.validation);
        memo.delete(quiz);
        return ResponseEntity.ok().build(); 
    }
}