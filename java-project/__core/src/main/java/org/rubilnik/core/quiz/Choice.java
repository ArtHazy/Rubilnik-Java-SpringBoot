package org.rubilnik.core.quiz;

import org.rubilnik.core.Position2D;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity @Table(name = "choice")
public class Choice {
    @Id @GeneratedValue @JsonProperty
    private long id;
    @Column @JsonProperty
    private String tempId;
    @Column @JsonProperty
    private String title = "new-choice";
    @Embedded @JsonProperty
    private Position2D position = new Position2D(0, 0);
    @Column @JsonProperty
    private float value = 0; // 0..1
    @JsonBackReference @ManyToOne @JoinColumn 
    Question question;

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}
    public String getTitle() {
        return title;
    }

    // def for JPA
    Choice(){}
    public Choice(String title){
        this.title = title;
    }
    public Choice(String title, float value){
        this.value = Math.max(0, Math.min(1, value));
        this.title = title;
    }
    public float getValue() {
        return value;
    }
}