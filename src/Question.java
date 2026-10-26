
import java.util.concurrent.ThreadLocalRandom;

public class Question {

    // The actual question
    private String questionText;

    // The answer options
    private String[] options;

    // The correct option number (1-based)
    private int correctAnswer;

    // Explanation of the correct answer
    private String explanation;

    // Topic associated with the question
    private String topic;

    private String difficulty;

    // Constructor with default explanation and topic
    public Question(
            String questionText,
            String[] options,
            int correctAnswer
    ) {
        this(
                questionText,
                options,
                correctAnswer,
                "Review the correct answer and the rule behind it.",
                "General English",
                "Beginner"
        );
    }

    // Constructor with a custom explanation
    public Question(
            String questionText,
            String[] options,
            int correctAnswer,
            String explanation
    ) {
        this(
                questionText,
                options,
                correctAnswer,
                explanation,
                "General English",
                "Beginner"
        );
    }

    // Constructor with a custom explanation and topic
    public Question(
            String questionText,
            String[] options,
            int correctAnswer,
            String explanation,
            String topic
    ) {
        this(
                questionText,
                options,
                correctAnswer,
                explanation,
                topic,
                "Beginner"
                );
    }
    //New constructor with topic and difficulty
    public Question(
            String questionText,
            String[] options,
            int correctAnswer,
            String explanation,
            String topic,
            String difficulty) {

        if (questionText == null || questionText.isBlank()) {
            throw new IllegalArgumentException(
                    "Question text cannot be empty."
            );
        }

        if (options == null || options.length < 2) {
            throw new IllegalArgumentException(
                    "A question must have at least two options."
            );
        }

        for (String option : options) {
            if (option == null || option.isBlank()) {
                throw new IllegalArgumentException(
                        "Answer options cannot be empty."
                );
            }
        }

        if (correctAnswer < 1 || correctAnswer > options.length) {
            throw new IllegalArgumentException(
                    "Correct answer must be between 1 and "
                            + options.length + "."
            );
        }

        if(difficulty == null
        || !(difficulty.equalsIgnoreCase("Beginner")
        || difficulty.equalsIgnoreCase("Intermediate")
        || difficulty.equalsIgnoreCase("Advanced"))) {
            throw new IllegalArgumentException("Difficulty must be Beginner, Intermediate, or Advanced. ");
        }

        this.questionText = questionText;
        this.options = options.clone();
        this.correctAnswer = correctAnswer;

        this.explanation = explanation == null
                        ? "Review the correct answer and the rule behind it."
                        : explanation;

        this.topic = topic == null || topic.isBlank()
                        ? "General English"
                        : topic;
        this.difficulty = difficulty.trim();


    // Get the question text
    //public String getQuestionText() {
      //  return questionText;
    }

    // Get a copy of the answer options
    public String[] getOptions() {
        return options.clone();
    }

    // Get the correct answer number
    public int getCorrectAnswer() {
        return correctAnswer;
    }

    // Get the explanation
    public String getExplanation() {
        return explanation;
    }

    // Get the topic
    public String getTopic() {
        return topic;
    }

    // Get Difficulty
    public String getDifficulty() {
        return difficulty;
    }
    public String getQuestionText() {
        return questionText;
    }




    // Shuffle options while keeping the correct answer aligned
    public void shuffleOptions() {
        int correctIndex = correctAnswer - 1;

        // Fisher-Yates shuffle
        for (int i = options.length - 1; i > 0; i--) {
            int j = ThreadLocalRandom.current().nextInt(i + 1);

            // Track the correct answer's new position
            if (correctIndex == i) {
                correctIndex = j;
            } else if (correctIndex == j) {
                correctIndex = i;
            }

            // Swap the options
            String temp = options[i];
            options[i] = options[j];
            options[j] = temp;
        }

        // Store the correct answer's new position
        correctAnswer = correctIndex + 1;
    }
}
