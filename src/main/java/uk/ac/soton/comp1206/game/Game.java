package uk.ac.soton.comp1206.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.utility.Multimedia;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.GameOverListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state,
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {
  /**
   * Logger for the Game class.
   */

  private static final Logger logger = LogManager.getLogger(Game.class);

  /**
   * Game Over Listener.
   */
  private GameOverListener gameOverListener = null;

  /**
   * Game Loop Listener.
   */
  private GameLoopListener gameLoopListener = null;

  /**
   * Next Piece Listener
   */
  private NextPieceListener nextPieceListener = null;

  /**
   * Line Cleared Listener
   */

  private LineClearedListener lineClearedListener = null;

  /**
   * The piece currently being controlled by the player
   */

  public GamePiece currentPiece;

  /**
   * The piece following the currentPiece;
   */

  public GamePiece followingPiece;

  /**
   * Number of rows.
   */
  protected final int rows;
  /**
   * Number of columns.
   */
  protected final int cols;

  /**
   * The grid model linked to the game.
   */
  protected final Grid grid;

  // The game loop.
  private ScheduledFuture<?> loop;

  // Calls gameLoop methods.
  private ScheduledExecutorService timer;

  /**
   * Game Scores
   */
  public ArrayList<Pair<String, Integer>> scores = new ArrayList<>();

  /**
   * Integer Property for Score. Default value is 0.
   */
  private IntegerProperty score = new SimpleIntegerProperty(0);
  /**
   * Integer Property for Level. Default value is 0.
   */
  private IntegerProperty level = new SimpleIntegerProperty(0);
  /**
   * Integer Property for Lives. Default value is 3.
   */
  private IntegerProperty lives = new SimpleIntegerProperty(3);
  /**
   * Integer Property for Multiplier. Default value is 1.
   */
  private IntegerProperty multiplier = new SimpleIntegerProperty(1);


  /**
   * Create a new game with the specified rows and columns. Creates a corresponding grid model.
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public Game(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;

    //Create a new grid model to represent the game state
    this.grid = new Grid(cols, rows);
    timer = Executors.newSingleThreadScheduledExecutor();
  }

  /**
   * This method starts the game.
   */
  public void start() {
    logger.info("Starting game");
    initialiseGame();
    // Setting up the loop
    loop = timer.schedule(this::gameLoop, getTimerDelay(), TimeUnit.MILLISECONDS);
    gameLoopListener();
  }

  /**
   * Events that will take place when the game loop ends.
   */
  public void gameLoop() {
    logger.info("In GameLoop");
    updateLives();
    //Reseting the Multiplier
    if (multiplier.get() > 1) {
      logger.info("Resat the Multiplier");
      multiplier.set(1);
    }
    nextPiece();
    gameLoopListener();
    loop = timer.schedule(this::gameLoop, getTimerDelay(), TimeUnit.MILLISECONDS);
  }

  /**
   * Initialise a new game and set up anything that needs to be done at the start.
   */
  public void initialiseGame() {
    logger.info("Initialising game");
    this.followingPiece = spawnPiece();
    nextPiece();
  }

  /**
   * Handle what should happen when a particular block is clicked.
   *
   * @param gameBlock the block that was clicked
   */
  public void blockClicked(GameBlock gameBlock) {
    //Get the position of this block
    int x = gameBlock.getX();
    int y = gameBlock.getY();

    // Updates the grid after checking if the piece can be played or not.
    if (grid.canPlayPiece(currentPiece, x, y)) {
      Multimedia.playAudio("place.wav");
      // Placing the GamePiece on the grid.
      grid.playPiece(currentPiece, x, y);
      // Calling nextPiece method to spawn a new piece.
      afterPiece();
      nextPiece();
      loop.cancel(false);
      loop = timer.schedule(this::gameLoop, getTimerDelay(), TimeUnit.MILLISECONDS);
      gameLoopListener();
      logger.info("GameLoop reset.");
    } else {
      Multimedia.playAudio("fail.wav");
    }
  }

  /**
   * stops the game loop.
   */
  public void stopTimer() {
    logger.info("Stopping the timer");
    // stopping the timer.
    timer.shutdownNow();
  }

  /**
   * Create a random Piece with a random rotation.
   *
   * @return new GamePiece
   */
  public GamePiece spawnPiece() {
    Random rndm = new Random();     // for random piece.
    Random rndmTransition = new Random();   // for random transition of the piece.
    return GamePiece.createPiece(rndm.nextInt(15), rndmTransition.nextInt(3));
  }

  /**
   * change current piece with the following piece and generate a new Piece.
   */
  public void nextPiece() {
    // Assigning current piece to comingPiece
    currentPiece = followingPiece;
    // Creating a new piece and assigning it to the comingPiece.
    followingPiece = spawnPiece();

    if (nextPieceListener != null) {
      nextPieceListener.nextPiece(currentPiece);
    }

    logger.info("current piece is now: " + currentPiece);
  }

  /**
   * Swap the current piece with the following piece.
   */
  public void swapCurrentPiece() {
    GamePiece newGamePiece = currentPiece;
    currentPiece = followingPiece;
    followingPiece = newGamePiece;
    logger.info("current piece is now next piece");
  }

  /**
   * Rotate the current piece.
   *
   * @param x is number of times for rotation.
   */
  public void rotateCurrentPiece(int x) {
    // Rotating the current piece.
    currentPiece.rotate(x);
  }

  /**
   * This method is called when a piece is played.
   * This method handles clearing of lines.
   */
  public void afterPiece() {
    var clearedLines = new HashSet<GameBlockCoordinate>();
    var clear = new HashSet<IntegerProperty>();
    var linesCleared = 0;

    //Checking for Horizontal lines (Rows).
    for (var x = 0; x < grid.getCols(); x++) {
      int count = 0;
      for (var y = 0; y < grid.getRows(); y++) {
        if (grid.get(x, y) != 0) {
          count++;
        }
      }

      if (count == grid.getRows()) {
        linesCleared++;
        for (var y = 0; y < grid.getRows(); y++) {
          clear.add(grid.getGridProperty(x, y));
          clearedLines.add(new GameBlockCoordinate(x, y));
        }
      }
    }

    //Checking for Vertical lines (Columns).
    for (var y = 0; y < grid.getRows(); y++) {
      int count = 0;
      for (var x = 0; x < grid.getCols(); x++) {
        if (grid.get(x, y) == 0) {
          break;
        }
        count++;
      }
      if (count == grid.getCols()) {
        linesCleared++;
        for (var x = 0; x < grid.getCols(); x++) {
          clear.add(grid.getGridProperty(x, y));
          clearedLines.add(new GameBlockCoordinate(x, y));
        }
      }
    }

    //Check for if lines are cleared.
    if (linesCleared > 0) {
      // Update the score.
      score(linesCleared, clear.size());
      // Update the multiplier.
      multiplier.set(multiplier.add(1).get());
      // Update the level.
      level.set(Math.floorDiv(score.get(), 1000));
      Multimedia.playAudio("level.wav");
      if (lineClearedListener != null) {
        lineClearedListener.lineCleared(clearedLines);
      }
      //clearing blocks
      for (IntegerProperty block : clear) {
        block.set(0);
      }
    } else {
      // Reset the multiplier.
      if (multiplier.get() > 1) {
        logger.info("Resat the Multiplier");
        multiplier.set(1);
      }
    }
  }

  /**
   * Calculation for time in each round.
   *
   * @return time for each round
   */
  public int getTimerDelay() {
    int max = 12000 - (500 * level.get());
    int max2 = 2500;
    return Math.max(max, max2);
  }

  /**
   * Calculates score.
   *
   * @param numberOfLines  number of lines cleared
   * @param numberOfBlocks number of blocks cleared
   */
  public void score(int numberOfLines, int numberOfBlocks) {
    // Get the multiplier.
    int multiplier = this.multiplier.get();
    //Check for the number of lines cleared.
    scoreProperty().set(score.add(numberOfLines * numberOfBlocks * multiplier * 10).get());
  }

  /**
   * Adds a life to the player.
   */
  public void addLife(){
    if(score.get() >= 500){
      lives.set(lives.get() + 1);
      score.set(score.get() - 500);
      Multimedia.playAudio("lifegain.wav");
      logger.info("Life added. Lives updated to: " + lives.get());
    } else {
      Multimedia.playAudio("fail.wav");
      logger.info("Not enough points to add life. Score: " + score.get());
    }

  }

  /**
   * Removes life after every game loop and checks if the game is over.
   */
  public void updateLives() {
    logger.info("Into livesReset");
    if (lives.get() > 0) {
      lives.set(lives.get() - 1);
      logger.info("Lives: " + lives.get());
      Multimedia.playAudio("lifelose.wav");
    } else {
      logger.info("Game Over");
      Multimedia.playBackgroundMusic("end.wav");
      Platform.runLater(() -> gameOverListener.gameOver());
    }
  }

  /**
   * Listens for next Piece.
   *
   * @param nextPieceListener nextPieceListener
   */
  public void setNextPieceListener(NextPieceListener nextPieceListener) {
    this.nextPieceListener = nextPieceListener;
  }

  /**
   * Listens for when line is cleared.
   *
   * @param lineClearedListener lineClearedListener
   */
  public void setOnLineCleared(LineClearedListener lineClearedListener) {
    this.lineClearedListener = lineClearedListener;
  }

  /**
   * Handles events after the countdown is finished.
   *
   * @param gameLoopListener listens for gameloop timer to end
   */
  public void setOnGameLoop(GameLoopListener gameLoopListener) {
    this.gameLoopListener = gameLoopListener;
  }

  /**
   * Listens for the game loop countdown.
   */
  public void gameLoopListener() {
    if (gameLoopListener != null) {
      gameLoopListener.gameLoop(getTimerDelay());
    }
  }

  /**
   * Handles events when game is finished.
   *
   * @param gameOverListener listens for game over event
   */
  public void setOnGameOver(GameOverListener gameOverListener) {
    this.gameOverListener = gameOverListener;
  }

  /**
   * Get the current piece being played.
   *
   * @return currentPiece
   */
  public GamePiece getCurrentPiece() {
    return currentPiece;
  }

  /**
   * Get the next piece to be played.
   *
   * @return follwingPiece
   */
  public GamePiece getFollowingPiece() {
    return followingPiece;
  }

  /**
   * Get the grid model inside this game representing the game state of the board.
   *
   * @return game grid model
   */
  public Grid getGrid() {
    return grid;
  }

  /**
   * IntegerProperty to manage the score.
   *
   * @return scoreProperty
   */
  public IntegerProperty scoreProperty() {
    return score;
  }

  /**
   * IntegerProperty to manage levels.
   *
   * @return levelProperty
   */
  public IntegerProperty levelProperty() {
    return level;
  }

  /**
   * IntegerProperty to manage lives.
   *
   * @return livesProperty
   */
  public IntegerProperty livesProperty() {
    return lives;
  }

  /**
   * IntegerProperty to manage the Multiplier.
   *
   * @return multiplierProperty
   */
  public IntegerProperty multiplierProperty() {
    return multiplier;
  }

  /**
   * Get the number of columns in this game.
   *
   * @return number of columns
   */
  public int getCols() {
    return cols;
  }

  /**
   * Get the current scores.
   *
   * @return scores
   */
  public ArrayList<Pair<String, Integer>> getScores() {
    return scores;
  }


  /**
   * Get the number of rows in this game.
   *
   * @return number of rows
   */
  public int getRows() {
    return rows;
  }
}
