package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * NextPieceListener interface is used to handle next piece after,
 * current piece is placed.
 */
public interface NextPieceListener {
  /**
   * receives a new piece.
   *
   * @param gamePiece the coming piece.
   */
  void nextPiece(GamePiece gamePiece);
}
