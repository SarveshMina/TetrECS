package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.utility.Multimedia;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ScoreScene extends BaseScene.
 * It displays scores after game is over.
 * It displays scores according to the challenge mode.
 * It displays scores from local device and online.
 * It also allows user to enter their name and update the local,
 * and online scores.
 * It let's user to go to main menu and try again (try again only in single player mode)
 */
public class ScoresScene extends BaseScene {

  /**
   * The logger for this class
   */
  public static final Logger logger = LogManager.getLogger(ScoresScene.class);
  /**
   * Game.
   */
  protected Game game;

  /**
   * If score should be displayed.
   */
  private final BooleanProperty giveScore = new SimpleBooleanProperty(false);

  /**
   * Remote Scores loading.
   */
  private ObservableList<Pair<String, Integer>> remoteScoresOL;


  /**
   * The name of the player
   */
  private final StringProperty cName = new SimpleStringProperty();
  // local ScoresList
  private ScoresList scoresListComp;
  //left box
  private VBox leftBox;
  //center box
  private VBox topBox;
  //scores on saved local device.
  private ObservableList<Pair<String, Integer>> localScoresOL;
  //To check if new high score is achieved.
  private boolean newLocalScore = false;
  //if the scores should be displayed.
  private boolean giveScores = true;


  /**
   * Create a new score scene attached to the given window
   *
   * @param gameWindow the game window
   * @param game       the game
   */
  public ScoresScene(GameWindow gameWindow, Game game) {
    super(gameWindow);
    this.game = game;
    logger.info("Creating score scene");
  }

  /**
   * Loads local scores from the scores.txt file.
   * if not found creates 8 new scores.
   *
   * @return ArrayList of scores.
   */
  public static ArrayList<Pair<String, Integer>> loadScores() {

    ArrayList<Pair<String, Integer>> scores = new ArrayList<>();
    File scoreFile;
    try {
      scoreFile = new File("scores.txt");
    } catch (Exception e) {
      logger.info("Error loading scores");
      return scores;
    }


    if (!scoreFile.exists()) {
      ArrayList<Pair<String, Integer>> scoreList = new ArrayList<>();
      scoreList.add(new Pair<>("Player", 300));
      scoreList.add(new Pair<>("Player", 250));
      scoreList.add(new Pair<>("Player", 200));
      scoreList.add(new Pair<>("Player", 150));
      scoreList.add(new Pair<>("Player", 100));
      scoreList.add(new Pair<>("Player", 50));
      scoreList.add(new Pair<>("Player", 25));
      scoreList.add(new Pair<>("Player", 10));
      writeScores(scoreList);
    }

    try {
      FileInputStream reader = new FileInputStream(scoreFile);
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(reader));
      try {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
          logger.info("Reading line: " + line);
          String[] scoreArr = line.split(":");
          scores.add(new Pair<>(scoreArr[0], Integer.parseInt(scoreArr[1])));
        }
        bufferedReader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
      logger.info("File not found " + scoreFile.getName());
    }

