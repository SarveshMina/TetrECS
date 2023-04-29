package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Visual User Interface component representing a single block in the grid.
 * Extends Canvas and is responsible for drawing itself.
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {
  //Logger for this class
  private static final Logger logger = LogManager.getLogger(GameBlock.class);

  /**
   * The set of colours for different pieces.
   */
  public static final Color[] COLOURS = {
      Color.TRANSPARENT,
      Color.DEEPPINK,
      Color.RED,
      Color.ORANGE,
      Color.YELLOW,
      Color.YELLOWGREEN,
      Color.LIME,
      Color.GREEN,
      Color.DARKGREEN,
      Color.DARKTURQUOISE,
      Color.DEEPSKYBLUE,
      Color.AQUA,
      Color.AQUAMARINE,
      Color.BLUE,
      Color.MEDIUMPURPLE,
      Color.PURPLE
  };


  private final double width;
  private final double height;

  /**
   * The column this block exists as in the grid.
   */
  private final int x;

  /**
   * The row this block exists as in the grid.
   */
  private final int y;

  /**
   * The value of this block (0 = empty, otherwise specifies the colour to render as).
   */
  private final IntegerProperty value = new SimpleIntegerProperty(0);

  /**
   * Center of piece.
   */
  private boolean centerOfPiece = false;

  /**
   * Hovering over block.
   */
  private boolean hover = false;

  /**
   * Create a new single Game Block.
   *
   * @param x      the column the block exists in.
   * @param y      the row the block exists in.
   * @param width  the width of the canvas to render.
   * @param height the height of the canvas to render.
   */
  public GameBlock(int x, int y, double width, double height) {
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;

    //initial width and height of canvas.
    setWidth(width);
    setHeight(height);

    //initial paint
    paint();

    //Call the internal updateValue function after updating changes to the value property.
    value.addListener(this::updateValue);
  }

  /**
   * When block value is updated.
   *
   * @param observable what was updated.
   * @param oldValue   old value.
   * @param newValue   new value.
   */
  private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
    paint();
  }

  /**
   * painting of block canvas.
   */
  public void paint() {
    //Paint the block empty if it's empty.
    if (value.get() == 0) {
      paintEmpty();
    } else {
      //Paint the block with the colour specified by the value if it is not empty.
      paintColor(COLOURS[value.get()]);
    }

    // if centerOfPiece is true, then paint center of piece
    if (this.centerOfPiece) {
      var graphicsContext = getGraphicsContext2D();

      graphicsContext.setFill(Color.color(1,1,1,0.6));
      graphicsContext.fillOval(width / 4, height / 4, width / 2, height / 2);
    }
    // If hover is true, then paint hover
    if (this.hover) {
      var gc = getGraphicsContext2D();

      gc.setFill(Color.rgb(204, 204, 204, 0.4));
      gc.fillRect(0, 0, width, height);
    }
  }

  /**
   * Paint for empty canvas.
   */
  private void paintEmpty() {
    var graphicsContext = getGraphicsContext2D();

    //Clear Canvas
    graphicsContext.clearRect(0, 0, width, height);

    //Fill Canvas
    graphicsContext.setFill(Color.rgb(0, 0, 0, 0.3));
    graphicsContext.fillRect(0, 0, width, height);

    //Border Canvas
    graphicsContext.setStroke(Color.WHITE);
    graphicsContext.strokeRect(0, 0, width, height);
  }

  /**
   * Paint canvas with the provided colour.
   *
   * @param colour colour to paint.
   */
  private void paintColor(Paint colour) {
    var graphicsContext = getGraphicsContext2D();

    // Clear
    graphicsContext.clearRect(0, 0, width, height);

    // Color fill
    graphicsContext.setFill(colour);
    graphicsContext.fillRect(0, 0, width, height);

    // Creates 3D effect on piece
    graphicsContext.setFill(Color.rgb(59, 59, 59, 0.2));
    graphicsContext.fillPolygon(new double[]{0, 0, width}, new double[]{0, height, height}, 3);
    graphicsContext.setFill(Color.rgb(161, 161, 161, 0.3));
    graphicsContext.fillRect(0, 0, 3, height);
    graphicsContext.setFill(Color.rgb(255, 255, 255, 0.3));
    graphicsContext.fillRect(0, 0, width, 3);

    //Border
    graphicsContext.setStroke(Color.rgb(0, 0, 0, 0.6));
    graphicsContext.strokeRect(0, 0, width, height);
  }

  /**
   * Paints circle center of the piece.
   */
  public void center() {
    this.centerOfPiece = true;
  }

  /**
   * To set the hover effect.
   *
   * @param hover true if hovering over block
   */
  public void hover(boolean hover) {
    this.hover = hover;
    paint();
  }

  /**
   * Fade line when it is cleared.
   */
  public void fadeOut() {
    var animeTimer = new AnimationTimer() {
      double opacity = 1;

      @Override
      public void handle(long l) {
        GameBlock.this.paintEmpty();
        opacity = opacity - 0.02;
        if (opacity <= 0) {
          stop();
          return;
        }
        var graphics = getGraphicsContext2D();
        graphics.setFill(Color.rgb(0, 1, 0, opacity));
        graphics.fillRect(0, 0, GameBlock.this.width, GameBlock.this.height);
      }
    };
    animeTimer.start();
  }

  /**
   * Get the column of this block.
   *
   * @return column number
   */
  public int getX() {
    return x;
  }

  /**
   * Get the row of this block.
   *
   * @return row number
   */
  public int getY() {
    return y;
  }

  /**
   * Get the current value held by this block, representing it's colour.
   *
   * @return value
   */
  public int getValue() {
    return this.value.get();
  }

  /**
   * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
   *
   * @param value property to bind the value to
   */
  public void bind(ObservableValue<? extends Number> value) {
    this.value.bind(value);
  }

}
