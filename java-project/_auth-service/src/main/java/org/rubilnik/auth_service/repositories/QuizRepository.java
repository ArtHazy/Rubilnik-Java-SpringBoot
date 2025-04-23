package org.rubilnik.auth_service.repositories;

import org.rubilnik.core.quiz.Quiz;
import org.springframework.data.repository.CrudRepository;

public interface QuizRepository extends CrudRepository<Quiz,Long>{
    
}
