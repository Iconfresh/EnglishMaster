
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ProgressBar;

public class EnglishMasterApp extends Application {

    private static final String BACKGROUND = "#111827";
    private static final String PANEL = "#1F2937";
    private static final String PURPLE = "#8B5CF6";
    private static final String TEXT = "#F9FAFB";
    private static final String MUTED = "#9CA3AF";

    private BorderPane root;
    private Stage stage;
    private Button activeNavButton;

    // Load existing saved progress from the same save file.
    private final PlayerProgress progress = new PlayerProgress();

    // Remember the topic and difficulty for this session.
    private String currentTopic;
    private String currentDifficulty;

    private final String[] topics = {
            "Grammar",
            "Vocabulary",
            "Spelling",
            "Punctuation",
            "Sentence Construction",
            "Synonyms and Antonyms",
            "Reading Comprehension"
    };

    private Question[] sessionQuestions;
    private int currentQuestionIndex;
    private int guiCorrectAnswers;
    private int guiWrongAnswers;
    private int guiScore;
    private boolean answerSubmitted;
    private boolean resultsSaved;
    private boolean campaignMode = false;
    private int currentCampaignLevel;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        root.setLeft(createSidebar());
        showDashboard();

        Scene scene = new Scene(root, 1100, 720);

        stage.setTitle("Shepherd | English Master");

        try (var iconStream = getClass().getResourceAsStream(
                "/images/shepherd-icon.png")) {

            if (iconStream != null) {
                stage.getIcons().add(
                        new javafx.scene.image.Image(iconStream)
                );
            } else {
                System.err.println(
                        "Shepherd icon not found in the runtime classpath."
                );
            }

        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }

