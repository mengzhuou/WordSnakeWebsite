package com.gtbackend.gtbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name = "word_additions")
public class WordAddition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "email", referencedColumnName = "email")
    @JsonIgnore
    private User user;

    @Column(name = "word")
    private String word;

    @Column(name = "definition", columnDefinition="TEXT")
    private String definition;

    public WordAddition() {
    }

    public WordAddition(User user, String word, String definition) {
        this.user = user;
        this.word = word;
        this.definition = definition;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonProperty("user_email")
    public String getUserEmail() {
        return user != null ? user.getEmail() : null;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
