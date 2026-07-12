package com.example.deeploft.models;

import java.io.Serializable;
import java.util.List;

public class Quiz implements Serializable {
    private Long id;
    private String title;
    private List<Question> questions;

    public String getTitle() { return title; }
    public List<Question> getQuestions() { return questions; }
}