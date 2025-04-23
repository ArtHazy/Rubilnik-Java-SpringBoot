package org.rubilnik.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Embeddable;

@Embeddable
public class Position2D {
    @JsonProperty
    int x;
    @JsonProperty
    int y;
    // for JPA
    public Position2D(){}

    public Position2D(int x, int y) {
        this.x = x;
        this.y = y;
    }
}