package uk.ac.soton.comp1206.scene;

import javafx.util.Duration;
import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.utility.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.SettingsDialog;

import java.util.Objects;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(MenuScene.class);
  // main Pane.
  private final BorderPane mainPane = new BorderPane();

  /**
   * Create a new menu scene.
   *
   * @param gameWindow the Game Window this will be displayed in.
   */
  public MenuScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Menu Scene");
  }

  /**
   * Build the menu layout.
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var menuPane = new StackPane();
    menuPane.setMaxWidth(gameWindow.getWidth());
    menuPane.setMaxHeight(gameWindow.getHeight());
    menuPane.getStyleClass().add("menu-background");
    root.getChildren().add(menuPane);
    menuPane.getChildren().add(mainPane);

    // TetrECS logo is displayed
    var tetrECS = new ImageView(new Image(getClass().getResource("/images/TetrECS.png").toExternalForm()));
    tetrECS.setFitWidth(gameWindow.getHeight());
    tetrECS.setPreserveRatio(true);
    BorderPane.setAlignment(tetrECS, Pos.CENTER);
    BorderPane.setMargin(tetrECS, new Insets(0, 0, 0, 0));
    mainPane.setCenter(tetrECS);

    // TetrECS logo rotates on the main menu from this transition.
    RotateTransition rotateTransition = new RotateTransition(Duration.millis(3000), tetrECS);
    rotateTransition.setToAngle(6);
    rotateTransition.setFromAngle(-6);
    rotateTransition.setAutoReverse(true);
    rotateTransition.setCycleCount(Animation.INDEFINITE);
    rotateTransition.play();
    // Create menu.
    menuBox();
  }

  /**
   * Make main menu.
   * Single player starts the single player game.
   * Multiplayer starts the multiplayer lobby scene.
   * Instruction starts Instructions scene.
   * Quit exits the game.
   */
  public void menuBox() {
    VBox menuBox = new VBox();
    menuBox.setPadding(new Insets(0, 0, 0, 0));
    menuBox.setPrefWidth(150);
    menuBox.setSpacing(20);
    menuBox.setAlignment(Pos.CENTER);
    menuBox.getStylesheets().add(getClass().getResource("/style/game.css").toExternalForm());

    //setting up single Player, multiplayer, intructions and exit buttons.
    Text singlePlayer = new Text("Single Player");
    Text instructions = new Text("Instructions");
    Text settings = new Text("Settings");
    settings.getStyleClass().add("menuItem");
    Text exit = new Text("Exit");

    singlePlayer.getStyleClass().add("menuItem");
    instructions.getStyleClass().add("menuItem");
    exit.getStyleClass().add("menuItem");

    menuBox.getChildren().addAll(singlePlayer,settings, instructions, exit);
    mainPane.setBottom(menuBox);

    settings.setOnMouseClicked(event -> {
      SettingsDialog.createDialog();
      Multimedia.playAudio("transition.wav");
    });

    // starts single player game.
    singlePlayer.setOnMouseClicked(event -> {
      gameWindow.startChallenge();
      Multimedia.playAudio("transition.wav");
    });

    // starts instructions scene.
    instructions.setOnMouseClicked(event -> {
      gameWindow.startInstructions();
      Multimedia.playAudio("transition.wav");
    });

    // exits the game.
    exit.setOnMouseClicked(event -> {
      App.getInstance().shutdown();
    });
  }

  /**
   * Initialise the menu.
   */
  @Override
  public void initialise() {
    logger.info("Initialising " + this.getClass().getName());
    Multimedia.playBackgroundMusic("menu.mp3");

    // Escape will exit the game
    getScene().setOnKeyPressed(event -> {
      if (Objects.requireNonNull(event.getCode()) == KeyCode.ESCAPE) {
        App.getInstance().shutdown();
      }
    });
  }
}
