package org.rubilnik.core.quiz;
import java.util.ArrayList;
import java.util.Date;
// import java.util.LinkedList;
import java.util.List;
// import java.util.stream.Collectors;

import org.rubilnik.core.users.User;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

// import com.fasterxml.jackson.annotation.JsonBackReference;
// import com.fasterxml.jackson.annotation.JsonManagedReference;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "quiz")
public class Quiz {
    @Id @GeneratedValue
    private long id;
    @JsonBackReference @JoinColumn @ManyToOne
    private User author;
    @Column @JsonProperty
    private String title = "new-quiz";
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "quiz") @JsonManagedReference 
    private List<Question> questions = new ArrayList<>();
    @Column @JsonProperty
    private Date dateCreated = new Date();
    @Column @JsonProperty
    private Date dateSaved = new Date();
    @Column(length = 2000) @JsonProperty
    private String startEndNodesPositions = ""; // {start:{0,0}, end:[{1,1},{2,2},{3,3}]}
    @Column(length = 10000) @JsonProperty
    private String graphEdges = ""; /* [{source:"start", target:"q1",condition:"",score:0},{},{}] */

    public void setDateSaved(Date dateSaved) {
        this.dateSaved = dateSaved;
    }
    public List<Question> getQuestions() {
        return questions;
    }
    // def for JPA
    protected Quiz(){}
    public Quiz(User author, String title){
        this.title = title;
        this.author = author;
        this.dateCreated = new Date();
        this.dateSaved = dateCreated;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) { this.id = id; }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
    public void setAuthor(User author) {
        this.author = author;
    }
    public User getAuthor() {
        return author;
    }
    public Quiz addQuestion(Question question){
        question.quiz = this;
        if (!questions.contains(question)) questions.add(question);
        return this;
    }
    void removeQuestion(int index){
        try {
            this.questions.remove(index);
        } catch (IndexOutOfBoundsException e) {
            // TODO: handle exception
        }
    }

    public void updateFrom(Quiz quiz){
        this.title = quiz.title;
        // for JPA to delete freed question entitys
        this.questions.clear(); this.questions.addAll(quiz.questions);  
        // this.questions = quiz.questions;
        this.startEndNodesPositions = quiz.startEndNodesPositions;
        this.graphEdges = quiz.graphEdges;
    }

    // public Quiz addQuestion(Question question){
    //     this.questions.add(question);
    //     return this;
    // }
    // public Quiz addQuestion(int ind, Question question){
    //     this.questions.add(ind, question);
    //     return this;
    // }
    // public Question createQuestion(String title){
    //     var q = new Question(title, new LinkedList<>());
    //     q.quiz = this;
    //     this.questions.add(q);
    //     return q;
    // }
    // Question createQuestion(int index, String title){
    //     var q = new Question(title, new LinkedList<>());
    //     q.quiz = this;
    //     this.questions.add(index,q);
    //     return q;
    // }

}