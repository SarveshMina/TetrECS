package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.

 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.

 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

  /**
   * The number of columns in this grid.
   */
  private final int cols;

  /**
   * The number of rows in this grid.
   */
  private final int rows;

  /**
   * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
   */
  private final SimpleIntegerProperty[][] grid;

  private static final Logger logger = LogManager.getLogger(Grid.class);

  /**
   * Create a new Grid with the specified number of columns and rows and initialise them.
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public Grid(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;

    //Create the grid itself
    grid = new SimpleIntegerProperty[cols][rows];

    //Add a SimpleIntegerProperty to every block in the grid
    for (var y = 0; y < rows; y++) {
      for (var x = 0; x < cols; x++) {
        grid[x][y] = new SimpleIntegerProperty(0);
      }
    }
  }

  /**
   * takes a GamePiece and position x and y on the grid and checks,
   * if the piece can be played on the grid.
   * Grid return true or false.
   *
   * @param gamePiece piece to be played
   * @param xValue    x position
   * @param yValue    y position
   * @return true if piece can be played.
   */
  public boolean canPlayPiece(GamePiece gamePiece, int xValue, int yValue) {
    int[][] blocks = gamePiece.getBlocks();
    for (var x = 0; x < blocks.length; x++) {
      for (var y = 0; y < blocks[x].length; y++) {
        int val = blocks[x][y];
        if (val != 0) {
          int valueOnGrid = this.get(x + xValue - 1, y + yValue - 1);
          if (valueOnGrid != 0) {
            logger.info("Cannot be played here!");
            return false;
          }
        }
      }
    }
    logger.info("Can be played");
    return true;
  }

  /**
   * takes a GamePiece and position x and y on the grid plays it.
   * If the piece can't be played, it will inform error in logger and a fail sound.
   *
   * @param gamePiece piece to be played
   * @param xValue    x position
   * @param yValue    y position
   */
  public void playPiece(GamePiece gamePiece, int xValue, int yValue) {
    int[][] blocks = gamePiece.getBlocks();
    logger.info("Checking if the piece can be played.");
    if (this.canPlayPiece(gamePiece, xValue, yValue)) {
      for (var x = 0; x < blocks.length; x++) {
        for (var y = 0; y < blocks[x].length; y++) {
          int val = blocks[x][y];
          if (val != 0) {
            logger.info("Piece played");
            this.set(x + xValue - 1, y + yValue - 1, val);
          }
        }
      }
    } else {
      logger.error("Can't play the Piece");
    }
  }

  /**
   * Get the Integer property contained inside the grid at a given row and column index.
   * Can be used for binding.
   *
   * @param x column
   * @param y row
   * @return the IntegerProperty at the given x and y in this grid
   */
  public IntegerProperty getGridProperty(int x, int y) {
    return grid[x][y];
  }

  /**
   * Update the value at the given x and y index within the grid.
   *
   * @param x     column
   * @param y     row
   * @param value the new value
   */
  public void set(int x, int y, int value) {
    grid[x][y].set(value);
  }

  /**
   * Get the value represented at the given x and y index within the grid.
   *
   * @param x column
   * @param y row
   * @return the value
   */
  public int get(int x, int y) {
    try {
      //Get the value held in the property at the x and y index provided
      return grid[x][y].get();
    } catch (ArrayIndexOutOfBoundsException e) {
      //No such index
      return -1;
    }
  }

  /**
   * Get the number of columns in this game.
   *
   * @return number of columns
   */
  public int getCols() {
    return cols;
  }

  /**
   * Get the number of rows in this game.
   *
   * @return number of rows
   */
  public int getRows() {
    return rows;
  }

}