    return scores;
  }

  /**
   * if new high scores writes scores to the scores.txt file.
   *
   * @param scores ArrayList of scores.
   */
  public static void writeScores(List<Pair<String, Integer>> scores) {
    scores.sort((score1, score2) -> (score2.getValue()).compareTo(score1.getValue()));
    try {
      if (new File("scores.txt").createNewFile()) {
        logger.info("File created: " + "scores.txt");
      }
    } catch (IOException e) {
      logger.error("File wasn't created");
      e.printStackTrace();
    }

    try {
      BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("scores.txt"));
      int scoresNum = 0;
      for (Pair<String, Integer> score : scores) {
        bufferedWriter.write(score.getKey() + ":" + score.getValue() + "\n");
        scoresNum++;
        if (scoresNum > 8) {
          break;
        }
      }
      bufferedWriter.close();
      logger.info("Scores written to file");
    } catch (IOException e) {
      e.printStackTrace();
      logger.error("Error: Cannot save score to file");
    }
  }

  /**
   * Displays scores.
   */
  public void displayScores() {
    if (giveScores) {
      newHighScore();
      giveScores = false;
      return;
    }
    giveScore.set(true);
    scoresListComp.reveal();
  }

  /**
   * UI to display the score.
   */
  public void newHighScore() {
    if (!game.scores.isEmpty()) {
      giveScore.set(true);
      scoresListComp.reveal();
      logger.info("No new score");
      return;
    }

    // score of the player
    var currentScore = game.scoreProperty().get();
    var scoreNum = 0;
    var finalScoreNum = scoreNum;
    // to enter name.
    var textField = new TextField();
    // lowest score stored.
    var lowestLocalStoredScore = this.localScoresOL.get(localScoresOL.size() - 1).getValue();
    textField.setMaxWidth(200);
    textField.setPromptText("Enter your name");

    // Interface to run when new high score is achieved.
    highScoreInterface highScoreInterface = () -> {
      cName.set(textField.getText().replace(":", ""));
      // clear top box.
      topBox.getChildren().remove(1);
      topBox.getChildren().remove(1);

      // set image to the top of the scene.
      var tetrECS = new ImageView(new Image(getClass().getResource("/images/TetrECS.png").toExternalForm()));
      tetrECS.setFitWidth(gameWindow.getHeight() / 2);
      tetrECS.setPreserveRatio(true);
      BorderPane.setAlignment(tetrECS, Pos.CENTER);
      BorderPane.setMargin(tetrECS, new Insets(0, 0, 0, 0));
      topBox.getChildren().add(tetrECS);

      // if new high score is achieved add to list.
      if (newLocalScore) {
        this.localScoresOL.add(finalScoreNum, new Pair<>(textField.getText().replace(":", ""), currentScore));
      }

      writeScores(this.localScoresOL);
      Platform.runLater(this::displayScores);
      newLocalScore = false;
      Multimedia.playAudio("explode.wav");
    };

    // if new high score is achieved.
    if (currentScore > lowestLocalStoredScore) {
      for (Pair<String, Integer> score : localScoresOL) {
        if (currentScore > score.getValue()) {
          newLocalScore = true;
        }
        scoreNum++;
      }
    }

    // if new high score is achieved locally or on server.
    if (newLocalScore) {
      var newHiiScore = new Text("New Score added");
      newHiiScore.getStyleClass().add("title");
      topBox.getChildren().add(newHiiScore);

      textField.setOnKeyPressed(keyEvent -> {
        if (keyEvent.getCode() == KeyCode.ENTER) {
          highScoreInterface.run();
        }
      });
      textField.setAlignment(Pos.CENTER);
      textField.requestFocus();
      topBox.getChildren().add(textField);

      // Confirmation
      var save = new Text("Confirm");
      save.getStyleClass().add("menuItem");
      save.setOnMouseClicked(mouseEvent -> highScoreInterface.run());
      topBox.getChildren().add(save);
    } else {
      var tetrECS = new ImageView(new Image(getClass().getResource("/images/TetrECS.png").toExternalForm()));
      tetrECS.setFitWidth(gameWindow.getHeight() / 2.0);
      tetrECS.setPreserveRatio(true);
      BorderPane.setAlignment(tetrECS, Pos.CENTER);
      BorderPane.setMargin(tetrECS, new Insets(0, 0, 0, 0));
      topBox.getChildren().add(tetrECS);

      Multimedia.playAudio("lifelose.wav");
      logger.info("No new High score achieved");
      giveScore.set(true);
      scoresListComp.reveal();
    }
  }

  /**
   * Initialising scores scene.
   */
  @Override
  public void initialise() {
    logger.info("Initialising score scene");
    Multimedia.playBackgroundMusic("end.wav");

    // display score.
    Platform.runLater(this::displayScores);
    scene.setOnKeyPressed(even -> {
      if (even.getCode() == (KeyCode.ESCAPE)) {
        Multimedia.playAudio("transition.wav");
        gameWindow.startMenu();
      }
    });
  }

  /**
   * Building scores scene.
   */
  @Override
  public void build() {
    logger.info("Building score scene");
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var scoreDisplayPane = new StackPane();
    scoreDisplayPane.getStyleClass().add("challenge-background");
    scoreDisplayPane.setMaxHeight(gameWindow.getHeight());
    scoreDisplayPane.setMaxWidth(gameWindow.getWidth());
    root.getChildren().add(scoreDisplayPane);

    var mainPane = new BorderPane();
    scoreDisplayPane.getChildren().add(mainPane);

    leftBox = new VBox();
    topBox = new VBox();

    Text localHighScoreText;
    localHighScoreText = new Text("Local Scores");

    var localScorePane = new GridPane();

    topBox.setAlignment(Pos.CENTER);
    leftBox.setAlignment(Pos.CENTER);

    leftBox.setSpacing(10);
    topBox.setSpacing(10);

    //Bottom of the Scene.
    var bottomBox = new HBox(80);
    bottomBox.setAlignment(Pos.CENTER);
    BorderPane.setMargin(bottomBox, new Insets(0, 0, 20, 0));
    mainPane.setBottom(bottomBox);

    localScorePane.setAlignment(Pos.CENTER);

    localScorePane.vgapProperty().bind(new SimpleDoubleProperty(10));

    localHighScoreText.getStyleClass().add("title");
    localHighScoreText.visibleProperty().bind(giveScore);

    scoresListComp = new ScoresList();
    scoresListComp.setAlignment(Pos.CENTER);

    // game.getScores().size() == 0 means that game is played on single player mode.
    // the scene is adjusted accordingly.
    // displays online scores and local scores and adds try againb button.
    mainPane.setCenter(leftBox);
    mainPane.setTop(topBox);
    localScoresOL = FXCollections.observableArrayList(loadScores());
    localScoresOL.sort((scoreA, scoreB) -> (scoreB.getValue()).compareTo(scoreA.getValue()));

    SimpleListProperty<Pair<String, Integer>> scoresList = new SimpleListProperty<>(localScoresOL);
    SimpleListProperty<Pair<String, Integer>> remoteScoresList = new SimpleListProperty<>(remoteScoresOL);

    scoresListComp.userNameProperty.bind(cName);
    scoresListComp.scores.bind(scoresList);

    localScorePane.getChildren().add(scoresListComp);
    leftBox.getChildren().addAll(localHighScoreText, localScorePane);
    var tryAgain = new Text("Try Again");
    tryAgain.getStyleClass().add("menuItem");
    tryAgain.setOnMouseClicked(mouseEvent -> {
      Multimedia.playAudio("transition.wav");
      gameWindow.startChallenge();
    });

    bottomBox.getChildren().add(tryAgain);
    // Going back to main menu button.
    var goBack = new Text("Main Menu");
    goBack.getStyleClass().add("menuItem");

    goBack.setOnMouseClicked(mouseEvent -> {
      Multimedia.playAudio("transition.wav");
      gameWindow.startMenu();
    });
    bottomBox.setSpacing(40);
    bottomBox.getChildren().add(goBack);
  }

  /**
   * interface to check score.
   */
  interface highScoreInterface {
    void run();
  }
}
