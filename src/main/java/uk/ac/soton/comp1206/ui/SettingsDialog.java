package uk.ac.soton.comp1206.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import uk.ac.soton.comp1206.utility.Config;
import uk.ac.soton.comp1206.utility.Multimedia;

/**
 * SettingsDialog class is used to create a dialog box for settings.
 * SettingsDialog class is a Dialog pop up where you can set,
 * settings of the game.
 * User Preferences are saved in the config file.
 */
public class SettingsDialog {
  private static Slider sfxSlider;
  private static Slider musicSlider;

  /**
   * creates a dialog box for settings.
   */
  public static void createDialog() {
    var dialog = new Stage();

    var dialogPane = new StackPane();
    dialogPane.setPadding(new Insets(10, 10, 10, 10));
    dialogPane.setAlignment(Pos.CENTER);
    dialog.setScene(new Scene(dialogPane, 300, 200));
    dialogPane.getStylesheets().add(SettingsDialog.class.getResource("/style/game.css").toExternalForm());
    dialogPane.getStyleClass().add("menu-background");

    dialog.setOnHidden(e -> {
      Config.saveConfiguration();
    });

    var vbox = new VBox();
    vbox.setSpacing(10);
    vbox.setPadding(new Insets(10, 10, 10, 10));
    vbox.setAlignment(Pos.CENTER);
    dialogPane.getChildren().add(vbox);

    var sfxlabel = new Text("SFX VOLUME");
    sfxlabel.getStyleClass().add("heading");
    var musiclabel = new Text("MUSIC VOLUME");
    musiclabel.getStyleClass().add("heading");
    sfxSlider = new Slider(0, 1, 0.8);
    musicSlider = new Slider(0,1,0.6);

    sfxSlider.valueProperty().bindBidirectional(Multimedia.getSfxVolume());
    musicSlider.valueProperty().bindBidirectional(Multimedia.getMusicVolume());

    vbox.getChildren().addAll(sfxlabel, sfxSlider, musiclabel, musicSlider);
    dialog.show();
  }

  /**
   * returns a string of the current configuration
   * @return  string of the current audio configuration
   */
  public static String audioConfig(){
    return musicSlider.getValue() + "\n" + sfxSlider.getValue();
  }
}
