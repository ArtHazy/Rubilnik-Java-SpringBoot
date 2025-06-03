package org.rubilnik.auth_service.http_controllers;

import org.rubilnik.auth_service.App;
import org.rubilnik.auth_service.services.CurrentHttpSessionUserResolver;
import org.rubilnik.auth_service.services.quizMemo.QuizMemoService;
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
    CurrentHttpSessionUserResolver clientResolver;

    static class PostQuizJsonBody{ // contained values can be insuffient
        public Quiz quiz; // not fully initialized
    }
    @PostMapping()
    ResponseEntity<?> postQuiz(@RequestBody PostQuizJsonBody body) {
        App.logObjectAsJson(body);
        var user = clientResolver.getCurrent();
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
    ResponseEntity<?> putQuiz(@RequestBody PutQuizJsonBody body) {
        App.logObjectAsJson(body);
        var quiz = memo.get(body.quiz.getId(), clientResolver.getCurrent());
        quiz.updateFrom(body.quiz);
        quiz.setDateSaved(new Date());
        memo.save(quiz);
        return ResponseEntity.ok().body(quiz); 
    }

    static class DeleteQuizJsonBody{
        public long id;
    }
    @DeleteMapping()
    ResponseEntity<?> deleteQuiz(@RequestBody DeleteQuizJsonBody body) {
        App.logObjectAsJson(body);
        var quiz = memo.get(body.id, clientResolver.getCurrent());
        memo.delete(quiz);
        return ResponseEntity.ok().build(); 
    }
}