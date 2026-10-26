import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

public class PlayerProgress {
    private final Properties properties = new Properties();
    private static final Path SAVE_FILE = Paths.get("english-master-progress.properties");
    private String playerName;
    private boolean intermediateUnlocked;
    private boolean advancedUnlocked;

    private boolean beginnerCompleted;
    private boolean intermediateCompleted;
    private boolean advancedCompleted;

    // -1 means the player has not completed the level yet.

    private double beginnerBestScore = -1;
    private double intermediateBestScore = -1;
    private double advancedBestScore = -1;
    private int totalXP = 0;

    private int beginnerBestStars = 0;
    private int intermediateBestStars = 0;
    private int advancedBestStars = 0;

    private boolean firstStepsUnlocked = false;
    private boolean grammarChampionUnlocked = false;
    private boolean perfectScoreUnlocked = false;
    private boolean masterInProgressUnlocked = false;
    private final Map<String, int[]> topicStatistics = new HashMap<>();

    // Load saved progress when an object is created.
    public PlayerProgress() {
        load();
    }

    private void load() {
        if (!Files.exists(SAVE_FILE)) {
            return;
        }

        try(InputStream input = Files.newInputStream(SAVE_FILE)) {
            properties.load(input);
            playerName = properties.getProperty("playerName", "");
            intermediateUnlocked = Boolean.parseBoolean(properties.getProperty("intermediateUnlocked", "false"));
            advancedUnlocked = Boolean.parseBoolean(properties.getProperty("advancedUnlocked", "false"));

            beginnerCompleted = Boolean.parseBoolean(properties.getProperty("beginnerCompleted", "false"));
            intermediateCompleted = Boolean.parseBoolean(properties.getProperty("intermediateCompleted", "false"));
            advancedCompleted = Boolean.parseBoolean(properties.getProperty("advancedCompleted", "false"));

            beginnerBestScore = readScore(properties, "beginnerBestScore");
            intermediateBestScore = readScore(properties, "intermediateBestScore");
            advancedBestScore = readScore(properties, "advancedBestScore");
            totalXP = Math.max(
                    0,
                    Integer.parseInt(properties.getProperty("totalXP", "0")));

            beginnerBestStars = Integer.parseInt(properties.getProperty("beginnerBestStars", "0"));
            intermediateBestStars = Integer.parseInt(properties.getProperty("intermediateBestStars", "0"));
            advancedBestStars = Integer.parseInt(properties.getProperty("advancedBestStars", "0"));

            firstStepsUnlocked = Boolean.parseBoolean(properties.getProperty("firstStepsUnlocked", "false"));
            grammarChampionUnlocked = Boolean.parseBoolean(properties.getProperty("grammarChampionUnlocked", "false"));
            perfectScoreUnlocked = Boolean.parseBoolean(properties.getProperty("perfectScore", "false"));
            masterInProgressUnlocked = Boolean.parseBoolean(properties.getProperty("masterInProgressUnlocked", "false"));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Could not read saved progress:" + e.getMessage());
        }

        String[] topics = {
                "Grammar",
                "Vocabulary",
                "Spelling",
                "Punctuation",
                "Sentence Construction",
                "Synonyms and Antonyms",
                "Reading Comprehension"
        };

        for (String topic : topics) {
            String key = topic.replace(" ", "_");

            int attempted = Integer.parseInt(
                    properties.getProperty(
                            "topicStats." + key + ".attempted", "0"
                    )
            );

            int correct = Integer.parseInt(
                    properties.getProperty(
                            "topicStats." + key + ".correct", "0"
                    )
            );

            int sessions = Integer.parseInt(
                    properties.getProperty(
                            "topicStats." + key + ".sessions", "0"
                    )
            );

            if (attempted > 0 || correct > 0 || sessions > 0) {
                topicStatistics.put(
                        topic,
                        new int[]{attempted, correct, sessions}
                );
            }
        }
    }

