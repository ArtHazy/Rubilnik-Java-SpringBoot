package org.rubilnik.core.quiz;

import java.util.ArrayList;
import java.util.List;

import org.rubilnik.core.utils.Position2D;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "question")
public class Question {
    @Id @GeneratedValue @JsonProperty
    private long id;
    @Column @JsonProperty
    private String tempId; // unique in one quiz, can repeat between others // created by frontend before generating database id
    @Column @JsonProperty
    private String title = "new-question";
    @Embedded @JsonProperty
    private Position2D position = new Position2D(0, 0);
    @JsonBackReference @ManyToOne @JoinColumn 
    Quiz quiz;
    @JsonManagedReference @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "question")
    private List<Choice> choices = new ArrayList<>();

    public long getId() {return id;}

    public void setId(long id) {this.id = id;}

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
    public List<Choice> getChoices() {
        return choices;
    }
    // Jackson uses getters to form json from java class. This method will create correctChoices property in json
    // if java class field doesnt have getter, @JsonProperty can be used to make it visible for Jackson
    // public List<Choice> getCorrectChoices(){
    //     return choices.stream().filter( c->(c.getValue()>0) ).collect(Collectors.toList());
    // }


    // def for JPA
    Question(){}
    public Question(String title){
        this.title = title;
    }
    public Question(String title, List<Choice> list){
        this.title = title;
        this.choices = list;
    }
    public Question addChoice(Choice choice){
        choice.question = this;
        if (!choices.contains(choice)) choices.add(choice);
        return this;
    }
    void removeChoice(int index){
        try {
            choices.remove(index);
        } catch (IndexOutOfBoundsException e) {}
    }
    void removeChoice(Choice choice){
        choices.remove(choice);
    }
}
