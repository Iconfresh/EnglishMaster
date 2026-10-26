
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class QuestionBank {

    public static Question[] loadFromFile(String filename) {

        List<Question> questions = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(
                Paths.get(filename),
                StandardCharsets.UTF_8)) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Skip blank lines and comments
                if (line.isBlank() || line.trim().startsWith("#")) {
                    continue;
                }

                // Support both five-field and six-field formats
                String[] fields = line.split("\\|", -1);

                try {
                    String topic;
                    String difficulty;
                    String questionText;
                    String optionsText;
                    String answerText;
                    String explanation;

                    if (fields.length == 5) {
                        // Old format:
                        // topic|question|options|answer|explanation
                        topic = fields[0].trim();
                        difficulty = "Beginner";
                        questionText = fields[1].trim();
                        optionsText = fields[2];
                        answerText = fields[3].trim();
                        explanation = fields[4].trim();

                    } else if (fields.length == 6) {
                        // New format:
                        // topic|difficulty|question|options|answer|explanation
                        topic = fields[0].trim();
                        difficulty = fields[1].trim();
                        questionText = fields[2].trim();
                        optionsText = fields[3];
                        answerText = fields[4].trim();
                        explanation = fields[5].trim();

                    } else {
                        throw new IllegalArgumentException(
                                "Expected 5 or 6 fields, but found "
                                        + fields.length
                        );
                    }

                    String[] options = optionsText.split("~", -1);

                    for (int i = 0; i < options.length; i++) {
                        options[i] = options[i].trim();
                    }

                    int correctAnswer = Integer.parseInt(answerText);

                    Question question = new Question(
                            questionText,
                            options,
                            correctAnswer,
                            explanation,
                            topic,
                            difficulty
                    );

                    questions.add(question);

                } catch (IllegalArgumentException e) {
                    System.out.println(
                            "Skipping invalid question at line "
                                    + lineNumber + ": "
                                    + e.getMessage()
                    );
                }
            }

        } catch (IOException e) {
            System.out.println(
                    "Could not load question bank: "
                            + e.getMessage()
            );
        }

        System.out.println(
                "External questions loaded: " + questions.size()
        );

        return questions.toArray(new Question[0]);
    }
}
