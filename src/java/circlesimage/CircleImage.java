package circlesimage;

import engine.vectors.points2d.Vec2df;
import engine.gfx.images.Image;

/**
 * This class is a representation of a Circle
 * which wants to copy an image
 *
 * @class CircleImage
 * @author Sergio Martí Torregrosa
 * @date 05/11/2020
 */
public class CircleImage {

    /**
     * The minimum size of the circle
     */
    private final int MIN_SIZE = 1;

    /**
     * The maximum size of the circle
     */
    private final int MAX_SIZE = 4;

    /**
     * The identifier of the object
     */
    private int id;

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
     * The color of the circle
     */
    private CircleColor color;

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
     * This method gives a random integer value between the specified
     * interval
     * This auxiliary method is needed fot reduce the code complexity
     * @param max the maximum value
     * @param min the minimum value
     * @return a random integer between the max and the min
     */
    private int randomIntBetween(int max, int min) {
        return (int)((Math.random() * (max - min)) + min);
    }

    /**
     * This method returns a new CircleImage with new "genes"
     * inherits from its parent
     * @return a new instance of the CircleImage with some differences with his parent
     */
    public CircleImage getBaby() {
        float size = this.size + randomIntBetween(2, -2);

        if ( size < MIN_SIZE ) {
            size = MIN_SIZE;
        }
        if ( size > MAX_SIZE ) {
            size = MAX_SIZE;
        }

        float x = position.getX() + randomIntBetween(10, -10);
        float y = position.getY() + randomIntBetween(10, -10);

        return new CircleImage(
                0,
                new Vec2df(x, y),
                size,
                color.getBabyColor(10, -10)
        );
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
