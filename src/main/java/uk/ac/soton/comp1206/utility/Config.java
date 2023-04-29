package uk.ac.soton.comp1206.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.SettingsDialog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

/**
 * This class is used to save and load saved configurations.
 */
public class Config {

  private static final Logger logger = LogManager.getLogger(Config.class);

  /**
   * saves configuration to config.txt
   */
  public static void saveConfiguration() {
    var path = Paths.get("config.txt");
    try {
      Files.writeString(path, SettingsDialog.audioConfig());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * loads configuration from config.txt
   */
  public static void loadCongfiguration() {
    var path = Paths.get("config.txt");
    try {
      var lines = Files.readAllLines(path);
      logger.info("Loading configuration from config.txt");
      logger.info("Music volume: " + lines.get(0));
      logger.info("SFX volume: " + lines.get(1));
      Multimedia.getMusicVolume().set(Double.parseDouble(lines.get(0)));
      Multimedia.getSfxVolume().set(Double.parseDouble(lines.get(1)));
    } catch (NoSuchFileException e) {
      Multimedia.getMusicVolume().set(0.6);
      Multimedia.getSfxVolume().set(0.8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
