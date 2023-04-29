package uk.ac.soton.comp1206.utility;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;

/**
 * Multimedia class for playing audio files.
 */
public class Multimedia {

    private static final Logger logger = LogManager.getLogger(GameBlock.class);
    /**
     * For playing sound effects.
     */
    public static MediaPlayer soundPlayer;
    /**
     * For playing background music.
     */
    public static MediaPlayer musicPlayer;
    /**
     * The volume of the background music.
     */
    public static DoubleProperty musicVolume = new SimpleDoubleProperty();
    /**
     * The volume of the sound effects.
     */
    public static DoubleProperty sfxVolume = new SimpleDoubleProperty();

    /**
     * This method lets us play audio files
     *
     * @param fileName the name of the file to play
     */
    public static void playAudio(String fileName) {
        sfxVolume.addListener((observable, oldValue, newValue) -> {
            soundPlayer.setVolume(newValue.doubleValue());
        });

        // opens files from sounds folder only
        String filePath = Multimedia.class.getResource("/sounds/" + fileName).toExternalForm();
        soundPlayer = new MediaPlayer(new Media(filePath));
        soundPlayer.setVolume(sfxVolume.get());
        logger.info("Playing audio file: " + fileName);
        soundPlayer.play();
    }

    /**
     * This method lets us play background music
     *
     * @param fileName the name of the file to play
     */
    public static void playBackgroundMusic(String fileName) {
        if(musicPlayer != null){
            musicPlayer.stop();
        }

        musicVolume.addListener((observable, oldValue, newValue) -> {
            musicPlayer.setVolume(newValue.doubleValue());
        });
        // opens files from music folder only
        String filePath = Multimedia.class.getResource("/music/" + fileName).toExternalForm();
        musicPlayer = new MediaPlayer(new Media(filePath));
        musicPlayer.setVolume(musicVolume.get());
        musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        musicPlayer.play();
        logger.info("Playing audio file: " + fileName);
    }

    /**
     * Getter for the music volume
     * @return this.musicVolume
     */
    public static DoubleProperty getMusicVolume() {
        return musicVolume;
    }

    /**
     * Getter for the sfx volume
     * @return  this.sfxVolume
     */
    public static DoubleProperty getSfxVolume() {
        return sfxVolume;
    }
}
