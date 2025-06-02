package org.rubilnik.auth_service.http_controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rubilnik.auth_service.App;
import org.rubilnik.auth_service.record_classes.Records;
import org.rubilnik.auth_service.services.quizMemo.QuizMemoService;
import org.rubilnik.auth_service.services.userMemo.UserMemoService;
import org.rubilnik.core.quiz.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@SpringBootApplication
@EnableAspectJAutoProxy
@RestController
@RequestMapping("/quiz")
//@CrossOrigin("*")
public class HTTP_Quiz_Controller {
    @Autowired
    QuizMemoService memo;
    @Autowired
    UserMemoService userMemo;

    static class PostQuizJsonBody{ // contained values can be insuffient
        public Quiz quiz; // not fully initialized
    }
    @PostMapping()
    ResponseEntity<?> postQuiz(@RequestBody PostQuizJsonBody body, @CookieValue(value="Authorization", required=false) String token) {
        App.logObjectAsJson(body);
        var user = userMemo.getValid(token);
        var quiz = user.createQuiz(null);
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
        public Quiz quiz;
    }
    @PutMapping()
    ResponseEntity<?> putQuiz(@RequestBody PutQuizJsonBody body, @CookieValue(value="Authorization", required=false) String token) {
        App.logObjectAsJson(body);
        var quiz = memo.get(body.quiz.getId(), userMemo.getValid(token));
        quiz.updateFrom(body.quiz);
        quiz.setDateSaved(new Date());
        memo.save(quiz);
        return ResponseEntity.ok().body(quiz); 
    }

    static class DeleteQuizJsonBody{
        public long id;
    }
    @DeleteMapping()
    ResponseEntity<?> deleteQuiz(@RequestBody DeleteQuizJsonBody body, @CookieValue(value="Authorization", required=false) String token) {
        App.logObjectAsJson(body);
        var quiz = memo.get(body.id, userMemo.getValid(token));
        memo.delete(quiz);
        return ResponseEntity.ok().build(); 
    }
}