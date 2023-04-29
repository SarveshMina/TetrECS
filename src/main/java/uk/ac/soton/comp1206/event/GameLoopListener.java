package uk.ac.soton.comp1206.event;

/**
 * links timer in the game to the timer in the UI.
 */
public interface GameLoopListener {
  /**
   * Handle when countdown ends.
   *
   * @param x clicked block
   */
  void gameLoop(int x);
}
