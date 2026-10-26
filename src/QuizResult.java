public class QuizResult {

    private int score;
    private int correctAnswers;
    private int wrongAnswers;
    private int totalQuestions;

    public QuizResult(
            int score,
            int correctAnswers,
            int wrongAnswers,
            int totalQuestions
    ) {
        this.score = score;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.totalQuestions = totalQuestions;
    }

    public int getScore() {
        return score;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public double getPercentage() {
        if (totalQuestions == 0) {
            return 0;
        }

        return correctAnswers * 100.0 / totalQuestions;
    }

    public boolean hasPassed() {
        return getPercentage() >= 70.0;
    }
}
