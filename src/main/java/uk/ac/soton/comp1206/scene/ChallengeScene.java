package uk.ac.soton.comp1206.scene;

import java.util.HashSet;

import javafx.scene.input.KeyEvent;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uk.ac.soton.comp1206.utility.Multimedia;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(MenuScene.class);
  /**
   * HighScore achieved by the player.
   */
  protected IntegerProperty hiiScore = new SimpleIntegerProperty();

  /**
   * x used for keyboard input.
   */
  protected int x = 0;
  /**
   * y used for keyboard input.
   */
  protected int y = 0;

  /**
   * GameBoard
   */
  protected GameBoard board;
  /**
   * Game mode
   */
  protected Game game;
  /**
   * Game timer.
   */
  protected HBox timer;
  /**
   * Countdown timer in bottom box.
   */
  protected Rectangle timerBar;
  /**
   * main Pane of the UI
   */
  protected BorderPane mainPane;
  /**
   * Current piece
   */
  protected PieceBoard currentPiece;
  /**
   * Next piece
   */
  protected PieceBoard comingPiece;

  /**
   * Create a new challenge scene.
   *
   * @param gameWindow the Game Window
   */
  public ChallengeScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Challenge Scene");
  }

  /**
   * Build the Single Player Challenge window.
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    setupGame();

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var challengePane = new StackPane();
    challengePane.setMaxWidth(gameWindow.getWidth());
    challengePane.setMaxHeight(gameWindow.getHeight());
    challengePane.getStyleClass().add("menu-background");
    root.getChildren().add(challengePane);

    mainPane = new BorderPane();
    challengePane.getChildren().add(mainPane);

    board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2);
    mainPane.setCenter(board);

    /* Bottom Countdown */
    timer = new HBox();
    timerBar = new Rectangle();
    timerBar.setHeight(10);
    timer.getChildren().add(timerBar);
    mainPane.setBottom(timer);
    /* Right side */
    statBox();
    /* Top side */
    playerProgressBox();
    /* Left side */
    powerUpBox();

    //Handle block on gameBoard grid being clicked
    board.setOnBlockClick(this::blockClicked);
  }

  private void playerProgressBox() {
    // Current PieceBoard
    currentPiece = new PieceBoard(100, 100);
    currentPiece.setPadding(new Insets(5, 0, 0, 0));
    currentPiece.setMiddleCircle();

    // Next PieceBoard
    comingPiece = new PieceBoard(75, 75);
    comingPiece.setPadding(new Insets(15, 0, 0, 0));

    currentPiece.setOnMouseClicked(mouseEvent -> rotate());
    comingPiece.setOnMouseClicked(mouseEvent -> swap());
    comingPiece.setMiddleCircle();

    // Right Box.
    VBox playerProgressBox = new VBox();
    playerProgressBox.setAlignment(Pos.CENTER);
    playerProgressBox.setPadding(new Insets(0, 15, 0, 0));

    //Level
    var levelLabel = new Text("Level");
    levelLabel.getStyleClass().add("heading");
    var currentLevel = new Text();
    currentLevel.getStyleClass().add("level");
    currentLevel.textProperty().bind(game.levelProperty().asString());

    //High Score and Multiplier
    var hiiScoreLabel = new Text("High Score: ");
    var hiiScore = new Text();
    var multiplierLabel = new Text("Multiplier: ");
    var currentMultiplier = new Text();

    hiiScoreLabel.getStyleClass().add("heading");
    hiiScore.getStyleClass().add("hiscore");
    hiiScore.textProperty().bind(this.hiiScore.asString());

    multiplierLabel.getStyleClass().add("heading");
    currentMultiplier.getStyleClass().add("hiscore");
    currentMultiplier.textProperty().bind(game.multiplierProperty().asString());


    playerProgressBox.getChildren().addAll(levelLabel, currentLevel, hiiScoreLabel, hiiScore, multiplierLabel, currentMultiplier, currentPiece, comingPiece);
    mainPane.setRight(playerProgressBox);
    board.setOnRightClick(this::rotate);
  }

  /**
   * Swap current piece with following Piece.
   */
  protected void swap() {
    game.swapCurrentPiece();
    Multimedia.playAudio("pling.wav");
    currentPiece.displayPiece(game.getCurrentPiece());
    comingPiece.displayPiece(game.getFollowingPiece());
    logger.info("Blocks have been swapped.");
  }

  /**
   * Countdown animation for timer.
   * Update according to gameLoop timer.
   *
   * @param time Time remaining.
   */
  public void updateTimer(int time) {
    KeyValue start = new KeyValue(timerBar.widthProperty(), timer.getWidth());
    KeyValue green = new KeyValue(timerBar.fillProperty(), Color.GREEN);
    KeyValue yellow = new KeyValue(timerBar.fillProperty(), Color.YELLOW);
    KeyValue red = new KeyValue(timerBar.fillProperty(), Color.RED);
    KeyValue finish = new KeyValue(timerBar.widthProperty(), 0);

    Timeline timeline = new Timeline();
    timeline.getKeyFrames().add(new KeyFrame(new Duration(0), start));
    timeline.getKeyFrames().add(new KeyFrame(new Duration(0), green));
    timeline.getKeyFrames().add(new KeyFrame(new Duration((float) time / 2), yellow));
    timeline.getKeyFrames().add(new KeyFrame(new Duration((float) time * 3 / 4), red));
    timeline.getKeyFrames().add(new KeyFrame(new Duration(time), finish));
    timeline.play();

  }

  /**
   * Rotate current Piece to right.
   */
  public void rotate() {
    game.rotateCurrentPiece(1);
    currentPiece.displayPiece(game.getCurrentPiece());
    Multimedia.playAudio("rotate.wav");
    logger.info("Block has been rotated to the right.");
  }

  /**
   * Rotate the current piece to left.
   */
  protected void rotateLeft() {
    game.rotateCurrentPiece(3);
    currentPiece.displayPiece(game.getCurrentPiece());
    Multimedia.playAudio("rotate.wav");
    logger.info("Block has been rotated to the left.");
  }

  /**
   * Get the saved high score and display it on the scene.
   * If player achieves a new high score, save and replace with current high score
   *
   * @param observable   observable
   * @param oldHighScore old recorded high score
   * @param newHighScore new recorded high score
   */
  void getHighScore(ObservableValue<? extends Number> observable, Number oldHighScore, Number newHighScore) {
    logger.info("Updated high score");
    if (newHighScore.intValue() > this.hiiScore.get()) {
      this.hiiScore.set(newHighScore.intValue());
    }
    if (newHighScore.intValue() < this.hiiScore.get()) {
      if (newHighScore.intValue() > ScoresScene.loadScores().get(0).getValue()) {
        this.hiiScore.set(newHighScore.intValue());
      } else {
        this.hiiScore.set(ScoresScene.loadScores().get(0).getValue());
      }
    }
  }

  /**
   * Replaces Current piece with new piece and updates PieceBoards.
   *
   * @param piece piece to be replaced.
   */
  protected void nextPiece(GamePiece piece) {
    currentPiece.displayPiece(piece);
    comingPiece.displayPiece(game.getFollowingPiece());
  }

  private void statBox() {
    HBox statsBar = new HBox(135);
    statsBar.setAlignment(Pos.CENTER);
    BorderPane.setMargin(statsBar, new Insets(10, 0, 0, 0));

    //Score
    VBox scoreBox = new VBox();
    scoreBox.setAlignment(Pos.CENTER);
    var scoreLabel = new Text("Score");
    scoreLabel.getStyleClass().add("heading");
    var score = new Text();
    score.getStyleClass().add("score");
    score.textProperty().bind(game.scoreProperty().asString());
    scoreBox.getChildren().addAll(scoreLabel, score);

    // Lives
    VBox livesBox = new VBox();
    livesBox.setAlignment(Pos.CENTER);
    var livesLabel = new Text("Lives");
    livesLabel.getStyleClass().add("heading");
    var numOfLives = new Text();
    numOfLives.getStyleClass().add("lives");
    numOfLives.textProperty().bind(game.livesProperty().asString());

    // Challenge Title
    var challengeTitle = new Text("TetrECS");
    challengeTitle.getStyleClass().add("bigTitle");

    livesBox.getChildren().addAll(livesLabel, numOfLives);
    statsBar.getChildren().addAll(scoreBox, challengeTitle, livesBox);
    mainPane.setTop(statsBar);
  }

  /**
   * Set up the power up box. it has powerUps in it.
   */
  public void powerUpBox() {
    VBox powerUpBox = new VBox(5);
    powerUpBox.setAlignment(Pos.CENTER);
    powerUpBox.setPadding(new Insets(0, 0, 0, 15));

    var addLife = new Text("Add Life");
    addLife.getStyleClass().add("heading");
    var addLifeNum = new Text("500");

    addLifeNum.getStyleClass().add("menuItem");

    powerUpBox.getChildren().addAll(addLife, addLifeNum);

    mainPane.setLeft(powerUpBox);

    addLifeNum.setOnMouseClicked(event -> {
      game.addLife();
      logger.info("Life added");
    });
  }

  /**
   * Handle when a block is clicked.
   *
   * @param gameBlock the Game Block that was clocked
   */
  void blockClicked(GameBlock gameBlock) {
    game.blockClicked(gameBlock);
  }

  /**
   * Set up the game object and model.
   */
  public void setupGame() {
    logger.info("Starting a new challenge");

    //Start new game
    game = new Game(5, 5);
  }

  /**
   * Fade animation when line is cleared.
   *
   * @param set set of blocks coordinates on grid to be faded.
   */
  protected void fadeLine(HashSet<GameBlockCoordinate> set) {
    logger.info("Line Cleared");
    board.fadeOut(set);
    Multimedia.playAudio("clear.wav");
  }

  /**
   * This method handles the keyboard functions.
   *
   * @param keyEvent the key that was pressed
   */
  public void keyInputs(KeyEvent keyEvent) {
    switch (keyEvent.getCode()) {
      case W, UP -> {
        if (y > 0) {
          y--;
          board.hover(board.getBlock(x, y));
        }
      }
      case A, LEFT -> {
        if (x > 0) {
          x--;
          board.hover(board.getBlock(x, y));
        }
      }
      case S, DOWN -> {
        if (y < game.getRows() - 1) {
          y++;
          board.hover(board.getBlock(x, y));
        }
      }
      case D, RIGHT -> {
        if (x < game.getCols() - 1) {
          x++;
          board.hover(board.getBlock(x, y));
        }
      }
      case Q, Z, OPEN_BRACKET -> rotateLeft();
      case E, C, CLOSE_BRACKET -> rotate();
      case X, ENTER -> blockClicked(board.getBlock(x, y));
      case R, SPACE -> swap();
      case ESCAPE -> {
        Multimedia.playAudio("transition.wav");
        logger.info("Shutting game");
        game.stopTimer();
        gameWindow.startMenu();
      }
    }
  }

  /**
   * Initialise the scene and start the game.
   */
  @Override
  public void initialise() {
    Multimedia.playBackgroundMusic("game.mp3");
    game.setOnLineCleared(this::fadeLine);
    game.setNextPieceListener(this::nextPiece);
    game.setOnGameLoop(this::updateTimer);
    game.scoreProperty().addListener(this::getHighScore);
    scene.setOnKeyPressed(this::keyInputs);
    hiiScore.set(ScoresScene.loadScores().get(0).getValue());
    game.start();
    game.setOnGameOver(() -> {
      game.stopTimer();
      gameWindow.startScoresScene(game);
    });
    logger.info("Initialising Challenge");
  }
}
