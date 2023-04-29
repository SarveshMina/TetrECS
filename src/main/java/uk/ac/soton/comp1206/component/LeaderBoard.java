package uk.ac.soton.comp1206.component;

import java.util.ArrayList;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;


/**
 * LeaderBoard class is used to display the scores of the players in the multiplayer game.
 * LeaderBoard class is extended from ScoresList class which is extended from VBox.
 */
public class LeaderBoard extends ScoresList {
  /**
   * holds scores.
   */
  private final SimpleListProperty<Pair<String, Integer>> scores = new SimpleListProperty<Pair<String, Integer>>();
  /**
   * holds userName of the Player.
   */
  private final StringProperty userName = new SimpleStringProperty();
  /**
   * holds defeated players and players who left the game.
   */
  private ArrayList<String> defeatedPlayers = new ArrayList<>();

  /**
   * Constructor for LeaderBoard class.
   */
  public LeaderBoard() {
    //For updating leaderboard when scores array is updated.
    scores.addListener((ListChangeListener<? super Pair<String, Integer>>) e -> updateList());

    //Updates nickName of the User.
    userName.addListener(name -> updateList());

    setAlignment(Pos.CENTER);
    getStyleClass().add("scorelist");
  }

  /**
   * Removes player who have been defeated.
   *
   * @param playerName name of the player who lost/left the game.
   */
  public void removePlayer(String playerName) {
    defeatedPlayers.add(playerName);
  }

  /**
   * updates the score in realTime.
   */
  public void updateList() {
    //Contents cleared from the scoreBoard
    this.getChildren().clear();

    for (Pair<String, Integer> score : scores) {
      HBox scoreBox = new HBox();
      scoreBox.setAlignment(Pos.CENTER);
      scoreBox.getStyleClass().add("scoreitem");
      scoreBox.setSpacing(10);

      Text name = new Text(score.getKey());

      if (userName.get() != null && userName.get().equals(name.getText())) {
        name.getStyleClass().add("leaderboardText");
      } else if (defeatedPlayers.contains(name.getText())) {
        name.getStyleClass().add("deadscore");
      } else {
        name.getStyleClass().add("scorer");
      }

      name.setTextAlignment(TextAlignment.CENTER);
      HBox.setHgrow(name, Priority.ALWAYS);
      scoreBox.getChildren().add(name);

      Text points = new Text(score.getValue().toString());
      if (userName.get() != null && userName.get().equals(name.getText())) {
        points.getStyleClass().add("leaderboardText");
      } else if (defeatedPlayers.contains(name.getText())) {
        points.getStyleClass().add("deadscore");
      } else {
        points.getStyleClass().add("scorer");
      }
      // adding points.
      points.setTextAlignment(TextAlignment.CENTER);
      HBox.setHgrow(points, Priority.ALWAYS);
      scoreBox.getChildren().add(points);

      getChildren().add(scoreBox);
    }
  }

  /**
   * @return scores array.
   */
  public SimpleListProperty<Pair<String, Integer>> scoresProperty() {
    return scores;
  }

  /**
   * @return userName of the Player.
   */
  public StringProperty userNameProperty() {
    return userName;
  }
}

