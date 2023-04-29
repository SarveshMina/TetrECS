package uk.ac.soton.comp1206.component;

import java.util.ArrayList;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ScoresList class is used to display local and remote scores.
 */
public class ScoresList extends VBox {
  private static final Logger logger = LogManager.getLogger(ScoresList.class);


  /**
   * Scores of the player.
   */
  public final ListProperty<Pair<String, Integer>> scores;

  /**
   * Score achieved by the player.
   */
  private SimpleListProperty scoresListProperty = new SimpleListProperty();

  /**
   * Name of the player.
   */

  public final StringProperty userNameProperty;

  /**
   * List of scores to display.
   */
  private ArrayList<VBox> displayScore = new ArrayList<>();

  /**
   * Constructor for Scores List.
   */
  public ScoresList() {
    getStyleClass().add("scoresList");
    scores = new SimpleListProperty<>();
    scores.addListener((ListChangeListener<? super Pair<String, Integer>>) e -> {
      displayScore.clear();
      getChildren().clear();
      int count = 0;
      for (Pair<String, Integer> score : scores) {
        count++;
        if (count > 10) {
          break;
        }
        //scores Box
        var scoresBox = new VBox();
        scoresBox.setAlignment(Pos.CENTER);

        // high score line (name + score)
        var name = new Text(score.getKey() + ":" + score.getValue());
        name.setFill(GameBlock.COLOURS[count]);
        name.getStyleClass().add("hiscore");
        scoresBox.getChildren().add(name);
        getChildren().add(scoresBox);
        displayScore.add(scoresBox);
        reveal();
      }
    });
    userNameProperty = new SimpleStringProperty();
    logger.info("scoresList created");
  }

  /**
   * Reveals score with a fade animation.
   */
  public void reveal() {
    //list of containing transition from each VBox.
    var transitions = new ArrayList<Transition>();
    logger.info("Revealing scores");
    // Display score with the transition.
    for (var score : displayScore) {
      FadeTransition fade = new FadeTransition(new Duration(150), score);
      fade.setFromValue(0);
      fade.setToValue(1);
      fade.setCycleCount(2);
      transitions.add(fade);
    }
    SequentialTransition seq = new SequentialTransition(transitions.toArray(Animation[]::new));
    seq.play();
    logger.info("Revealing scores");
  }

}
