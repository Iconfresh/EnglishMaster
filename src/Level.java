public class Level {

    private int levelNumber;
    private  String levelName;
    private Question[] questions;
    private boolean unlocked;


    public Level(
            int levelNumber,
            String levelName,
            Question[] questions,
            boolean unlocked
    ) {
        this.levelNumber = levelNumber;
        this.levelName = levelName;
        this.questions = questions;
        this.unlocked = unlocked;
    }

    public  int getLevelNumber() {
        return levelNumber;
    }

    public String getLevelName() {
        return levelName;
    }

    public Question[]getQuestions() {
        return questions;
    }

    public boolean getUnlocked() {
        return unlocked;
    }

    public void unlock() {
        unlocked = true;
    }
}
