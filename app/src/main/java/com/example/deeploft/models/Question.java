package com.example.deeploft.models;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private Long id;
    private String questionText;
    private List<String> options;
    private int correctOptionIndex;

    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
}