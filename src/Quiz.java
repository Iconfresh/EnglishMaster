import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Quiz {

    // Store all questions for this quiz
    private Question[] questions;

    // Store the player's score
    private int score;

    // Count correct answers
    private int correctAnswers;

    // Count wrong answers
    private int wrongAnswers;

    private List<Question> incorrectQuestions = new ArrayList<>();

    //Constructor
    public Quiz(Question[] questions) {
        if (questions == null || questions.length == 0) {
            throw new IllegalArgumentException("A quiz must contain at least one question.");
        }

        this.questions = questions;

        // Start everything at Zero
        this.score = 0;
        this.correctAnswers = 0;
        this.wrongAnswers = 0;

    }

    // Start the Quiz
    public QuizResult start(Scanner scanner) {
        // Reset results for a new attempt
        score = 0;
        correctAnswers = 0;
        wrongAnswers = 0;
        incorrectQuestions.clear();

        // Randomize the question order
        Collections.shuffle(Arrays.asList(questions));

        //Go through every question
        for (int i = 0; i < questions.length; i++) {

            //Get the current question
            Question currentQuestion = questions[i];

            // Randomize this question's answer options
            currentQuestion.shuffleOptions();

            System.out.println();
            System.out.println("===============================");
            System.out.println(
                    "Question" + (i + 1) + " of " + questions.length
            );
            System.out.println("===============================");

            System.out.println("Topic: " + currentQuestion.getTopic());

            //Display the QUESTION
            System.out.println(
                    currentQuestion.getQuestionText()
            );

            System.out.println();

            //Get the answer options
            String[] options = currentQuestion.getOptions();

            // Display the options
            for (int j = 0; j < options.length; j++) {
                System.out.println(
                        (j + 1) + ". " + options[j]
                );
            }

            // Ask player for answer
            System.out.println();
            //System.out.print("Your answer: ");

            //int answer = scanner.nextInt();

            // Validate Answer
            int answer = getValidAnswer(scanner, options.length);

           /* while (answer < 1 || answer > options.length) {
                System.out.println();
                System.out.println("Invalid choice!");
                System.out.println("Please select a number from 1 to "
                + options.length + ".");
                System.out.println("Your answer: ");
                answer = scanner.nextInt();
            }
            */
            // Check the answer
            checkAnswer(
                    currentQuestion,
                    options,
                    answer
            );

//            System.out.println();
//            System.out.println("Explanation: ");
//            System.out.println(currentQuestion.getExplanation());

            // Display current score
            System.out.println(
                    "Current Score: " + score
            );

        }


            // Show final result
            showResults();


        // Show final result
        showResults();

        // Offer mistake review
        if (!incorrectQuestions.isEmpty()) {
            System.out.println();
            System.out.println(
                    "Would you like to review your mistakes? (y/n): "
            );

            if (scanner.hasNext()) {
                String choice = scanner.next().trim();

                if (choice.equalsIgnoreCase("y")) {
                    reviewIncorrectQuestions(scanner);
                }
            }
        }

        // Return the results of this quiz attempt
        return new QuizResult(
                score,
                correctAnswers,
                wrongAnswers,
                questions.length
        );
    }


    public static Question[] filterByTopic(Question[] questions, String topic) {
                if (questions == null || topic == null || topic.isEmpty()) {
                    return new Question[0];
                }

                return Arrays.stream(questions).filter(question -> question.getTopic().equalsIgnoreCase(topic.trim()) ).toArray(Question[]::new);
        }



    public static Question[] filterByTopicAndDifficulty(
            Question[] questions,
            String topic,
            String difficulty) {

        if (questions == null || topic == null || difficulty == null) {
            return new Question[0];
        }

        return Arrays.stream(questions)
                .filter(question ->
                        question.getTopic() != null
                                && question.getDifficulty() != null
                                && question.getTopic().equalsIgnoreCase(topic.trim())
                                && question.getDifficulty().equalsIgnoreCase(difficulty.trim()))
                .toArray(Question[]::new);
    }


    // Validate player's answer

        private int getValidAnswer(
                Scanner scanner,
                int numberOfOptions
        ) {
            while (true) {
                System.out.print("Your answer: ");

                // check whether the player entered a number
                if (!scanner.hasNextInt()){
                    System.out.println();
                    System.out.println("Invalid input!");

                    System.out.println("Please enter a number.");

                    // Remove the invalid input
                    scanner.next();

                    continue;
                }

                // Get the number
                int answer = scanner.nextInt();

                // Check whether the number is within range
                if (answer < 1 || answer > numberOfOptions) {
                    System.out.println();
                    System.out.println("Invalid choice");

                    System.out.println("Please select a number from 1 to "
                                    + numberOfOptions + "."
                            );

                    continue;
                }

                // The answer is valid
                return answer;
            }
        }

        // Check player's answer
        private void checkAnswer(
                Question question,
                String[] options,
                int answer
                ) {

            if(answer == question.getCorrectAnswer() ) {
                score +=10;
                correctAnswers++;
                System.out.println();
                System.out.println("✓ Correct! Well done!");
                System.out.println("+ 10 points");

            } else {
                wrongAnswers++;
                incorrectQuestions.add(question);

                System.out.println();
                System.out.println("Incorrect! Keep learning.");

                int correctIndex =
                        question.getCorrectAnswer() - 1;

                System.out.println(
                        "Correct answer: "
                                + options[correctIndex]);

            }

            System.out.println();
            System.out.println("Explanation: ");
            System.out.println(question.getExplanation());

            System.out.println();
            System.out.println("Correct Score: " + score);
        }

        public List<Question> getIncorrectQuestions() {
        return new ArrayList<>(incorrectQuestions);
        }

        private void reviewIncorrectQuestions(Scanner scanner) {

            if(incorrectQuestions.isEmpty()) {
                System.out.println();
                System.out.println("Excellent! You answered every question correctly.");
                System.out.println("There are no mistakes to review.");
                return;
            }

            System.out.println();
            System.out.println("=================================");
            System.out.println("         MISTAKE REVIEW");
            System.out.println("=================================");
            System.out.println("Questions to review: " + incorrectQuestions.size());

            for(int i = 0; i < incorrectQuestions.size(); i++) {
                Question question = incorrectQuestions.get(i);
                String [] options = question.getOptions();

                System.out.println();
                System.out.println("------------------------------");
                System.out.println("Review Question " + (i + 1)
                    + " of " + incorrectQuestions.size());
                System.out.println(question.getQuestionText());

                for(int j = 0; j < options.length; j++) {
                    System.out.println((j + 1) + ". " + options[j]);
                }

                int answer = getValidAnswer(scanner, options.length);

                if(answer == question.getCorrectAnswer()) {
                    System.out.println("Correct! You've learned from your mistake.");
                } else {
                    int correctAnswer = question.getCorrectAnswer();

                    System.out.println("Not quite. Keep practising!");
                    System.out.println("Correct answer: "
                            + correctAnswer + ". "
                            + options[correctAnswer -1]);
                }

                System.out.println();
                System.out.println("Explanation: ");
                System.out.println(question.getExplanation());
            }

            System.out.println();
            System.out.println("=================================");
            System.out.println("          REVIEW COMPLETED");
            System.out.println("=================================");
            System.out.println("You've reviewed all you mistakes.");
        }

        // Display final results
        private void showResults() {

            System.out.println();

            System.out.println("===============================");
            System.out.println("          QUIZ COMPLETE");
            System.out.println("===============================");

            System.out.println(
                    "Correct Answers: " + correctAnswers
            );

            System.out.println(
                    "Wrong Answers: " + wrongAnswers
            );

            System.out.println();
            System.out.println(
                    "Final Score: " + score + " / " + (questions.length * 10)
            );

            // CALCULATE percentage of performance feedback.
            double percentage = questions.length == 0 ? 0 : (correctAnswers * 100.0) / questions.length;
            System.out.printf("Percentage: %.1f%%%n", percentage);
            // Performance message
            if (percentage >= 80) {
                System.out.println("Excellent work!");
            } else if (percentage >=60) {
                System.out.println("Good job!");
            } else if (percentage >= 40) {
                System.out.println("Not bad. Keep practicing");

            }else {

                System.out.println("You need more practice.");
        }
    }

}
