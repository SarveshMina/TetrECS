package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * Preview of the upcoming and current piece.
 * Uses a 3x3 grid.
 */
public class PieceBoard extends GameBoard {

  /**
   * Constructor of pieceBoard. Uses a 3x3 grid.
   *
   * @param width  width of pieceBoard
   * @param height height of pieceBoard
   */
  public PieceBoard(double width, double height) {
    super(3, 3, width, height);
    build();
  }

  /**
   * Clears the grid and displays the piece.
   *
   * @param gamePiece the piece to display
   */
  public void displayPiece(GamePiece gamePiece) {
    for (var x = 0; x < this.grid.getCols(); x++) {
      for (var y = 0; y < this.grid.getRows(); y++) {
        this.grid.set(x, y, 0);
      }
    }

    this.grid.playPiece(gamePiece, 1, 1);
  }

  /**
   * Displays the middle circle on the PieceBoard.
   */
  public void setMiddleCircle() {
    //the X and Y midpoints of the piece board are determined
    double midX = Math.ceil(rows / 2);
    double midY = Math.ceil(cols / 2);

    //the midpoint of those blocks sets the setCentre method true
    //as it's the centre of the block
    this.blocks[(int) midX][(int) midY].center();
  }
}