    private double readScore(Properties properties, String property) {
        try {
            return Double.parseDouble(
                    properties.getProperty(property, "-1")
            );
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Save the current progress to the file.
    public void save() {
        for (Map.Entry<String, int[]> entry
                : topicStatistics.entrySet()) {

            String key = entry.getKey().replace(" ", "_");
            int[] stats = entry.getValue();

            properties.setProperty(
                    "topicStats." + key + ".attempted",
                    String.valueOf(stats[0])
            );

            properties.setProperty(
                    "topicStats." + key + ".correct",
                    String.valueOf(stats[1])
            );

            properties.setProperty(
                    "topicStats." + key + ".sessions",
                    String.valueOf(stats[2])
            );
        }

        properties.setProperty("playerName", playerName == null ? "" : playerName);
        properties.setProperty("intermediateUnlocked", Boolean.toString(intermediateUnlocked));
        properties.setProperty("advancedUnlocked", Boolean.toString(advancedUnlocked));
        properties.setProperty("beginnerCompleted", Boolean.toString(beginnerCompleted));
        properties.setProperty("intermediateCompleted", Boolean.toString(intermediateCompleted));
        properties.setProperty("advancedCompleted", Boolean.toString(advancedCompleted));
        properties.setProperty("beginnerBestScore", Double.toString(beginnerBestScore));
        properties.setProperty("intermediateBestScore", Double.toString(intermediateBestScore));
        properties.setProperty("advancedBestScore", Double.toString(advancedBestScore));
        properties.setProperty("totalXP", String.valueOf(totalXP));
        properties.setProperty("beginnerBestStars", String.valueOf(beginnerBestStars));
        properties.setProperty("intermediateBestStars", String.valueOf(intermediateBestStars));
        properties.setProperty("advancedBestStars", String.valueOf(advancedBestStars));
        properties.setProperty("firstStepsUnlocked", String.valueOf(firstStepsUnlocked));
        properties.setProperty("grammarChampionUnlocked", String.valueOf(grammarChampionUnlocked));
        properties.setProperty("perfectScore", String.valueOf(perfectScoreUnlocked));
        properties.setProperty("masterInProgressUnlocked", String.valueOf(masterInProgressUnlocked));

        try(OutputStream output = Files.newOutputStream(SAVE_FILE)) {
            properties.store(output, "English Master Player Progress");
        } catch (IOException e) {
            System.out.println("Could not save progress:" + e.getMessage());
        }
    }
    public int getTotalXP() {
        return totalXP;
    }

    public int getBeginnerBestStars() {
        return beginnerBestStars;
    }

    public int getIntermediateBestStars() {
        return intermediateBestStars;
    }

    public int getAdvancedBestStars() {
        return advancedBestStars;
    }

    public void addXP(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("XP amount cannot be negative.");
        }
        totalXP += amount;
        save();
    }



    // Record the result of a completed level.

    public void recordResult(int levelNumber, double percentage) {
        if (!Double.isFinite(percentage)
                || percentage < 0
                || percentage > 100) {
            throw new IllegalArgumentException(
                    "Percentage must be between 0 and 100."
            );
        }

        boolean passed = percentage >= 70;

        switch (levelNumber) {
            case 1:
                beginnerBestScore =
                        Math.max(beginnerBestScore, percentage);

                if (passed) {
                    beginnerCompleted = true;
                    intermediateUnlocked = true;
                }
                break;

            case 2:
                intermediateBestScore =
                        Math.max(intermediateBestScore, percentage);

                if (passed) {
                    intermediateCompleted = true;
                    advancedUnlocked = true;
                }
                break;

            case 3:
                advancedBestScore =
                        Math.max(advancedBestScore, percentage);

                if (passed) {
                    advancedCompleted = true;
                }
                break;

            default:
                throw new IllegalArgumentException(
                        "Invalid level number: " + levelNumber
                );
        }

        save();
    }


    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isBeginnerUnlocked() {
        return true;
    }

    public boolean isIntermediateUnlocked() {
        return intermediateUnlocked;
    }

    public boolean isAdvancedUnlocked() {
        return advancedUnlocked;
    }

    public boolean isBeginnerCompleted() {
        return beginnerCompleted;
    }

    public boolean isIntermediateCompleted() {
        return intermediateCompleted;
    }

    public boolean isAdvancedCompleted() {
        return advancedCompleted;
    }

    public double getBeginnerBestScore() {
        return beginnerBestScore;
    }

    public double getIntermediateBestScore() {
        return intermediateBestScore;
    }

    public double getAdvancedBestScore() {
        return advancedBestScore;
    }

    public boolean isFirstStepsUnlocked() {
        return firstStepsUnlocked;
    }

    public boolean isGrammarChampionUnlocked() {
        return grammarChampionUnlocked;
    }

    public boolean isPerfectScoreUnlocked() {
        return perfectScoreUnlocked;
    }

    public boolean isMasterInProgressUnlocked() {
        return masterInProgressUnlocked;
    }

    public int getCompletedLevelsCount() {
        int count = 0;
        if (beginnerCompleted) {
            count++;
        }
        if (intermediateCompleted) {
            count++;
        }
        if (advancedCompleted) {
            count++;
        }
        return count;
    }

    public int getLevelsPlayedCount() {
        int count = 0;
        if (beginnerBestScore >= 0) {
            count++;
        }

        if (intermediateBestScore >= 0) {
            count++;
        }
        if (advancedBestScore >= 0) {
            count++;
        }
        return count;
    }

    public double getOverallBestScore() {
        double total = 0;
        int levelsPlayed = 0;

        if (beginnerBestScore >= 0) {
            total += beginnerBestScore;
            levelsPlayed++;
        }

        if (intermediateBestScore >= 0) {
            total += intermediateBestScore;
            levelsPlayed++;
        }

        if (advancedBestScore >= 0) {
            total += advancedBestScore;
            levelsPlayed++;
        }

        if (levelsPlayed == 0) {
            return -1;
        }
        return total/levelsPlayed;

    }

    public int calculateStars(double percentage) {
        if (percentage < 70) {
            return 0;
        } else if (percentage < 80) {
            return 1;
        } else if (percentage < 90) {
            return 2;
        } else {
            return 3;
        }
    }


    public void recordTopicResult(
            String topic,
            int correctAnswers,
            int totalQuestions) {

        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException(
                    "Topic cannot be empty."
            );
        }

        if (totalQuestions < 0
                || correctAnswers < 0
                || correctAnswers > totalQuestions) {
            throw new IllegalArgumentException(
                    "Invalid topic quiz result."
            );
        }

        String key = topic.trim();

        int[] stats = topicStatistics.computeIfAbsent(
                key,
                ignored -> new int[3]
        );

        stats[0] += totalQuestions;
        stats[1] += correctAnswers;
        stats[2]++;

        save();
    }