        stage.setMinWidth(850);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }


    private Node iconGraphic(String name, double size, String color) {
        String path = switch (name) {
            case  "dashboard", "⌂" ->
                    "M3 10.5 L12 3 L21 10.5 "
                            + "M5 9 V21 H19 V9 "
                            + "M9 21 V14 H15 V21";

            case  "book", "📚", "📖", "▣" ->
                    "M3 5 Q7 3 12 6 Q17 3 21 5 "
                            + "V19 Q16 17 12 20 Q8 17 3 19 Z "
                            + "M12 6 V20";

            case "trophy", "🏆", "★" ->
                    "M7 4 H17 V9 Q17 15 12 15 "
                            + "Q7 15 7 9 Z "
                            + "M7 6 H3 V9 Q3 13 8 13 "
                            + "M17 6 H21 V9 Q21 13 16 13 "
                            + "M12 15 V19 M8 21 H16 M9 19 H15";

            case "progress", "chart", "◎" ->
                    "M4 20 V12 H8 V20 Z "
                            + "M10 20 V5 H14 V20 Z "
                            + "M16 20 V9 H20 V20 Z";

            case "achievement", "medal", "🎯" ->
                    "M12 3 A7 7 0 1 0 12 17 "
                            + "A7 7 0 1 0 12 3 "
                            + "M9 17 L7 22 L12 20 L17 22 L15 17 "
                            + "M12 6 L13.5 9 L17 9.5 "
                            + "L14.5 12 L15 15 L12 13.5 "
                            + "L9 15 L9.5 12 L7 9.5 L10.5 9 Z";

            case "grammar", "✎" ->
                    "M4 20 L5 15 L16 4 L20 8 L9 19 Z "
                            + "M14 6 L18 10 M4 20 L9 19";

            case "spelling", "Aa" ->
                    "M4 20 L10 4 L16 20 M6 15 H14 "
                            + "M17 13 H21 V20 H17 Z "
                            + "M17 13 L19 10";

            case "punctuation", "¶" ->
                    "M5 4 H15 V12 H10 "
                            + "M10 12 V20 M15 4 V20";

            case "sentence", "≡" ->
                    "M4 6 H20 M4 12 H16 M4 18 H20";

            case "synonyms", "↔" ->
                    "M4 8 H20 M16 4 L20 8 L16 12 "
                            + "M20 16 H4 M8 12 L4 16 L8 20";

            case "reading", "▤" ->
                    "M4 4 H20 V20 H4 Z "
                            + "M8 8 H16 M8 12 H16 M8 16 H13";

            case "xp", "◆" ->
                    "M12 3 L21 12 L12 21 L3 12 Z "
                            + "M12 7 V17 M7 12 H17";

            case "sessions", "↻" ->
                    "M20 7 V3 L16 7 "
                            + "M19 7 A8 8 0 0 0 5 5 "
                            + "M4 17 V21 L8 17 "
                            + "M5 17 A8 8 0 0 0 19 19";

            case "correct", "✓" ->
                    "M4 12 L9 17 L20 6";

            default ->
                    "M12 3 A9 9 0 1 0 12 21 "
                            + "A9 9 0 1 0 12 3";
        };

        SVGPath icon = new SVGPath();
        icon.setContent(path);
        icon.setFill(Color.TRANSPARENT);
        icon.setStroke(Color.web(color));
        icon.setStrokeWidth(1.8);
        icon.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        icon.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
        icon.setScaleX(size / 24.0);
        icon.setScaleY(size / 24.0);

        return icon;
    }


    private VBox createSidebar() {
        VBox sidebar = new VBox(14);
        sidebar.setPadding(new Insets(28, 18, 24, 18));
        sidebar.setPrefWidth(225);
        sidebar.setStyle(
                "-fx-background-color: #0B1220;"
        );



        Label logo = new Label("<e/>");
        logo.setAlignment(Pos.CENTER);
        logo.setMinSize(62, 62);
        logo.setPrefSize(62, 62);

        logo.setStyle(
                "-fx-background-color: linear-gradient("
                        + "to bottom right, #A78BFA, #6D28D9, #4338CA);"
                        + "-fx-text-fill: white;"
                        + "-fx-font-family: 'Monospaced';"
                        + "-fx-font-size: 22px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 17px;"
                        + "-fx-effect: dropshadow(gaussian, #6D28D9, 16, 0.25, 0, 4);"
        );


        Label companyName = new Label("Shepherd");
        companyName.setStyle(
                "-fx-text-fill: #C4B5FD;"
                        + "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
        );

        Label brand = new Label("ENGLISH MASTER");
        brand.setStyle(
                "-fx-text-fill: " + TEXT + ";"
                        + "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
        );

        VBox brandBox = new VBox(10, logo, companyName, brand);
        brandBox.setAlignment(Pos.CENTER_LEFT);


        Label menuLabel = new Label("MAIN MENU");
        menuLabel.setStyle(
                "-fx-text-fill: " + MUTED + ";" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;"
        );
        Button home = navButton("dashboard", "Dashboard");
        Button practice = navButton("book", "Topic Practice");
        Button campaign = navButton("trophy", "Campaign Mode");
        Button progress = navButton("progress", "My Progress");
        Button achievements = navButton("achievement", "Achievements");

        home.setOnAction(e -> {
            setActiveNavigationButton(home);
            showDashboard();
        });

        practice.setOnAction(e -> {
            setActiveNavigationButton(practice);
            showPractice();
        });

        campaign.setOnAction(e -> {
            setActiveNavigationButton(campaign);
            showCampaign();
        });

        progress.setOnAction(e -> {
            setActiveNavigationButton(progress);
            showProgress();
        });

        achievements.setOnAction(e -> {
            setActiveNavigationButton(achievements);
            showAchievements();
        });

        setActiveNavigationButton(home);

        RegionSpacer spacer = new RegionSpacer();

        Label footer = new Label("LEARN • PRACTISE • MASTER");
        footer.setStyle(
                "-fx-text-fill: " + MUTED + ";" +
                        "-fx-font-size: 10px;"
        );

        sidebar.getChildren().addAll(
                brandBox,
                gap(22),
                menuLabel,
                home,
                practice,
                campaign,
                progress,
                achievements,
                spacer,
                footer
        );

        VBox.setVgrow(spacer, Priority.ALWAYS);

        return sidebar;
    }



    private Button navButton(String iconName, String text) {
        Button button = new Button(text);

        button.setGraphic(iconGraphic(iconName, 21, "#A5B4FC"));
        button.setGraphicTextGap(14);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinHeight(48);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(12, 14, 12, 14));

        String normalStyle =
                "-fx-background-color: transparent;"
                        + "-fx-text-fill: " + TEXT + ";"
                        + "-fx-font-size: 13px;"
                        + "-fx-background-radius: 10px;"
                        + "-fx-cursor: hand;";

        String hoverStyle =
                "-fx-background-color: #252044;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 13px;"
                        + "-fx-background-radius: 10px;"
                        + "-fx-cursor: hand;";

        String activeStyle =
                "-fx-background-color: linear-gradient("
                        + "to right, #4C1D95, #312E81);"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 10px;"
                        + "-fx-border-color: #8B5CF6;"
                        + "-fx-border-width: 0 0 0 3;"
                        + "-fx-border-radius: 10px;"
                        + "-fx-cursor: hand;";

        button.setStyle(normalStyle);

        button.setOnMouseEntered(e -> {
            if (button == activeNavButton) {
                button.setStyle(activeStyle);
            } else {
                button.setStyle(hoverStyle);
            }
        });

        button.setOnMouseExited(e -> {
            if (button == activeNavButton) {
                button.setStyle(activeStyle);
            } else {
                button.setStyle(normalStyle);
            }
        });

        return button;
    }


    private void setActiveNavigationButton(Button button) {
        activeNavButton = button;

        // Refresh all sidebar buttons so only one stays selected.
        if (root.getLeft() instanceof VBox sidebar) {
            for (Node node : sidebar.getChildren()) {
                if (node instanceof Button navButton) {
                    navButton.setStyle(
                            navButton == activeNavButton
                                    ? "-fx-background-color: linear-gradient("
                                    + "to right, #4C1D95, #312E81);"
                                    + "-fx-text-fill: white;"
                                    + "-fx-font-size: 13px;"
                                    + "-fx-font-weight: bold;"
                                    + "-fx-background-radius: 10px;"
                                    + "-fx-border-color: #8B5CF6;"
                                    + "-fx-border-width: 0 0 0 3;"
                                    + "-fx-border-radius: 10px;"
                                    + "-fx-cursor: hand;"
                                    : "-fx-background-color: transparent;"
                                    + "-fx-text-fill: " + TEXT + ";"
                                    + "-fx-font-size: 13px;"
                                    + "-fx-background-radius: 10px;"
                                    + "-fx-cursor: hand;"
                    );
                }
            }
        }
    }






    private void showDashboard() {
        VBox page = pageContainer();
        page.setSpacing(22);

        // Calculate real topic-practice statistics
        int totalAnswered = 0;
        int totalCorrect = 0;
        int completedTopics = 0;

        for (String topic : topics) {
            int answered = progress.getTopicQuestionsAnswered(topic);
            int correct = progress.getTopicCorrectAnswers(topic);

            totalAnswered += answered;
            totalCorrect += correct;

            if (answered > 0) {
                completedTopics++;
            }
        }

        double accuracy = totalAnswered == 0
                ? 0
                : totalCorrect * 100.0 / totalAnswered;

        // Dashboard heading
        Label greeting = heading("Ready to level up?");

        Label subtitle = mutedText(
                "Build your English skills one question at a time."
        );
        subtitle.setWrapText(true);

        VBox welcome = new VBox(6, greeting, subtitle);

        // Learning journey banner
        VBox hero = new VBox(15);
        hero.setPadding(new Insets(28));
        hero.setMaxWidth(Double.MAX_VALUE);

        hero.setStyle(
                "-fx-background-color: linear-gradient("
                        + "to bottom right, #4C1D95, #3730A3, #1D4ED8);"
                        + "-fx-background-radius: 18px;"
                        + "-fx-border-color: #6D5CE8;"
                        + "-fx-border-radius: 18px;"
        );

        Label heroTag = new Label("YOUR LEARNING JOURNEY");
        heroTag.setStyle(
                "-fx-text-fill: #DDD6FE;"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-letter-spacing: 1px;"
        );

        Label heroTitle = new Label(
                "Every question makes you stronger."
        );
        heroTitle.setWrapText(true);
        heroTitle.setStyle(
                "-fx-text-fill: white;"
                        + "-fx-font-size: 25px;"
                        + "-fx-font-weight: bold;"
        );

        Label heroDescription = new Label(
                "Practise your skills, earn XP and unlock new levels."
        );
        heroDescription.setWrapText(true);
        heroDescription.setStyle(
                "-fx-text-fill: #E0E7FF;"
                        + "-fx-font-size: 13px;"
        );

        Button play = primaryButton("Play Now  →");
        play.setStyle(
                "-fx-background-color: white;"
                        + "-fx-text-fill: #4C1D95;"
                        + "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-padding: 12px 24px;"
                        + "-fx-background-radius: 9px;"
                        + "-fx-cursor: hand;"
        );
        play.setOnAction(e -> showPractice());

        hero.getChildren().addAll(
                heroTag,
                heroTitle,
                heroDescription,
                play
        );

        // Overview statistics
        Label statsTitle = sectionHeading("YOUR OVERVIEW");

        GridPane stats = new GridPane();
        stats.setHgap(14);
        stats.setVgap(14);
        stats.setMaxWidth(Double.MAX_VALUE);

        stats.add(
                statCard(
                        "xp",
                        "XP POINTS",
                        progress.getTotalXP() + " XP",
                        "#FBBF24"
                ),
                0, 0
        );

        stats.add(
                statCard(
                        "book",
                        "TOPICS PRACTISED",
                        completedTopics + " / " + topics.length,
                        "#34D399"
                ),
                1, 0
        );

        stats.add(
                statCard(
                        "progress",
                        "QUESTIONS ANSWERED",
                        String.valueOf(totalAnswered),
                        "#60A5FA"
                ),
                2, 0
        );

        for (int i = 0; i < 3; i++) {
            ColumnGrow(stats, i);
        }

        // Learning performance summary
        Label performanceTitle = sectionHeading("YOUR PERFORMANCE");

        VBox performanceCard = new VBox(12);
        performanceCard.setPadding(new Insets(20));
        performanceCard.setMaxWidth(Double.MAX_VALUE);
        performanceCard.setStyle(
                "-fx-background-color: " + PANEL + ";"
                        + "-fx-background-radius: 14px;"
                        + "-fx-border-color: #293244;"
                        + "-fx-border-radius: 14px;"
        );

        HBox accuracyRow = new HBox(12);
        accuracyRow.setAlignment(Pos.CENTER_LEFT);

        Label accuracyLabel = new Label("Practice accuracy");
        accuracyLabel.setStyle(
                "-fx-text-fill: " + TEXT + ";"
                        + "-fx-font-size: 14px;"
                        + "-fx-font-weight: bold;"
        );

        javafx.scene.layout.Region accuracySpacer =
                new javafx.scene.layout.Region();

        HBox.setHgrow(accuracySpacer, Priority.ALWAYS);

        Label accuracyValue = new Label(
                String.format("%.1f%%", accuracy)
        );
        accuracyValue.setStyle(
                "-fx-text-fill: #60A5FA;"
                        + "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
        );

        accuracyRow.getChildren().addAll(
                accuracyLabel,
                accuracySpacer,
                accuracyValue
        );

        ProgressBar accuracyBar = new ProgressBar(
                Math.max(0, Math.min(1, accuracy / 100.0))
        );
        accuracyBar.setMaxWidth(Double.MAX_VALUE);
        accuracyBar.setPrefHeight(9);
        accuracyBar.setStyle("-fx-accent: #818CF8;");

        Label performanceHint = mutedText(
                totalAnswered == 0
                        ? "Start Topic Practice to record your first results."
                        : totalCorrect + " correct answers out of "
                        + totalAnswered + " questions."
        );
        performanceHint.setWrapText(true);

        performanceCard.getChildren().addAll(
                accuracyRow,
                accuracyBar,
                performanceHint
        );

        // Campaign progress summary
        Label campaignTitle = sectionHeading("CAMPAIGN PROGRESS");

        int completedLevels = progress.getCompletedLevelsCount();

        VBox campaignCard = new VBox(12);
        campaignCard.setPadding(new Insets(20));
        campaignCard.setMaxWidth(Double.MAX_VALUE);
        campaignCard.setStyle(
                "-fx-background-color: " + PANEL + ";"
                        + "-fx-background-radius: 14px;"
                        + "-fx-border-color: #293244;"
                        + "-fx-border-radius: 14px;"
        );

        HBox campaignRow = new HBox(12);
        campaignRow.setAlignment(Pos.CENTER_LEFT);

        Label campaignLabel = new Label("Levels completed");
        campaignLabel.setStyle(
                "-fx-text-fill: " + TEXT + ";"
                        + "-fx-font-size: 14px;"
                        + "-fx-font-weight: bold;"
        );

        javafx.scene.layout.Region campaignSpacer =
                new javafx.scene.layout.Region();

        HBox.setHgrow(campaignSpacer, Priority.ALWAYS);

        Label campaignValue = new Label(
                completedLevels + " / 3"
        );
        campaignValue.setStyle(
                "-fx-text-fill: #34D399;"
                        + "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
        );

        campaignRow.getChildren().addAll(
                campaignLabel,
                campaignSpacer,
                campaignValue
        );

        ProgressBar campaignBar = new ProgressBar(
                Math.max(0, Math.min(1, completedLevels / 3.0))
        );
        campaignBar.setMaxWidth(Double.MAX_VALUE);
        campaignBar.setPrefHeight(9);
        campaignBar.setStyle("-fx-accent: #34D399;");

        Label campaignHint = mutedText(
                completedLevels == 3
                        ? "All campaign levels completed. Excellent work!"
                        : "Pass each campaign level with at least 70% "
                        + "to unlock the next stage."
        );
        campaignHint.setWrapText(true);

        Button campaignButton = secondaryButton("Continue Campaign  →");
        campaignButton.setOnAction(e -> showCampaign());

        campaignCard.getChildren().addAll(
                campaignRow,
                campaignBar,
                campaignHint,
                campaignButton
        );

        // Quick actions
        Label actionsTitle = sectionHeading("CHOOSE YOUR NEXT MOVE");

        HBox quickActions = new HBox(14);
        quickActions.setFillHeight(true);
        quickActions.setMaxWidth(Double.MAX_VALUE);

        VBox practiceCard = actionCard(
                "book",
                "Topic Practice",
                "Choose a topic and difficulty.",
                this::showPractice
        );

        VBox achievementsCard = actionCard(
                "trophy",
                "Achievements",
                "Explore your milestones and rewards.",
                this::showAchievements
        );

        practiceCard.setMaxWidth(Double.MAX_VALUE);
        achievementsCard.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(practiceCard, Priority.ALWAYS);
        HBox.setHgrow(achievementsCard, Priority.ALWAYS);

        quickActions.getChildren().addAll(
                practiceCard,
                achievementsCard
        );

        // Assemble the dashboard
        page.getChildren().addAll(
                welcome,
                hero,
                statsTitle,
                stats,
                performanceTitle,
                performanceCard,
                campaignTitle,
                campaignCard,
                actionsTitle,
                quickActions
        );

        showScrollablePage(page);
    }





    private void showCampaign() {
        VBox page = pageContainer();
        page.setSpacing(20);

        Label title = heading("Campaign Mode");
        Label subtitle = mutedText(
                "Your journey to English mastery. Pass each level "
                        + "with at least 70% to unlock the next."
        );
        subtitle.setWrapText(true);

        // Campaign summary banner
        VBox banner = new VBox(10);
        banner.setPadding(new Insets(24));
        banner.setMaxWidth(Double.MAX_VALUE);
        banner.setStyle(
                "-fx-background-color: linear-gradient("
                        + "to right, #312E81, #4C1D95, #1D4ED8);"
                        + "-fx-background-radius: 16px;"
        );

        Label bannerTitle = new Label("YOUR CAMPAIGN");
        bannerTitle.setStyle(
                "-fx-text-fill: #DDD6FE;"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
        );

        Label bannerDescription = new Label(
                "Learn. Practise. Progress."
        );
        bannerDescription.setWrapText(true);
        bannerDescription.setStyle(
                "-fx-text-fill: white;"
                        + "-fx-font-size: 22px;"
                        + "-fx-font-weight: bold;"
        );

        Label bannerHint = new Label(
                "Earn stars and XP as you advance through three levels."
        );
        bannerHint.setWrapText(true);
        bannerHint.setStyle("-fx-text-fill: #E0E7FF;");

        banner.getChildren().addAll(
                bannerTitle,
                bannerDescription,
                bannerHint
        );

        Label levelsTitle = sectionHeading("YOUR LEVELS");

        VBox levels = new VBox(16);
        levels.setMaxWidth(Double.MAX_VALUE);

        addCampaignLevel(
                levels,
                1,
                "Beginner",
                "Build your foundation in English.",
                progress.isBeginnerUnlocked()
        );

        addCampaignLevel(
                levels,
                2,
                "Intermediate",
                "Strengthen your skills with more challenging questions.",
                progress.isIntermediateUnlocked()
        );

        addCampaignLevel(
                levels,
                3,
                "Advanced",
                "Test your knowledge and work towards English mastery.",
                progress.isAdvancedUnlocked()
        );

        Button back = secondaryButton("← Back to Dashboard");
        back.setOnAction(e -> showDashboard());

        page.getChildren().addAll(
                title,
                subtitle,
                banner,
                levelsTitle,
                levels,
                back
        );

        showScrollablePage(page);
    }



    private void addCampaignLevel(
            VBox page,
            int levelNumber,
            String levelName,
            String description,
            boolean unlocked
    ) {
        int bestStars = getBestStarsForLevel(levelNumber);

        String accent = switch (levelNumber) {
            case 1 -> "#34D399";
            case 2 -> "#60A5FA";
            case 3 -> "#C4B5FD";
            default -> "#A78BFA";
        };

        String levelLabel = switch (levelNumber) {
            case 1 -> "LEVEL 01";
            case 2 -> "LEVEL 02";
            case 3 -> "LEVEL 03";
            default -> "LEVEL";
        };

        Label number = new Label(levelLabel);
        number.setStyle(
                "-fx-text-fill: " + accent + ";"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
        );

        Label name = heading(levelName);

        Label details = mutedText(description);
        details.setWrapText(true);

        Label status = new Label(
                unlocked
                        ? "●  AVAILABLE"
                        : "🔒  LOCKED — Complete the previous level"
        );
        status.setWrapText(true);
        status.setStyle(
                "-fx-text-fill: "
                        + (unlocked ? "#34D399" : "#9CA3AF") + ";"
                        + "-fx-font-size: 12px;"
                        + "-fx-font-weight: bold;"
        );

        Label stars = new Label(
                "BEST STARS   "
                        + "★".repeat(bestStars)
                        + "☆".repeat(Math.max(0, 3 - bestStars))
        );
        stars.setStyle(
                "-fx-text-fill: #FBBF24;"
                        + "-fx-font-size: 15px;"
                        + "-fx-font-weight: bold;"
        );

        Label requirement = mutedText(
                "Pass requirement: 70%  •  20 questions"
        );

        Button play = primaryButton(
                unlocked ? "Play " + levelName + "  →" : "Level Locked"
        );
        play.setDisable(!unlocked);
        play.setMaxWidth(Double.MAX_VALUE);

        play.setOnAction(e -> {
            if (unlocked) {
                startCampaignQuiz(levelNumber);
            }
        });

        VBox information = new VBox(
                9,
                number,
                name,
                details,
                status,
                stars,
                requirement
        );
        information.setMaxWidth(Double.MAX_VALUE);

        VBox card = new VBox(16, information, play);
        card.setPadding(new Insets(22));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(
                "-fx-background-color: "
                        + (unlocked ? "#1B2638" : "#141D2C") + ";"
                        + "-fx-background-radius: 15px;"
                        + "-fx-border-color: "
                        + (unlocked ? "#374151" : "#293244") + ";"
                        + "-fx-border-radius: 15px;"
        );

        page.getChildren().add(card);
    }



    private void showPractice() {
        VBox page = pageContainer();

        page.getChildren().addAll(
                heading("Topic Practice"),
                mutedText(
                        "Choose what to practise and set your difficulty."
                ),
                gap(8)
        );

        GridPane topicGrid = new GridPane();
        topicGrid.setHgap(16);
        topicGrid.setVgap(16);
        topicGrid.setMaxWidth(Double.MAX_VALUE);

        String[] descriptions = {
                "Master tenses, agreement and grammar rules.",
                "Learn new words and their meanings.",
                "Improve your spelling accuracy.",
                "Use commas, apostrophes and full stops correctly.",
                "Build clear and well-structured sentences.",
                "Explore words with similar and opposite meanings.",
                "Read passages and answer comprehension questions."
        };

        String[] icons = {
                "grammar",
                "book",
                "spelling",
                "punctuation",
                "sentence",
                "synonyms",
                "reading"
        };

        for (int i = 0; i < topics.length; i++) {
            final String topic = topics[i];

            VBox card = actionCard(
                    icons[i],
                    topics[i],
                    descriptions[i],
                    () -> showDifficultySelection(topic)
            );

            topicGrid.add(card, i % 2, i / 2);
            GridPane.setHgrow(card, Priority.ALWAYS);
        }

        javafx.scene.layout.ColumnConstraints firstColumn =
                new javafx.scene.layout.ColumnConstraints();

        firstColumn.setPercentWidth(50);
        firstColumn.setHgrow(Priority.ALWAYS);
        firstColumn.setFillWidth(true);

        javafx.scene.layout.ColumnConstraints secondColumn =
                new javafx.scene.layout.ColumnConstraints();

        secondColumn.setPercentWidth(50);
        secondColumn.setHgrow(Priority.ALWAYS);
        secondColumn.setFillWidth(true);

        topicGrid.getColumnConstraints().clear();
        topicGrid.getColumnConstraints().addAll(
                firstColumn,
                secondColumn
        );

        page.getChildren().add(topicGrid);
        showScrollablePage(page);
    }



    private void startCampaignQuiz(int levelNumber) {
        final int QUESTIONS_PER_LEVEL = 20;

        String difficulty;

        switch (levelNumber) {
            case 1 -> difficulty = "Beginner";
            case 2 -> difficulty = "Intermediate";
            case 3 -> difficulty = "Advanced";
            default -> {
                showMessage(
                        "Invalid Campaign Level",
                        "The selected level is not valid.",
                        "Return to Campaign Mode and select a valid level."
                );
                return;
            }
        }

        // Confirm that the selected campaign level is unlocked.
        boolean unlocked = switch (levelNumber) {
            case 1 -> progress.isBeginnerUnlocked();
            case 2 -> progress.isIntermediateUnlocked();
            case 3 -> progress.isAdvancedUnlocked();
            default -> false;
        };

        if (!unlocked) {
            showMessage(
                    "Level Locked",
                    "Complete the previous campaign level first.",
                    "You need a passing score of at least 70% to unlock the next level."
            );
            return;
        }

        try {
            // Load the complete question bank.
            Question[] allQuestions =
                    QuestionBank.loadFromFile("questions.txt");

            if (allQuestions == null || allQuestions.length == 0) {
                showMessage(
                        "Question Bank Empty",
                        "No questions are available.",
                        "Check questions.txt and try again."
                );
                return;
            }

            // Collect questions from all topics at the selected difficulty.
            Question[] availableQuestions =
                    java.util.Arrays.stream(allQuestions)
                            .filter(java.util.Objects::nonNull)
                            .filter(question ->
                                    difficulty.equalsIgnoreCase(
                                            question.getDifficulty()
                                    )
                            )
                            .toArray(Question[]::new);

            // Ensure there are enough questions for a full campaign.
            if (availableQuestions.length < QUESTIONS_PER_LEVEL) {
                showMessage(
                        "Not Enough Questions",
                        difficulty + " requires "
                                + QUESTIONS_PER_LEVEL + " questions.",
                        "Only " + availableQuestions.length
                                + " questions were found at this difficulty. "
                                + "Add more questions to questions.txt."
                );
                return;
            }

            // Shuffle all matching questions randomly.
            java.util.Collections.shuffle(
                    java.util.Arrays.asList(availableQuestions)
            );

            // Select exactly 20 different questions for this session.
            sessionQuestions = java.util.Arrays.copyOf(
                    availableQuestions,
                    QUESTIONS_PER_LEVEL
            );

            // Initialize campaign session state.
            currentCampaignLevel = levelNumber;
            currentTopic = null;
            currentDifficulty = difficulty;
            campaignMode = true;

            currentQuestionIndex = 0;
            guiCorrectAnswers = 0;
            guiWrongAnswers = 0;
            guiScore = 0;
            resultsSaved = false;

            // Display the first campaign question.
            showCurrentQuestion();

        } catch (Exception ex) {
            showMessage(
                    "Unable to Load Campaign",
                    "The campaign could not start.",
                    ex.getMessage() == null
                            ? "Check questions.txt and the question bank."
                            : ex.getMessage()
            );
        }
    }



    private void showDifficultySelection(String topic) {
        VBox page = pageContainer();

        Label title = heading(topic);
        Label subtitle = mutedText(
                "Select a difficulty to prepare your practice session."
        );


        ComboBox<String> difficulty = new ComboBox<>();

        difficulty.getItems().addAll(
                "Beginner",
                "Intermediate",
                "Advanced"
        );

        difficulty.setValue("Beginner");
        difficulty.setMaxWidth(350);

        difficulty.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-font-size: 14px;"
        );

        difficulty.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setTextFill(javafx.scene.paint.Color.web("#111827"));
                    setStyle(
                            "-fx-background-color: white;" +
                                    "-fx-padding: 8px;"
                    );
                }
            }
        });

        difficulty.setCellFactory(list -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setTextFill(javafx.scene.paint.Color.web("#111827"));
                    setStyle(
                            "-fx-background-color: white;" +
                                    "-fx-padding: 8px;"
                    );
                }
            }
        });


        Button start = primaryButton("Start Practice");
        start.setOnAction(e -> {
            String selectedDifficulty = difficulty.getValue();

            if (selectedDifficulty == null) {
                return;
            }

            startGuiQuiz(topic, selectedDifficulty);
        });

        Button back = secondaryButton("← Back to Topics");
        back.setOnAction(e -> showPractice());

        VBox panel = new VBox(
                18,
                title,
                subtitle,
                sectionHeading("DIFFICULTY"),
                difficulty,
                start,
                back
        );

        panel.setPadding(new Insets(28));
        panel.setMaxWidth(600);
        panel.setStyle(
                "-fx-background-color: " + PANEL + ";" +
                        "-fx-background-radius: 16px;"
        );

        page.getChildren().add(panel);
        showScrollablePage(page);
    }


    private void startGuiQuiz(String topic, String difficulty) {
        currentTopic = topic;
        currentDifficulty = difficulty;
        currentQuestionIndex = 0;
        guiCorrectAnswers = 0;
        guiWrongAnswers = 0;
        guiScore = 0;
        resultsSaved = false;
        campaignMode = false;
        try {
            Question[] allQuestions =
                    QuestionBank.loadFromFile("questions.txt");

            sessionQuestions =
                    Quiz.filterByTopicAndDifficulty(
                            allQuestions,
                            topic,
                            difficulty
                    );

            if (sessionQuestions == null ||
                    sessionQuestions.length == 0) {
                showMessage(
                        "No Questions Found",
                        topic + " • " + difficulty,
                        "Check questions.txt and try again."
                );
                return;
            }

            // Randomize the order without changing the source array.
            sessionQuestions = sessionQuestions.clone();

            java.util.Collections.shuffle(
                    java.util.Arrays.asList(sessionQuestions)
            );

            currentQuestionIndex = 0;
            guiCorrectAnswers = 0;
            guiWrongAnswers = 0;
            guiScore = 0;

            showCurrentQuestion();

        } catch (Exception ex) {
            showMessage(
                    "Unable to Load Questions",
                    "English Master could not start this session.",
                    ex.getMessage() == null
                            ? "Check questions.txt and the question bank."
                            : ex.getMessage()
            );
        }
    }

    private void showCurrentQuestion() {
        answerSubmitted = false;

        Question question =
                sessionQuestions[currentQuestionIndex];

        VBox page = pageContainer();

        Label title = heading( campaignMode ? "CampaingMode" : "Topic Practice");

        Label progress = mutedText(
                "Question " + (currentQuestionIndex + 1)
                        + " of " + sessionQuestions.length
        );

        Label topicLabel = mutedText(
                question.getTopic() + " • "
                        + question.getDifficulty()
        );

        Label questionText = new Label(
                question.getQuestionText()
        );

        questionText.setWrapText(true);
        questionText.setStyle(
                "-fx-text-fill: " + TEXT + ";"
                        + "-fx-font-size: 20px;"
                        + "-fx-font-weight: bold;"
        );

        VBox questionCard = new VBox(
                14, progress, topicLabel, questionText
        );

        questionCard.setPadding(new Insets(24));
        questionCard.setStyle(
                "-fx-background-color: " + PANEL + ";"
                        + "-fx-background-radius: 14px;"
        );

        VBox optionsBox = new VBox(12);

        Label feedback = mutedText("");

        Button nextButton = primaryButton("Next Question →");
        nextButton.setDisable(true);

        String[] options = question.getOptions();
        Button[] optionButtons = new Button[options.length];

        for (int i = 0; i < options.length; i++) {
            final int answerNumber = i + 1;

            Button optionButton = secondaryButton(
                    (char) ('A' + i) + ".  " + options[i]
            );

            optionButton.setMaxWidth(Double.MAX_VALUE);
            optionButton.setAlignment(Pos.CENTER_LEFT);
            optionButton.setWrapText(true);

            optionButton.setOnAction(e -> {
                if (answerSubmitted) {
                    return;
                }

                answerSubmitted = true;

                for (Button button : optionButtons) {
                    button.setDisable(true);
                }

                boolean correct =
                        answerNumber == question.getCorrectAnswer();

                if (correct) {
                    guiCorrectAnswers++;
                    guiScore += 10;

                    feedback.setText(
                            "Correct! +10 points\n"
                                    + question.getExplanation()
                    );

                    feedback.setStyle(
                            "-fx-text-fill: #34D399;"
                                    + "-fx-font-size: 14px;"
                    );
                } else {
                    guiWrongAnswers++;

                    String[] currentOptions = question.getOptions();
                    int correctIndex = question.getCorrectAnswer() - 1;

                    String correctText =
                            correctIndex >= 0
                                    && correctIndex < currentOptions.length
                                    ? currentOptions[correctIndex]
                                    : "Unavailable";

                    feedback.setText(
                            "Not quite. Correct answer: "
                                    + correctText
                                    + "\n"
                                    + question.getExplanation()
                    );

                    feedback.setStyle(
                            "-fx-text-fill: #FCA5A5;"
                                    + "-fx-font-size: 14px;"
                    );
                }

                nextButton.setDisable(false);
            });

            optionButtons[i] = optionButton;
            optionsBox.getChildren().add(optionButton);
        }

        nextButton.setOnAction(e -> {
            currentQuestionIndex++;

            if (currentQuestionIndex < sessionQuestions.length) {
                showCurrentQuestion();
            } else {
                showGuiResults();
            }
        });

        Button exitButton = secondaryButton("Exit Practice");
        exitButton.setOnAction(e -> showDashboard());

        page.getChildren().addAll(
                title,
                questionCard,
                sectionHeading("SELECT YOUR ANSWER"),
                optionsBox,
                feedback,
                nextButton,
                exitButton
        );

        showScrollablePage(page);
    }



    private void showGuiResults() {
        double percentage = sessionQuestions.length == 0
                ? 0
                : guiCorrectAnswers * 100.0 / sessionQuestions.length;

        if (!resultsSaved) {

            if (campaignMode) {
                progress.recordResult(currentCampaignLevel, percentage);
                boolean passed = percentage >= 70.0;

                String levelName = switch (currentCampaignLevel) {
                    case 1 -> "Beginner";
                    case 2 -> "Intermediate";
                    case 3 -> "Advanced";
                    default -> "Unknown";
                };

                // Determine whether the next level is unlocked.
                String progressionMessage;

                if (!passed) {
                    progressionMessage =
                            "You need at least 70% to pass. "
                                    + "Replay this level to try again.";
                } else if (currentCampaignLevel == 1
                        && progress.isIntermediateUnlocked()) {
                    progressionMessage =
                            "Intermediate is now available!";
                } else if (currentCampaignLevel == 2
                        && progress.isAdvancedUnlocked()) {
                    progressionMessage =
                            "Advanced is now available!";
                } else if (currentCampaignLevel == 3) {
                    progressionMessage =
                            "You completed the Advanced campaign level!";
                } else {
                    progressionMessage =
                            "Level passed! Your campaign progress has been saved.";
                }

                Label statusLabel = new Label(
                        passed ? "✓ LEVEL PASSED" : "LEVEL NOT PASSED"
                );

                statusLabel.setStyle(
                        "-fx-text-fill: " + (passed ? "#34D399" : "#FCA5A5") + ";"
                                + "-fx-font-size: 18px;"
                                + "-fx-font-weight: bold;"
                );

                int earnedStars = progress.calculateStars(percentage);

                Label starsLabel = new Label(
                        "Stars earned: "
                                + "★".repeat(earnedStars)
                                + "☆".repeat(Math.max(0, 3 - earnedStars))
                );

                starsLabel.setStyle(
                        "-fx-text-fill: #FBBF24;"
                                + "-fx-font-size: 22px;"
                                + "-fx-font-weight: bold;"
                );

                Label progressionLabel = mutedText(progressionMessage);

//                page.getChildren().addAll(
//                        statusLabel,
//                        starsLabel,
//                        progressionLabel,
//                        mutedText(
//                                "Level: " + levelName
//                                        + "\nPassing score: 70%"
//                                        + "\nXP earned: " + (guiCorrectAnswers * 10)
//                        )
//                );

                Button replayButton =
                        primaryButton("↻ Replay " + levelName);

                replayButton.setOnAction(
                        e -> startCampaignQuiz(currentCampaignLevel)
                );

                Button campaignButton =
                        secondaryButton("Return to Campaign");

                campaignButton.setOnAction(e -> showCampaign());

                Button homeButton =
                        secondaryButton("Return to Dashboard");

                homeButton.setOnAction(e -> showDashboard());

//                content.getChildren().addAll(
//                        replayButton,
//                        campaignButton,
//                        homeButton
//                );

            } else {
                progress.recordTopicResult(
                        currentTopic,
                        guiCorrectAnswers,
                        sessionQuestions.length
                );
            }

            progress.checkAchievements(
                    campaignMode ? currentCampaignLevel : 0,
                    percentage,
                    guiCorrectAnswers,
                    sessionQuestions.length
            );

            progress.addXP(guiCorrectAnswers * 10);
            resultsSaved = true;
        }

        VBox page = pageContainer();

        String resultHeading = campaignMode
                ? "Campaign Level Complete!"
                : "Practice Complete!";

        page.getChildren().add(heading(resultHeading));

        if (campaignMode) {
            String levelName = switch (currentCampaignLevel) {
                case 1 -> "Beginner";
                case 2 -> "Intermediate";
                case 3 -> "Advanced";
                default -> "Unknown";
            };

            page.getChildren().add(
                    mutedText("Campaign Level: " + levelName)
            );
        } else {
            page.getChildren().add(
                    mutedText(currentTopic + " • " + currentDifficulty)
            );
        }

        Label scoreLabel = new Label(
                "Score: " + guiScore + " points"
        );

        scoreLabel.setStyle(
                "-fx-text-fill: #FBBF24;"
                        + "-fx-font-size: 24px;"
                        + "-fx-font-weight: bold;"
        );

        Label summary = mutedText(
                "Correct: " + guiCorrectAnswers
                        + "   |   Incorrect: " + guiWrongAnswers
                        + "\nAccuracy: "
                        + String.format("%.1f%%", percentage)
                        + "\nXP earned: " + (guiCorrectAnswers * 10)
        );

        page.getChildren().addAll(scoreLabel, summary);

        if (campaignMode) {
            boolean passed = percentage >= 70.0;

            String message;

            if (!passed) {
                message = "You need at least 70% to pass. "
                        + "Practise and try this level again.";
            } else if (currentCampaignLevel < 3) {
                message = "Congratulations! You passed this level. "
                        + "Your next campaign level is now available.";
            } else {
                message = "Congratulations! You completed the "
                        + "Advanced campaign level.";
            }

            page.getChildren().add(mutedText(message));
            int earnedStars = progress.calculateStars(percentage);

            Label starsLabel = new Label(
                    "Stars earned: " + "★".repeat(earnedStars)
                            + "☆".repeat(Math.max(0, 3 - earnedStars))
            );

            starsLabel.setStyle(
                    "-fx-text-fill: #FBBF24;"
                            + "-fx-font-size: 22px;"
                            + "-fx-font-weight: bold;"
            );

            page.getChildren().add(starsLabel);

            page.getChildren().add(
                    mutedText(
                            "Best campaign score: "
                                    + String.format(
                                    "%.1f%%",
                                    progress.getOverallBestScore()
                            )
                    )
            );

            Button campaignButton =
                    primaryButton("Return to Campaign");

            campaignButton.setOnAction(e -> showCampaign());

            Button homeButton =
                    secondaryButton("Return to Dashboard");

            homeButton.setOnAction(e -> showDashboard());

            page.getChildren().addAll(
                    campaignButton,
                    homeButton
            );

        } else {
            Label savedProgress = mutedText(
                    "Total questions answered in " + currentTopic + ": "
                            + progress.getTopicQuestionsAnswered(currentTopic)
                            + "\nTopic accuracy: "
                            + String.format(
                            "%.1f%%",
                            progress.getTopicAccuracy(currentTopic)
                    )
                            + "\nPractice sessions: "
                            + progress.getTopicPracticeSessions(currentTopic)
            );

            Button retry = primaryButton("Practise Again");
            retry.setOnAction(e -> showPractice());

            Button home = secondaryButton("Return to Dashboard");
            home.setOnAction(e -> showDashboard());

            page.getChildren().addAll(
                    savedProgress,
                    retry,
                    home
            );
        }

        showScrollablePage(page);
    }


    // Keep the remainder of your existing showGuiResults()
        // method below this point, including the result labels,
        // buttons and root.setCenter(page).





    private void showProgress() {
        VBox page = pageContainer();
        page.setSpacing(20);

        // Page heading
        Label title = heading("My Progress");

        Label subtitle = mutedText(
                "Track your learning journey, review your performance, "
                        + "and see how far you have progressed."
        );
        subtitle.setWrapText(true);

        // Calculate overall topic practice statistics
        int totalAnswered = 0;
        int totalCorrect = 0;
        int totalSessions = 0;

        for (String topic : topics) {
            totalAnswered += progress.getTopicQuestionsAnswered(topic);
            totalCorrect += progress.getTopicCorrectAnswers(topic);
            totalSessions += progress.getTopicPracticeSessions(topic);
        }

        double accuracy = totalAnswered == 0
                ? 0
                : totalCorrect * 100.0 / totalAnswered;

        // Overview statistics
        GridPane stats = new GridPane();
        stats.setHgap(14);
        stats.setVgap(14);
        stats.setMaxWidth(Double.MAX_VALUE);

        stats.add(
                statCard(
                        "✓",
                        "QUESTIONS ANSWERED",
                        String.valueOf(totalAnswered),
                        "#34D399"
                ),
                0, 0
        );

        stats.add(
                statCard(
                        "◎",
                        "OVERALL ACCURACY",
                        String.format("%.1f%%", accuracy),
                        "#60A5FA"
                ),
                1, 0
        );

        stats.add(
                statCard(
                        "↻",
                        "PRACTICE SESSIONS",
                        String.valueOf(totalSessions),
                        "#F59E0B"
                ),
                2, 0
        );

        for (int i = 0; i < 3; i++) {
            ColumnGrow(stats, i);
        }

        // Campaign statistics
        GridPane campaignStats = new GridPane();
        campaignStats.setHgap(14);
        campaignStats.setVgap(14);
        campaignStats.setMaxWidth(Double.MAX_VALUE);

        double bestScore = progress.getOverallBestScore();

        campaignStats.add(
                statCard(
                        "✓",
                        "LEVELS COMPLETED",
                        progress.getCompletedLevelsCount() + " / 3",
                        "#34D399"
                ),
                0, 0
        );

        campaignStats.add(
                statCard(
                        "↻",
                        "LEVELS PLAYED",
                        String.valueOf(progress.getLevelsPlayedCount()),
                        "#60A5FA"
                ),
                1, 0
        );

        campaignStats.add(
                statCard(
                        "★",
                        "BEST CAMPAIGN SCORE",
                        bestScore < 0
                                ? "Not played"
                                : String.format("%.1f%%", bestScore),
                        "#F59E0B"
                ),
                2, 0
        );

        for (int i = 0; i < 3; i++) {
            ColumnGrow(campaignStats, i);
        }

        // Topic performance cards
        VBox topicCards = new VBox(14);
        topicCards.setMaxWidth(Double.MAX_VALUE);

        for (String topic : topics) {
            int answered = progress.getTopicQuestionsAnswered(topic);
            int correct = progress.getTopicCorrectAnswers(topic);
            int sessions = progress.getTopicPracticeSessions(topic);

            double topicAccuracy = answered == 0
                    ? 0
                    : correct * 100.0 / answered;

            // Topic title
            Label topicName = new Label(topic);
            topicName.setWrapText(true);
            topicName.setStyle(
                    "-fx-text-fill: " + TEXT + ";"
                            + "-fx-font-size: 14px;"
                            + "-fx-font-weight: bold;"
            );

            // Accuracy label
            Label accuracyLabel = new Label(
                    String.format("%.1f%%", topicAccuracy)
            );
            accuracyLabel.setStyle(
                    "-fx-text-fill: "
                            + (answered == 0 ? "#9CA3AF" : "#60A5FA") + ";"
                            + "-fx-font-size: 14px;"
                            + "-fx-font-weight: bold;"
            );

            // Topic heading row
            HBox topicHeader = new HBox(
                    12,
                    topicName,
                    new javafx.scene.layout.Region(),
                    accuracyLabel
            );
            topicHeader.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(
                    topicHeader.getChildren().get(1),
                    Priority.ALWAYS
            );

            // Accuracy progress bar
            ProgressBar accuracyBar = new ProgressBar(
                    Math.max(0, Math.min(1, topicAccuracy / 100.0))
            );
            accuracyBar.setMaxWidth(Double.MAX_VALUE);
            accuracyBar.setPrefHeight(8);
            accuracyBar.setStyle(
                    "-fx-accent: "
                            + (answered == 0 ? "#4B5563" : "#818CF8") + ";"
            );

            // Supporting statistics
            Label topicStats = mutedText(
                    answered + " questions answered  •  "
                            + correct + " correct  •  "
                            + sessions + " sessions"
            );
            topicStats.setWrapText(true);

            Label practiceStatus = mutedText(
                    answered == 0
                            ? "Start practising this topic to build your progress."
                            : "Accuracy based on your recorded practice answers."
            );
            practiceStatus.setWrapText(true);

            VBox card = new VBox(
                    12,
                    topicHeader,
                    accuracyBar,
                    topicStats,
                    practiceStatus
            );

            card.setPadding(new Insets(18));
            card.setMaxWidth(Double.MAX_VALUE);
            card.setStyle(
                    "-fx-background-color: " + PANEL + ";"
                            + "-fx-background-radius: 14px;"
                            + "-fx-border-color: #293244;"
                            + "-fx-border-radius: 14px;"
                            + "-fx-border-width: 1px;"
            );

            topicCards.getChildren().add(card);
        }

        // Back navigation
        Button back = secondaryButton("← Back to Dashboard");
        back.setOnAction(e -> showDashboard());

        page.getChildren().addAll(
                title,
                subtitle,
                sectionHeading("LEARNING OVERVIEW"),
                stats,
                sectionHeading("CAMPAIGN PERFORMANCE"),
                campaignStats,
                sectionHeading("TOPIC PERFORMANCE"),
                topicCards,
                back
        );

        showScrollablePage(page);
    }



    private int getBestStarsForLevel(int levelNumber) {
        return switch (levelNumber) {
            case 1 -> progress.getBeginnerBestStars();
            case 2 -> progress.getIntermediateBestStars();
            case 3 -> progress.getAdvancedBestStars();
            default -> 0;
        };
    }





    private void showAchievements() {
        VBox page = pageContainer();
        page.setSpacing(20);

        // Page heading
        Label title = heading("Achievements");

        Label subtitle = mutedText(
                "Every milestone counts. Keep practising to unlock them all."
        );
        subtitle.setWrapText(true);

        // Read the existing achievement states
        boolean firstSteps = progress.isFirstStepsUnlocked();
        boolean grammarChampion = progress.isGrammarChampionUnlocked();
        boolean perfectScore = progress.isPerfectScoreUnlocked();
        boolean masterInProgress = progress.isMasterInProgressUnlocked();

        int unlockedCount = 0;

        if (firstSteps) unlockedCount++;
        if (grammarChampion) unlockedCount++;
        if (perfectScore) unlockedCount++;
        if (masterInProgress) unlockedCount++;

        // Achievement summary banner
        VBox banner = new VBox(10);
        banner.setPadding(new Insets(24));
        banner.setMaxWidth(Double.MAX_VALUE);

        banner.setStyle(
                "-fx-background-color: linear-gradient("
                        + "to right, #312E81, #4C1D95, #1D4ED8);"
                        + "-fx-background-radius: 16px;"
        );

        Label bannerLabel = new Label("YOUR ACHIEVEMENT COLLECTION");
        bannerLabel.setStyle(
                "-fx-text-fill: #DDD6FE;"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
        );

        Label countLabel = new Label(
                unlockedCount + " / 4 Achievements Unlocked"
        );
        countLabel.setWrapText(true);
        countLabel.setStyle(
                "-fx-text-fill: white;"
                        + "-fx-font-size: 23px;"
                        + "-fx-font-weight: bold;"
        );

        Label encouragement = new Label(
                unlockedCount == 4
                        ? "Outstanding! You have unlocked every achievement."
                        : "Keep learning and practising to unlock your next milestone."
        );
        encouragement.setWrapText(true);
        encouragement.setStyle("-fx-text-fill: #E0E7FF;");

        // Overall completion progress
        ProgressBar achievementProgress = new ProgressBar(
                unlockedCount / 4.0
        );
        achievementProgress.setMaxWidth(Double.MAX_VALUE);
        achievementProgress.setPrefHeight(8);
        achievementProgress.setStyle(
                "-fx-accent: #A78BFA;"
        );

        Label percentage = new Label(
                Math.round(unlockedCount / 4.0 * 100) + "% completed"
        );
        percentage.setStyle(
                "-fx-text-fill: #E0E7FF;"
                        + "-fx-font-size: 12px;"
        );

        banner.getChildren().addAll(
                bannerLabel,
                countLabel,
                encouragement,
                achievementProgress,
                percentage
        );

        // Achievement cards
        Label sectionTitle = sectionHeading("YOUR MILESTONES");

        VBox cards = new VBox(14);
        cards.setMaxWidth(Double.MAX_VALUE);

        cards.getChildren().addAll(
                achievementCard(
                        "🌱",
                        "First Steps",
                        "Complete your first campaign attempt.",
                        firstSteps
                ),
                achievementCard(
                        "📚",
                        "Grammar Champion",
                        "Pass the Beginner campaign level with at least 70%.",
                        grammarChampion
                ),
                achievementCard(
                        "🎯",
                        "Perfect Score",
                        "Answer every question correctly in a campaign attempt.",
                        perfectScore
                ),
                achievementCard(
                        "🏆",
                        "Master in Progress",
                        "Complete all three campaign levels.",
                        masterInProgress
                )
        );

        Button back = secondaryButton("← Back to Dashboard");
        back.setOnAction(e -> showDashboard());

        page.getChildren().addAll(
                title,
                subtitle,
                banner,
                sectionTitle,
                cards,
                back
        );

        showScrollablePage(page);
    }




    private VBox achievementCard(
            String icon,
            String title,
            String description,
            boolean unlocked
    ) {
        // Achievement icon
        Node iconNode = iconGraphic(
                icon,
                30,
                unlocked ? "#FBBF24" : "#9CA3AF"
        );

        // Achievement title
        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.setStyle(
                "-fx-text-fill: " + (unlocked ? "#34D399" : TEXT) + ";"
                        + "-fx-font-size: 15px;"
                        + "-fx-font-weight: bold;"
        );

        // Achievement description
        Label descriptionLabel = mutedText(description);
        descriptionLabel.setWrapText(true);

        // Status indicator
        Label statusLabel = new Label(
                unlocked ? "✓  UNLOCKED" : "🔒  LOCKED"
        );

        statusLabel.setStyle(
                "-fx-text-fill: " + (unlocked ? "#34D399" : "#9CA3AF") + ";"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-color: "
                        + (unlocked ? "#12352F" : "#252D3A") + ";"
                        + "-fx-background-radius: 20px;"
                        + "-fx-padding: 6px 10px;"
        );

        // Icon and status row
        javafx.scene.layout.Region spacer =
                new javafx.scene.layout.Region();

        HBox topRow = new HBox(12, iconNode, spacer, statusLabel);
        topRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Card content
        VBox information = new VBox(
                8,
                titleLabel,
                descriptionLabel
        );

        VBox card = new VBox(
                14,
                topRow,
                information
        );

        card.setPadding(new Insets(20));
        card.setMaxWidth(Double.MAX_VALUE);

        card.setStyle(
                "-fx-background-color: "
                        + (unlocked ? "#1B2638" : "#141D2C") + ";"
                        + "-fx-background-radius: 14px;"
                        + "-fx-border-color: "
                        + (unlocked ? "#315447" : "#293244") + ";"
                        + "-fx-border-radius: 14px;"
                        + "-fx-border-width: 1px;"
        );

        return card;
    }




    private VBox statCard(
            String icon,
            String title,
            String value,
            String accent
    ) {
        Node iconNode = iconGraphic(icon, 26, accent);

        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.setStyle(
                "-fx-text-fill: " + MUTED + ";"
                        + "-fx-font-size: 10px;"
                        + "-fx-font-weight: bold;"
        );

        Label valueLabel = new Label(value);
        valueLabel.setWrapText(true);
        valueLabel.setStyle(
                "-fx-text-fill: " + TEXT + ";"
                        + "-fx-font-size: 18px;"
                        + "-fx-font-weight: bold;"
        );

        VBox card = new VBox(12, iconNode, titleLabel, valueLabel);
        card.setPadding(new Insets(20));
        card.setMinHeight(145);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(
                "-fx-background-color: linear-gradient("
                        + "to bottom right, " + PANEL + ", #111827);"
                        + "-fx-background-radius: 16px;"
                        + "-fx-border-color: #374151;"
                        + "-fx-border-radius: 16px;"
        );

        return card;
    }



    private VBox actionCard(
            String icon,
            String title,
            String description,
            Runnable action
    ) {
        String accent = switch (icon) {
            case "grammar" -> "#C4B5FD";
            case "book", "reading" -> "#93C5FD";
            case "spelling" -> "#F9A8D4";
            case "punctuation" -> "#FCD34D";
            case "sentence" -> "#67E8F9";
            case "synonyms" -> "#86EFAC";
            case "trophy" -> "#FBBF24";
            default -> "#C4B5FD";
        };

        Node iconNode = iconGraphic(icon, 27, accent);

        VBox iconContainer = new VBox(iconNode);
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.setMinSize(48, 48);
        iconContainer.setMaxSize(48, 48);
        iconContainer.setStyle(
                "-fx-background-color: #252044;"
                        + "-fx-background-radius: 12px;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.setStyle(
                "-fx-text-fill: " + TEXT + ";"
                        + "-fx-font-size: 14px;"
                        + "-fx-font-weight: bold;"
        );

        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle(
                "-fx-text-fill: " + MUTED + ";"
                        + "-fx-font-size: 12px;"
                        + "-fx-line-spacing: 3px;"
        );

        Label actionLabel = new Label("Start practising  →");
        actionLabel.setStyle(
                "-fx-text-fill: " + accent + ";"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
        );

        VBox card = new VBox(
                13,
                iconContainer,
                titleLabel,
                descriptionLabel,
                actionLabel
        );

        card.setPadding(new Insets(20));
        card.setMinWidth(200);
        card.setMinHeight(185);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(
                "-fx-background-color: linear-gradient("
                        + "to bottom right, #1F2937, #182235);"
                        + "-fx-background-radius: 15px;"
                        + "-fx-border-color: #374151;"
                        + "-fx-border-radius: 15px;"
                        + "-fx-cursor: hand;"
        );

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: linear-gradient("
                        + "to bottom right, #292343, #1F2937);"
                        + "-fx-background-radius: 15px;"
                        + "-fx-border-color: " + accent + ";"
                        + "-fx-border-radius: 15px;"
                        + "-fx-cursor: hand;"
        ));

        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: linear-gradient("
                        + "to bottom right, #1F2937, #182235);"
                        + "-fx-background-radius: 15px;"
                        + "-fx-border-color: #374151;"
                        + "-fx-border-radius: 15px;"
                        + "-fx-cursor: hand;"
        ));

        card.setOnMouseClicked(e -> action.run());

        return card;
    }


    private VBox pageContainer() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(30));
        page.setStyle("-fx-background-color: " + BACKGROUND + ";");
        return page;
    }



    private void showScrollablePage(VBox page) {
        ScrollPane scrollPane = new ScrollPane();

        scrollPane.setContent(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setPannable(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle(
                "-fx-background: " + BACKGROUND + ";"
                        + "-fx-background-color: " + BACKGROUND + ";"
                        + "-fx-border-color: transparent;"
        );

        page.setFillWidth(true);
        page.setMaxWidth(Double.MAX_VALUE);

        // Important: set the ScrollPane as the center.
        // Do not call showScrollablePage(page) here.
        root.setCenter(scrollPane);
    }



    private Label heading(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                        "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;"
        );
        return label;
    }

    private Label sectionHeading(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;"
        );
        return label;
    }

    private Label mutedText(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle(
                "-fx-text-fill: " + MUTED + ";" +
                        "-fx-font-size: 13px;"
        );
        return label;
    }

    private Button primaryButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: " + PURPLE + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12px 20px;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-cursor: hand;"
        );
        return button;
    }

    private Button secondaryButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: " + PANEL + ";" +
                        "-fx-text-fill: " + TEXT + ";" +
                        "-fx-padding: 10px 16px;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-cursor: hand;"
        );
        return button;
    }

    private void showMessage(
            String title,
            String subtitle,
            String details
    ) {
        VBox page = pageContainer();

        page.getChildren().addAll(
                heading(title),
                mutedText(subtitle),
                mutedText(details)
        );

        Button back = secondaryButton("← Back to Topics");
        back.setOnAction(e -> showPractice());
        page.getChildren().add(back);

        showScrollablePage(page);
    }

    private Label gap(double height) {
        Label spacer = new Label();
        spacer.setMinHeight(height);
        return spacer;
    }

    private void ColumnGrow(GridPane grid, int column) {
        javafx.scene.layout.ColumnConstraints constraints =
                new javafx.scene.layout.ColumnConstraints();

        constraints.setPercentWidth(100.0 / 3);
        constraints.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().add(constraints);
    }

    private static class RegionSpacer extends VBox {
        RegionSpacer() {
            setMinHeight(10);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
