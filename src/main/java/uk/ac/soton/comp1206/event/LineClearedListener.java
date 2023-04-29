package uk.ac.soton.comp1206.event;

import java.util.HashSet;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * This interface is used to listen for when a line is cleared.
 */
public interface LineClearedListener {
  /**
   * This method is called when a line is cleared.
   *
   * @param lineCleared cleared Line.
   */
  void lineCleared(HashSet<GameBlockCoordinate> lineCleared);
}
