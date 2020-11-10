package circlesimage;

import engine.gfx.HexColors;
import engine.gfx.Renderer;
import engine.vectors.points2d.Vec2df;
import engine.gfx.images.Image;

/**
 * This class is a representation of a circle
 * which wants to copy an image
 *
 * This specie will have a highest score
 * if the screen pixels which compose the circle
 * are more similar in color to the pixels of the
 * background image
 * This capacity can be related with the natural
 * mimicry or imitation
 * Some living beings (this feature can be seen
 * on animals and plants) imitate other
 * living beings or their environment because
 * this gives a benefit to them
 *
 * The individual has his own method to calculate
 * the score it has with its own characteristics
 *
 * It has some "genes" which defines the
 * phenotype's characteristics of the individual
 * They are:
 * - The size: the radius of the circle to be drawn on screen
 * - The color: a four channel based color, red, green, blue and alpha
 * - The position: a two dimensional vector which defines the x and y coordinates
 *
 * The features of the individual are inherit with
 * some variations to its children
 *
 * @class CircleImage
 * @author Sergio Martí Torregrosa
 * @date 05/11/2020
 */
public class CircleImage {

    /**
     * The identifier of the object
     */
    private int id;

    /**
     * The color of the circle
     */
    private CircleColor color;

    /**
     * The position where is the circle on screen
     */
    private Vec2df position;

    /**
     * The radius of the circle. In the future,
     * it will be changed for a Vec2df with the width
     * and height of an ellipse, which is more better
     * than a simple circle
     */
    private float size;

    /**
     * The score is how much points
     * has this circle referred to
     * the similarity with the image
     *
     * By default, always zero
     */
    private double score = 0.0;

    /**
     * Constructor
     * @param id the identifier
     * @param position the position of the circle
     * @param size the size of the circle, the radius
     * @param color the color of the circle
     */
    public CircleImage(int id, Vec2df position, float size, CircleColor color) {
        this.id = id;
        this.position = position;
        this.size = size;
        this.color = color;
    }

    /**
     * This method measures the value which have
     * a full line with a background image
     * @param sx start x
     * @param ex end x
     * @param ny the y coordinate
     * @param image the image
     * @return the score of all line which compose the circle
     */
    private double calculateScoreOfLine(int sx, int ex, int ny, Image image) {
        double score = 0.0;
        int count = 1;
        for ( int i = sx; i < ex; i++ ) {
            if ( !(sx < 0 || ex >= image.getW() || ny < 0 || ny >= image.getH()) ) {
                score += color.getSimilarity(new CircleColor(image.getPixel(i, ny)));
                count++;
            }
        }
        return score / (double)count;
    }

    /**
     * This method calculates the similarity of
     * this circle with the background image
     * This is stored inside the size field
     * @param image the background image
     */
    public void calculateScore(Image image) {
        int x0 = 0;
        int y0 = (int)size;
        int d = 3 - 2 * (int)size;
        if ( size == 0 ) {
            score = 0;
        }

        int count = 0;

        while ( y0 >= x0 ) {
            count++;

            score += calculateScoreOfLine((int)(position.getX() - x0), (int)(position.getX() + x0), (int)(position.getY() - y0), image);
            score += calculateScoreOfLine((int)(position.getX() - y0), (int)(position.getX() + y0), (int)(position.getY() - x0), image);
            score += calculateScoreOfLine((int)(position.getX() - x0), (int)(position.getX() + x0), (int)(position.getY() + y0), image);
            score += calculateScoreOfLine((int)(position.getX() - y0), (int)(position.getX() + y0), (int)(position.getY() + x0), image);

            if (d < 0) {
                d += 4 * x0++ + 6;
            } else {
                d += 4 * (x0++ - y0--) + 10;
            }
        }

        score /= (4 * count);
    }

    /**
     * This method draws the circle on screen
     * @param r the renderer object with all drawing methods
     * @param isDrawingBorder if the method has to draw the border of the circle
     * @param isDrawingScore if the method has to draw the score of the circle
     */
    public void drawYourSelf(Renderer r, boolean isDrawingBorder, boolean isDrawingScore) {
        r.drawFillCircle(
                (int)position.getX(),
                (int)position.getY(),
                (int)size,
                color.getCode()
        );

        if ( isDrawingBorder ) {
            r.drawCircle(
                    (int)position.getX(),
                    (int)position.getY(),
                    (int)size,
                    HexColors.WHITE
            );
        }

        if ( isDrawingScore ) {
            r.drawText(
                    String.format("%.2f%%", score * 100),
                    (int)position.getX(),
                    (int)position.getY(),
                    HexColors.WHITE
            );
        }
    }

    /////////////////////////////////////////////////////////////////////////////////

    public int getId() {
        return id;
    }

    public Vec2df getPosition() {
        return position;
    }

    public float getSize() {
        return size;
    }

    public CircleColor getColor() {
        return color;
    }

    public double getScore() {
        return score;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPosition(Vec2df position) {
        this.position = position;
    }

    public void setColor(CircleColor color) {
        this.color = color;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "position " + position + " size " + size + " color " + color;
    }

}
