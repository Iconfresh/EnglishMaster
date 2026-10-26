import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // Create Scanner so we receive input from the player

        Scanner scanner = new Scanner(System.in);

        // Display the game title
        System.out.println("================================");
        System.out.println("        ENGLISH MASTER");
        System.out.println("================================");

        //GET PLAYER NAME
        //System.out.print("Enter your name: ");
        //String playerName = scanner.nextLine();
        PlayerProgress progress = new PlayerProgress();
        String playerName = progress.getPlayerName();
        if (playerName == null || playerName.isBlank()) {

            System.out.println("Enter your name: " );
            playerName = scanner.nextLine().trim();

            while (playerName.isEmpty()) {
                System.out.print("Name cannot be empty. Enter your name: ");
                playerName = scanner.nextLine().trim();
            }

            progress.setPlayerName(playerName);
            progress.save();
        } else {
            System.out.println("Welcome back, " + playerName + "!");
        }

        System.out.println();

        // Welcome the Player
       // System.out.println();
        //System.out.println("Welcome, " + playerName + "!");
        //System.out.println("Let's test your English skills.");
        //System.out.println();

        // Level progression
//        boolean beginnerUnlocked = true;
//        boolean intermediateUnlocked = false;
//        boolean advancedUnlocked = false;
//
//        boolean beginnerCompleted = false;
//        boolean intermediateCompleted = false;
//        boolean advancedCompleted = false;

        // main game loop
        boolean running = true;

        while (running) {
            System.out.println("\n================================");
            System.out.println("        LEVEL SELECT");
            System.out.println("=================================");

//            System.out.println("1. Beginner"
//                + (beginnerUnlocked ? "COMPLETED"
//                    : beginnerUnlocked ? "UNLOCKED" : "LOCKED")
//            );
//
//            System.out.println("2. Intermediate"
//                    + (intermediateCompleted ? "COMPLETED"
//                    : intermediateCompleted ? "UNLOCKED" : "LOCKED")
//            );
//
//            System.out.println("3. Advanced"
//                    + (advancedCompleted ? "COMPLETED"
//                    : advancedCompleted ? "UNLOCKED" : "LOCKED")
//
//            );
            System.out.println("1. Beginner         "
                + (progress.isBeginnerCompleted()
                ? "COMPLETED" : "UNLOCKED"
                    + " | Best: "
                    + formatBestScore(progress.getBeginnerBestScore())
                    )
            );

            System.out.println("2. Intermediate        "
                + (progress.isIntermediateCompleted()
                ? "COMPLETED"
                : progress.isIntermediateUnlocked()
                ? "UNLOCKED" : "LOCKED"    )
                + " | Best: "
                + formatBestScore(
                        progress.getIntermediateBestScore()
                    )
            );

            System.out.println("3. Advanced            "
                + (progress.isAdvancedCompleted()
                ? "COMPLETED"
                : progress.isAdvancedUnlocked()
                ? "UNLOCKED" : "LOCKED"    )
                + " | Best: "
                + formatBestScore(
                        progress.getAdvancedBestScore()
                    )
                );

            System.out.println("4. Player Statistics");
            System.out.println("5. Topic Practices");

            System.out.println("0. Exit Game");
            System.out.println("----------------------------------");

            System.out.println("Choose a Level:");

            // Validate menu input
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input! Enter a menu number.");
                scanner.next();
                continue;
            }
            int level = scanner.nextInt();

            if (level == 0){
               running = false;
               continue;
            }

            // Check whether the selected level is unlocked
            if (level == 1 && progress.isBeginnerUnlocked()) {
                QuizResult result = startBeginnerLevel(scanner);

                progress.recordResult( 1, result.getPercentage()
                );
                progress.checkAchievements( 1, result.getPercentage(),
                        result.getCorrectAnswers(),
                        result.getTotalQuestions());

                progress.checkAchievements( 2, result.getPercentage(),
                        result.getCorrectAnswers(),
                        result.getTotalQuestions());

                progress.checkAchievements( 3, result.getPercentage(),
                        result.getCorrectAnswers(),
                        result.getTotalQuestions());

                progress.recordStars(1, result.getPercentage());

                // Award 10 XP for every correct answer
                int earnedXP = result.getCorrectAnswers() * 10;

                // Add a 20 XP bonus for passing
                if (result.hasPassed()) {
                    earnedXP += 20;
                }
                progress.addXP(earnedXP);
                System.out.println();
                System.out.println("XP earned this round:" + earnedXP);
                System.out.println("Total XP: " + progress.getTotalXP());
                System.out.println("Stars earned: "
                    + progress.calculateStars(result.getPercentage()) + " / 3 ");

                if (result.hasPassed()) {
//                    beginnerCompleted = true;
//                    intermediateUnlocked = true;

                    System.out.println("\nLEVEL 1 PASSED!");
                    System.out.println("Intermediate level unlocked!");
                } else {
                    System.out.println("\nLevel not passed.");
                    System.out.println("Score at least 70% to pass.");
                }
            } else if (level == 2 && progress.isIntermediateUnlocked()) {
                QuizResult result = startIntermediateLevel(scanner);

                progress.recordResult( 2, result.getPercentage()
                );

                if (result.hasPassed()) {
//                    intermediateCompleted = true;
//                    advancedUnlocked = true;

                    System.out.println("\nLEVEL 2 PASSED!");
                    System.out.println("Advanced level unlocked!");
                } else  {
                    System.out.println("Level not passed.");
                    System.out.println("Score at least 70% to pass.");
                }

            } else if (level == 3 && progress.isAdvancedUnlocked()) {
                QuizResult result = startAdvancedLevel(scanner);

                progress.recordResult( 3, result.getPercentage()
                );

                if (result.hasPassed()) {
                  //  advancedCompleted = true;

                    System.out.println("\nLEVEL 3 PASSED!");
                    System.out.println("Congratulations! You have completed all levels!");
                } else   {
                    System.out.println("Level not passed.");
                    System.out.println("Score at least 70% to pass.");
                }

            } else if (level == 4) {
                displayPlayerStatistics(progress);

            } else if (level == 5) {
                startTopicPractice(scanner, progress);
            } else if (level >= 1 && level <= 3) {
                System.out.println("\nThat level is locked.");
                System.out.println("Pass the previous level with at least 70%.");

            } else {
                System.out.println("\nInvalid selection. Choose 0, 1, 2, 3, 4 or 5.");
            }

        }
        System.out.println("\nThanks for playing English Master!");
        scanner.close();




        /*

        // Display level menu
        System.out.println("SELECT YOUR LEVEL");
        System.out.println("---------------------------------");
        System.out.println("1. Beginner");
        System.out.println("2. Intermediate");
        System.out.println("3. Advanced");
        System.out.println("---------------------------------");

        // Ask the player to select a level
        System.out.println("Enter your choice: ");

        if (!scanner.hasNextInt()){
            System.out.println("Invalid level selected.");
            scanner.close();
            return;
        }
        int level = scanner.nextInt();

         */


        // Display selected level
        /*System.out.println();

        // CHECK SELECTED LEVEL

        if (level == 1) {
            System.out.println("You selected: Beginner");
            System.out.println();

            // START THE BEGINNER QUIZ
            startBeginnerLevel(scanner);
        } else if (level == 2) {
            System.out.println("Intermediate is currently locked.");
            System.out.println();

            System.out.println("Complete Beginner with at least 70%");

        } else if (level == 3) {
            System.out.println("Advanced is currently locked.");
            System.out.println();

            System.out.println("Complete the previous levels first.");

        } else {
            System.out.println("Invalid level selected.");
        }
        */

        /*
        switch (level) {
            case 1:
                System.out.println("You selected: Beginner");
                startBeginnerLevel(scanner);
                break;

            case 2:
                System.out.println("Intermediate is currently locked.");
                System.out.println("Compete Beginner with at least 70%");
                break;

            case 3:
                System.out.println("Advanced is currently locked.");
                System.out.println("Complete the previous levels first.");
                break;

            default:
                System.out.println("Invalid level selected.");
        }

         */
        // Close Scanner
        //scanner.close();
    }

    public static void displayPlayerStatistics( PlayerProgress progress) {
        System.out.println();
        System.out.println("==================================");
        System.out.println("         ENGLISH MASTER ");
        System.out.println("         PLAYER PROFILE");
        System.out.println("==================================");

        System.out.println("Player: " + progress.getPlayerName());
        System.out.println("Total XP: " + progress.getTotalXP());

        System.out.println();
        System.out.println("------------- PROGRESS --------------");

        System.out.println("Levels completed: " + progress.getCompletedLevelsCount() + " / 3");

        System.out.println("Levels attempted: " + progress.getLevelsPlayedCount() + " / 3");

        int totalStars = progress.getBeginnerBestStars()
                + progress.getIntermediateBestStars()
                + progress.getAdvancedBestStars();

        System.out.println("Total Stars: " + totalStars + " / 9");

        System.out.println();
        System.out.println("---------BEST SCORES--------------");

        System.out.println("Beginner: " + formatBestScore(progress.getBeginnerBestScore())
                + " | Stars: " + progress.getBeginnerBestStars() + " / 3");

        System.out.println("Intermediate: " + formatBestScore(progress.getIntermediateBestScore())
        + " | Stars: " + progress.getIntermediateBestStars() + " / 3");

        System.out.println("Advanced: " + formatBestScore(progress.getAdvancedBestScore())
        + " | Stars: " + progress.getAdvancedBestStars() + " / 3");

        System.out.println("----------------------------------");

        double average = progress.getOverallBestScore();
        if (average < 0 ) {
            System.out.println("Overall best average: No scores yet");
        } else  {
            System.out.println("Overall best average: " + String.format("%.1f%%", average));
        }

        System.out.println();
        System.out.println("------------ACHIEVEMENTS--------------");

        displayAchievement("First Steps", progress.isFirstStepsUnlocked());
        displayAchievement("Grammar Champion", progress.isGrammarChampionUnlocked());
        displayAchievement("Perfect Score", progress.isPerfectScoreUnlocked());
        displayAchievement("Master in Progress", progress.isMasterInProgressUnlocked());


        System.out.println();
        System.out.println("--------------LEVEL STATUS---------------");

        System.out.println("Beginner: " + (progress.isBeginnerCompleted() ? "COMPLETED" : "NOT COMPLETED"));

        System.out.println("Intermediate: " + (progress.isIntermediateCompleted()
                ? "COMPLETED"
                :progress.isIntermediateUnlocked()
                ? "UNLOCKED"
                : "LOCKED" ));

        System.out.println("Advanced: " + (progress.isAdvancedCompleted()
                ? "COMPLETED"
                :progress.isAdvancedUnlocked()
                ? "UNLOCKED"
                : "LOCKED"));

        System.out.println("===================================");

        System.out.println();
        System.out.println("----------- TOPIC PERFORMANCE ----------");

        String[] trackedTopics = {
                "Grammar",
                "Vocabulary",
                "Spelling",
                "Punctuation",
                "Sentence Construction",
                "Synonyms and Antonyms",
                "Reading Comprehension"
        };

        for (String topic : trackedTopics) {

            int questionsAnswered =
                    progress.getTopicQuestionsAnswered(topic);

            int sessions =
                    progress.getTopicPracticeSessions(topic);

            if (sessions == 0) {
                System.out.println(topic + ": Not practised yet");
            } else {
                System.out.println(topic);
                System.out.println("  Questions answered: "
                        + questionsAnswered);
                System.out.println("  Correct answers: "
                        + progress.getTopicCorrectAnswers(topic));
                System.out.println("  Accuracy: "
                        + String.format(
                        "%.1f%%",
                        progress.getTopicAccuracy(topic)
                ));
                System.out.println("  Sessions: " + sessions);
            }
        }


    }

    public static void displayAchievement(String achievementName, boolean unlocked) {
        String status = unlocked ? "[UNLOCKED]" : "[LOCKED]";
        System.out.println(status + " " + achievementName);
    }

    public static String formatBestScore(double bestScore) {
        if (bestScore < 0) {
            return "Not played";
        }

        return String.format("%.1f%%", bestScore);
    }

    public static Question[] getBeginnerQuestions() {
        // STORE THE QUESTIONS

        Question [] questions = {

                new Question(
                        "She ___ to school every morning.",
                        new String[] { "go", "goes", "going", "gone"},
                        2,
                        "In the simple present tense, we generally add -s or -es "
                                + "to the verb when the subject is he, she, or it.",
                                "Grammar"
                ),

                new Question(
                        "I ___ a new phone yesterday.",
                        new String[] {"buy", "bought", "buys", "buying"},
                        2
                ),

                new Question(
                        "They ___ playing football now.",
                        new String[] {"is", "are", "was", "be"},
                        2
                ),

                new Question(
                        "He ___ not happy to see you.",
                        new String[] {"do", "does", "is", "are"},
                        3
                ),

                new Question(
                        "They _____ football every Saturday.",
                        new String[]{"plays", "playing", "play", "played"},
                        3,
                        "Use the base form of the verb with the plural subject "
                                + "'they' in the simple present tense.",
                        "Grammar"
                ),

                new Question(
                        "I have lived here _____ 2020.",
                        new String[]{"for", "since", "during", "from"},
                        2,
                        "Use 'since' with a starting point in time, such as a year. "
                                + "Use 'for' with a duration.",
                        "Grammar"
                ),

                new Question(
                        "She is _____ honest person.",
                        new String[]{"a", "an", "the", "no article"},
                        2,
                        "Use 'an' before a vowel sound. 'Honest' begins with a "
                                + "vowel sound because its h is silent.",
                        "Grammar"
                ),

                new Question(
                        "If I _____ enough money, I would buy a new laptop.",
                        new String[]{"have", "had", "will have", "has"},
                        2,
                        "In this second conditional sentence, use the past simple "
                                + "form 'had' after 'if'.",
                        "Grammar"
                ),

                new Question(
                        "Neither John nor his friends _____ ready.",
                        new String[]{"is", "was", "are", "has"},
                        3,
                        "With 'neither...nor', the verb commonly agrees with the "
                                + "nearest subject. Here, 'friends' is plural.",
                        "Grammar"
                ),


                new Question(
                        "We ___ happy to see you",
                        new String[] {"is", "are", "was", "be"},
                        2
                ),

                new Question(
                        "The cat is ___ the table.",
                        new String[] {"under", "run", "eat", "write"},
                        1
                ),

                new Question(
                        "She has ___ her homework.",
                        new String[] {"do", "done", "did", "doing"},
                        2
                ),

                new Question(
                        "I ___ going to the market.",
                        new String[] {"am", "is", "are", "be"},
                        1
                ),

                new Question(
                        "John and Peter ___ friends.",
                        new String[] {"is", "am", "are", "was"},
                        3
                ),

                new Question(
                        "He ___ very fast.",
                        new String[] {"run", "runs", "running", "ran"},
                        2
                ),


                new Question(
                        "Which word is closest in meaning to 'rapid'?",
                        new String[]{"Slow", "Quick", "Weak", "Quiet"},
                        2,
                        "'Rapid' means happening or moving quickly.",
                        "Vocabulary"
                ),

                new Question(
                        "Choose the opposite of 'generous'.",
                        new String[]{"Kind", "Selfish", "Helpful", "Friendly"},
                        2,
                        "'Generous' describes someone willing to give or share. "
                                + "'Selfish' describes someone mainly concerned with "
                                + "their own interests.",
                        "Vocabulary"
                ),

                new Question(
                        "What does 'reluctant' mean?",
                        new String[]{
                                "Eager to begin",
                                "Unwilling or hesitant",
                                "Extremely excited",
                                "Completely confused"
                        },
                        2,
                        "'Reluctant' means unwilling or hesitant to do something.",
                        "Vocabulary"
                ),

                new Question(
                        "Choose the word that best completes the sentence: "
                                + "The teacher praised her _____ effort.",
                        new String[]{"diligent", "careless", "lazy", "weak"},
                        1,
                        "'Diligent' means showing care and sustained effort.",
                        "Vocabulary"
                ),


                new Question(
                        "Choose the correctly spelled word.",
                        new String[]{
                                "Accomodation",
                                "Accommodation",
                                "Acommodation",
                                "Accommondation"
                        },
                        2,
                        "'Accommodation' has double c and double m.",
                        "Spelling"
                ),

                new Question(
                        "Choose the correctly spelled word.",
                        new String[]{
                                "Neccessary",
                                "Necesary",
                                "Necessary",
                                "Neccesary"
                        },
                        3,
                        "'Necessary' is spelled with one c and double s.",
                        "Spelling"
                ),

                new Question(
                        "Which word is spelled correctly?",
                        new String[]{
                                "Seperate",
                                "Separate",
                                "Seperrate",
                                "Sepparate"
                        },
                        2,
                        "The correct spelling is 'separate', with 'par' in the middle.",
                        "Spelling"
                ),

                new Question(
                        "Choose the correctly spelled word.",
                        new String[]{
                                "Priviledge",
                                "Privilege",
                                "Privelege",
                                "Privillage"
                        },
                        2,
                        "The correct spelling is 'privilege'.",
                        "Spelling"
                ),


                new Question(
                        "Which sentence is punctuated correctly?",
                        new String[]{
                                "Where are you going.",
                                "Where are you going?",
                                "Where are you going,",
                                "Where are you going!"
                        },
                        2,
                        "A direct question normally ends with a question mark.",
                        "Punctuation"
                ),

                new Question(
                        "Which sentence uses the apostrophe correctly?",
                        new String[]{
                                "The girls bag is red.",
                                "The girl's bag is red.",
                                "The girls' bag is red. (one girl)",
                                "The girls,s bag is red."
                        },
                        2,
                        "The singular possessive 'girl's' indicates that the bag "
                                + "belongs to one girl.",
                        "Punctuation"
                ),

                new Question(
                        "Choose the sentence with the correct comma placement.",
                        new String[]{
                                "After lunch we, went home.",
                                "After lunch, we went home.",
                                "After, lunch we went home.",
                                "After lunch we went, home."
                        },
                        2,
                        "A comma commonly follows an introductory phrase such as "
                                + "'After lunch'.",
                        "Punctuation"
                ),

                new Question(
                        "Which sentence uses capitalization correctly?",
                        new String[]{
                                "we visited abuja on monday.",
                                "We visited Abuja on Monday.",
                                "We visited abuja on monday.",
                                "we visited Abuja on Monday."
                        },
                        2,
                        "Capitalize the beginning of a sentence, proper nouns "
                                + "such as Abuja, and days of the week such as Monday.",
                        "Punctuation"
                )



        };

        return questions;
    }

    public static Question[] getIntermediateQuestions() {
        Question[] questions = {
                new Question(
                        "If I ____ enough money, I would buy a car.",
                        new String[] { "have", "had", "will have", "having"},
                        2
                ),

                new Question(
                        "She has lived here ___ 2019.",
                        new String[] { "for", "since", "during", "from"},
                        2
                ),

                new Question(
                        "Neither John nor James ___ coming today.",
                        new String[] { "are", "were", "is", "be"},
                        3
                ),

                new Question(
                        "The book ___ by the student yesterday.",
                        new String[] { "was read", "read", "is reading", "reads"},
                        1
                ),

                new Question(
                        "She is interested ___ learning Java.",
                        new String[] { "on", "at", "in", "for"},
                        3
                ),

                new Question(
                        "By the time we arrived, the movie ___.",
                        new String[] { "started", "has started", "had started", "starts"},
                        3
                ),

                new Question(
                        "He speaks English ___ than his brother.",
                        new String[] { "good", "better", "best", "well"},
                        2
                ),

                new Question(
                        "I wish I ___ taller.",
                        new String[] { "am", "was", "were", "be"},
                        3
                ),

                new Question(
                        "The manager asked me ___ the report.",
                        new String[] { "finish", "finished", "to finish", "finishing"},
                        3
                ),

                new Question(
                        "she avoided ___ about the incident.",
                        new String[] { "talk", "to talk", "talked", "talking"},
                        4
                )
        };

        return questions;
    }
    // BEGINNER QUIZ

    public static QuizResult startBeginnerLevel(Scanner scanner) {
        Quiz quiz = new Quiz(getBeginnerQuestions());
        return quiz.start(scanner);

        /*if (result.hasPassed()) {
            System.out.println("Beginner level passed!");
        } else {
            System.out.println("Beginner level not passed. You need at least 70%.");
        }
        */
        }


        //INTERMEDIATE QUIZ
    public static QuizResult startIntermediateLevel(Scanner scanner) {
        Quiz quiz = new Quiz(getIntermediateQuestions());
        return quiz.start(scanner);


        // Create the quiz

       /*
       if (result.hasPassed()) {
            System.out.println();
            System.out.println(" \uD83C\uDF89  INTERMEDIATE LEVEL PASSED!");
        } else {
            System.out.println();
            System.out.println("❌ INTERMEDIATE LEVEL FAILED.");

            System.out.println("You need at least 70% to pass.");
        }
        */
    }

    public static Question[] getAdvantageQuestions() {
        Question[] questions = {
                new Question("Had the committee ___ the implications earlier, the decision might have been different.",
                        new String[] {"considered", "consider", "considering", "considers"},
                        1
                ),

                new Question("Hardly ___ the meeting begun when the power went out.",
                        new String[] {"had", "has", "did", "was"},
                        1
                ),

                new Question("The proposal, ___ was submitted yesterday, has been approved.",
                        new String[] {"who", "which", "whom", "whose"},
                        2
                ),

                new Question( "Were I ___ your position, I would reconsider the decision.",
                        new String[] {"on", "at", "in", "by"},
                        3),

                new Question("No sooner had he arrived ___ the phone rang.",
                        new String[] {"when", "than", "then", "while"},
                        2
                ),

                new Question("The evidence was insufficient to ___ the defendant's claim.",
                        new String[] {"substantiate", "substanial", "substance", "substantiating"},
                        1
                ),

                new Question("She would rather ___ at home than attend the event.",
                        new String[] {"stays", "staying", "stay", "stayed"},
                        3
                ),

                new Question("The CEO insisted that the report ___ before Friday.",
                        new String[] {"is completed", "be completed", "was completed", "completed"},
                        2
                ),

                new Question("His explanation was so ___ that nobody understood it.",
                        new String[] {"ambiguous", "obvious", "precise", "transparent"},
                        1
                ),

                new Question("Despite ___ extensively, the team failed to win.",
                        new String[] {"train", "trained", "training", "trains"},
                        3
                )


        };
        return questions;
    }

    public static QuizResult startAdvancedLevel( Scanner scanner) {
        Quiz quiz = new Quiz(getAdvantageQuestions());
        return quiz.start(scanner);

        // Display the Advanced level outcome
        /*
        if (result.hasPassed()) {
            System.out.println("ADVANCED LEVEL PASSED!");
        } else {
            System.out.println("ADVANCED LEVEL FAILED.");
            System.out.println("You need at least 70% to pass.");
        }
        */

    }

    public static Question[] getAllQuestions() {
        java.util.List<Question> allQuestions = new java.util.ArrayList<>();
        allQuestions.addAll(java.util.Arrays.asList(getBeginnerQuestions()));
        allQuestions.addAll(java.util.Arrays.asList(getIntermediateQuestions()));
        allQuestions.addAll(java.util.Arrays.asList(getAdvantageQuestions()));

        // Add question stored in the external file
        Question[] externalQuestions = QuestionBank.loadFromFile("questions.txt");
        allQuestions.addAll(java.util.Arrays.asList(externalQuestions));
        return allQuestions.toArray(new Question[0]);
//        Question[] beginner = getBeginnerQuestions();
//        Question[] intermediate = getIntermediateQuestions();
//        Question[] advantage = getAdvantageQuestions();

//        Question[] all = new Question[beginner.length + intermediate.length + advantage.length];
//        int index = 0;
//
//        for (Question question : beginner) {
//            all[index++] = question;
//        }
//
//        for (Question question : intermediate) {
//            all[index++] = question;
//        }
//
//        for (Question question : advantage) {
//            all[index++] = question;
//        }
//        return all;
    }



    public static void startTopicPractice(
            Scanner scanner,
            PlayerProgress progress) {

        String[] topics = {
                "Grammar",
                "Vocabulary",
                "Spelling",
                "Punctuation",
                "Sentence Construction",
                "Synonyms and Antonyms",
                "Reading Comprehension"
        };

        System.out.println();
        System.out.println("====================================");
        System.out.println("          TOPIC PRACTICE");
        System.out.println("====================================");

        for (int i = 0; i < topics.length; i++) {
            System.out.println((i + 1) + ". " + topics[i]);
        }

        System.out.println("0. Return to main menu");

        int topicChoice = readMenuChoice(scanner, topics.length);

        if (topicChoice == 0) {
            return;
        }

        String selectedTopic = topics[topicChoice - 1];

        System.out.println();
        System.out.println("Selected topic: " + selectedTopic);
        System.out.println();
        System.out.println("Choose Difficulty");
        System.out.println("1. Beginner");
        System.out.println("2. Intermediate");
        System.out.println("3. Advanced");
        System.out.println("0. Return to main menu");

        int difficultyChoice = readMenuChoice(scanner, 3);

        if (difficultyChoice == 0) {
            return;
        }

        String[] difficulties = {
                "Beginner",
                "Intermediate",
                "Advanced"
        };

        String selectedDifficulty = difficulties[difficultyChoice - 1];

        Question[] matchingQuestions =
                Quiz.filterByTopicAndDifficulty(
                        getAllQuestions(),
                        selectedTopic,
                        selectedDifficulty
                );

        System.out.println();
        System.out.println("====================================");
        System.out.println("          PRACTICE SUMMARY");
        System.out.println("====================================");
        System.out.println("Topic: " + selectedTopic);
        System.out.println("Difficulty: " + selectedDifficulty);
        System.out.println("Questions available: " + matchingQuestions.length);

        if (matchingQuestions.length == 0) {
            System.out.println();
            System.out.println(
                    "No questions are available for this selection yet."
            );
            System.out.println(
                    "Add matching questions to questions.txt and try again."
            );
            return;
        }

        System.out.println();
        System.out.println("Starting your practice session...");

        Quiz quiz = new Quiz(matchingQuestions);
        QuizResult result = quiz.start(scanner);

        progress.recordTopicResult(
                selectedTopic,
                result.getCorrectAnswers(),
                result.getTotalQuestions()
        );

        System.out.println();
        System.out.println("Topic performance saved!");
        System.out.println("Topic: " + selectedTopic);
        System.out.println(
                "Accuracy: " + String.format("%.1f%%", result.getPercentage())
        );
        System.out.println(
                "Practice sessions: "
                        + progress.getTopicPracticeSessions(selectedTopic)
        );
    }



    public static int readMenuChoice(Scanner scanner, int maxChoice) {

        while (true) {
            System.out.print("Enter your choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Enter a number.");
                scanner.next();
                continue;
            }

            int choice = scanner.nextInt();

            if (choice >= 0 && choice <= maxChoice) {
                return choice;
            }

            System.out.println(
                    "Choose a number between 0 and " + maxChoice + "."
            );
        }
    }



    public static void processLevelResult(
            PlayerProgress progress,
            int levelNumber,
            QuizResult result
    ) {
        double percentage = result.getPercentage();

        //Save the score and update level completion/unlocking.
        progress.recordResult(levelNumber, percentage);

        // Record the player's best star count for this level.
        progress.recordStars(levelNumber, percentage);

        // Check achievements once for this actual attempt.
        progress.checkAchievements(
                levelNumber,
                percentage,
                result.getCorrectAnswers(),
                result.getTotalQuestions()
        );

        // Award XP consistently for all levels.
        int earnedXP = result.getCorrectAnswers() * 10;

        if (result.hasPassed()) {
            earnedXP += 20;
        }

        progress.addXP(earnedXP);

        System.out.println();
        System.out.println("XP earned this round: " + earnedXP);
        System.out.println("Total XP: " + progress.getTotalXP());
        System.out.println("Stars earned this round: " + progress.calculateStars(percentage) + " / 3");

        if (result.hasPassed()) {

            System.out.println("LEVEL " + levelNumber + " PASSED!");

            if(levelNumber == 1) {
                System.out.println("Intermediate level unlocked!");
            } else if (levelNumber == 2) {
                System.out.println("Advanced level unlocked!");
            } else if (levelNumber == 3) {
                System.out.println("Congratulations! You have completed all levels!");
            }
        } else {
            System.out.println("Level not passed.");
            System.out.println("Score at least 70% to pass.");
        }
    }

}