    public int getTopicQuestionsAnswered(String topic) {
        int[] stats = topicStatistics.get(topic);
        return stats == null ? 0 : stats[0];
    }

    public int getTopicCorrectAnswers(String topic) {
        int[] stats = topicStatistics.get(topic);
        return stats == null ? 0 : stats[1];
    }

    public int getTopicPracticeSessions(String topic) {
        int[] stats = topicStatistics.get(topic);
        return stats == null ? 0 : stats[2];
    }

    public double getTopicAccuracy(String topic) {
        int[] stats = topicStatistics.get(topic);

        if (stats == null || stats[0] == 0) {
            return 0.0;
        }

        return (double) stats[1] / stats[0] * 100;
    }



    public void recordStars(int levelNumber, double percentage) {
        int earnedStars = calculateStars(percentage);

        switch(levelNumber) {
            case 1: beginnerBestStars = Math.max( beginnerBestStars, earnedStars);
            break;

            case 2: intermediateBestStars = Math.max( intermediateBestStars, earnedStars);
            break;

            case 3: advancedBestStars = Math.max( advancedBestStars, earnedStars);
            break;

            default:
                throw new IllegalArgumentException("Invalid level number:" + levelNumber);
        }
        save();
    }

    public void checkAchievements(
            int levelNumber,
            double percentage,
            int correctAnswers,
            int totalQuestions
    ) {
        boolean changed = false;

        // Achievement 1: Complete a quiz attempt
        if (!firstStepsUnlocked) {
            firstStepsUnlocked = true;
            changed = true;

            System.out.println();
            System.out.println("ACHIEVEMENT UNLOCKED: FIRST STEPS!");
            System.out.println("You've completed your first quiz.");
        }

        // Achievement 2: Pass Beginner
        if (levelNumber == 1
                 && percentage >= 70
                 && !grammarChampionUnlocked) {

            grammarChampionUnlocked = true;
            changed = true;

            System.out.println();
            System.out.println("ACHIEVEMENT UNLOCKED: GRAMMAR CHAMPION!");
            System.out.println("You passed the Beginner level.");
        }

        // Achievement 3: Answer every question correctly
        if (totalQuestions >= 0
                 && correctAnswers == totalQuestions
                 && !perfectScoreUnlocked) {

            perfectScoreUnlocked = true;
            changed = true;

            System.out.println();
            System.out.println("ACHIEVEMENT UNLOCKED: PERFECT SCORES!");
            System.out.println("You answered every question correctly.");
        }

        // Achievement 4: Pass all three levels
        if (beginnerCompleted
                && intermediateCompleted
                && advancedCompleted
                && !masterInProgressUnlocked) {

            masterInProgressUnlocked = true;
            changed = true;

            System.out.println();
            System.out.println("ACHIEVEMENT UNLOCKED: MASTER IN PROGRESS!");
            System.out.println("You've passed all three levels.");
        }

        if (changed) {
            save();
        }

    }


}
