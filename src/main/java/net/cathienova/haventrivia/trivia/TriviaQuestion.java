package net.cathienova.haventrivia.trivia;

import java.util.List;

public class TriviaQuestion {
    public String question;
    public List<String> answers;
    public String category;

    public TriviaQuestion(String question, List<String> answers, String category) {
        this.question = question;
        this.answers = answers;
        this.category = category;
    }
}
