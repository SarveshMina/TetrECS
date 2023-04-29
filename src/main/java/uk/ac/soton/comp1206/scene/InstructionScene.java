package uk.ac.soton.comp1206.scene;

import java.util.Objects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.utility.Multimedia;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;


/**
 * Instruction Scene class. Extends BaseScene class.
 * Displays the instructions of the game.
 * Contains dynamically generated grid of game pieces.
 */
public class InstructionScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(InstructionScene.class);
  BorderPane mainPane;
  GridPane gridPane = new GridPane();
  private int x = 0;
  private int y;

  /**
   * Create a new scene attached to the specified game window.
   *
   * @param gameWindow the game window.
   */
  public InstructionScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating instructions scene");
  }

  /*
   * Initialises the scene.
   */
  @Override
  public void initialise() {
    Multimedia.playBackgroundMusic("menu.mp3");
    getScene().setOnKeyPressed(event -> {
      if (Objects.requireNonNull(event.getCode()) == KeyCode.ESCAPE) {
        gameWindow.startMenu();
        logger.info("Going back to menu scene.");
        Multimedia.playAudio("transition.wav");
      }
    });
  }

  /**
   * Building instruction scene.
   */
  @Override
  public void build() {
    logger.info("Building Instruction scene.");
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    //Background
    var instructionsPane = new StackPane();
    instructionsPane.getStyleClass().add("menu-background");
    instructionsPane.setMaxHeight(gameWindow.getHeight());
    instructionsPane.setMaxWidth(gameWindow.getWidth());
    root.getChildren().add(instructionsPane);

    //Main Pane
    mainPane = new BorderPane();
    instructionsPane.getChildren().add(mainPane);

    //Top of the scene
    topInstructionsBar();
    //Center of the scene
    centerBox();
  }

  /**
   * Center of the scene. Generates the dynamic grid of game pieces.
   */
  public void centerBox() {
    var centerBox = new VBox();
    centerBox.setAlignment(Pos.CENTER);
    mainPane.setCenter(centerBox);

    // Brief intro of the game
    Text information = new Text("A fast-paced block placement game. You have a 5x5 grid. You must Place in that grid. " +
        "\nYou score by clearing line horizontally or Vertically.");
    information.getStyleClass().add("bodyText");
    information.setTextAlignment(TextAlignment.CENTER);
    centerBox.getChildren().add(information);

    //Displaying image
    var image = new ImageView(new Image(getClass().getResource("/images/Instructions.png").toExternalForm()));
    image.setFitHeight(340);
    image.setPreserveRatio(true);

    var gamePiecesLabel = new Text("Game Pieces");
    gamePiecesLabel.getStyleClass().add("heading");

    var grid = new VBox();
    grid.setAlignment(Pos.CENTER);
    grid.setSpacing(10);

    // Generating game pieces dynamically
    for (int x = 0; x < 3; x++) {
      var row = new HBox();
      grid.getChildren().add(row);
      row.setAlignment(Pos.CENTER);
      row.setSpacing(10);
      for (int y = 0; y < 5; y++) {
        var displayBoard = new PieceBoard(50, 50);
        var piece = GamePiece.createPiece(x * 5 + y);
        displayBoard.displayPiece(piece);
        row.getChildren().add(displayBoard);
      }
    }

    centerBox.getChildren().addAll(image, gamePiecesLabel, grid);
  }

  /**
   * Top the scene containing title.
   */
  public void topInstructionsBar() {
    HBox topBox = new HBox();
    topBox.setAlignment(Pos.CENTER);
    BorderPane.setMargin(topBox, new Insets(10, 0, 0, 0));

    // Title of the scene.
    Text title = new Text("Instructions");
    title.getStyleClass().add("title");
    topBox.getChildren().add(title);

    mainPane.setTop(topBox);

  }
}
