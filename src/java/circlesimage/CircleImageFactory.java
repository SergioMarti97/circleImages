package circlesimage;

import engine.GameContainer;
import engine.vectors.points2d.Vec2df;
import engine.vectors.points2d.Vec2di;

import java.util.ArrayList;

/**
 * This class is a the factory of circles
 * It contains all methods for build a new
 * CircleImage
 * Also, this class contains all parameters
 * for building the CircleImages
 *
 * @class CircleImageFactory
 * @author Sergio Martí Torregrosa
 * @date 09/11/2020
 */
public class CircleImageFactory {

    /**
     * The minimum number of babies what can have a circle,
     * by default 0
     */
    private final int MIN_NUM_BABIES = 0;

    /**
     * The color limits, the maximum value is 255 and the
     * minimum value is 0
     */
    private final Vec2di COLOR_LIMITS = new Vec2di(255, 0);

    /**
     * The minimum size for the size, 1
     */
    private final int MIN_CIRCLE_SIZE = 1;

    /**
     * The maximum limit for the size, 4
     */
    private int maxCircleSize;

    /**
     * The maximum number of babies what can have
     * a circle
     */
    private int maxNumBabies = 3;

    /**
     * The variation for the size
     */
    private Vec2di variationSize;

    /**
     * The variation for the position
     */
    private Vec2di variationPosition;

    /**
     * The variation for the color, for all three channels
     */
    private Vec2di variationColor;

    /**
     * Constructor
     */
    public CircleImageFactory() {
        variationSize = new Vec2di();
        variationPosition = new Vec2di();
        variationColor = new Vec2di();
    }

    /**
     * This method calculates a random integer value
     * between the maximum and minimum values specified
     * on the parameters
     * @param max the maximum value of variation
     * @param min the minimum value of variation
     * @return a random integer value between the max and min
     */
    private int randomIntBetween(int max, int min) {
        return (int)((Math.random() * (max - min)) + min);
    }

    /**
     * This method builds a random circle image
     * @param gc the game container object
     * @return a new instance of random circle image
     */
    private CircleImage buildRandomCircleImage(GameContainer gc) {
        return new CircleImage(
                0,
                new Vec2df(
                        randomIntBetween(gc.getWidth(), 0),
                        randomIntBetween(gc.getHeight(), 0)
                ),
                randomIntBetween(maxCircleSize, MIN_CIRCLE_SIZE),
                new CircleColor(
                        randomIntBetween(COLOR_LIMITS.getX(), COLOR_LIMITS.getY()),
                        randomIntBetween(COLOR_LIMITS.getX(), COLOR_LIMITS.getY()),
                        randomIntBetween(COLOR_LIMITS.getX(), COLOR_LIMITS.getY()),
                        COLOR_LIMITS.getX()
                ),
                randomIntBetween(maxNumBabies, MIN_NUM_BABIES)
        );
    }

    /**
     * This method builds an array full of distinct CircleImages
     * @param gc the game container object
     * @param size the size of the array
     * @return an array of circle images
     */
    public ArrayList<CircleImage> buildRandomCircleImageArray(GameContainer gc, int size) {
        ArrayList<CircleImage> arrayList = new ArrayList<>();
        for ( int i = 0; i < size; i++ ) {
            CircleImage c = buildRandomCircleImage(gc);
            c.setId(arrayList.size());
            arrayList.add(c);
        }
        return arrayList;
    }

    /**
     * This method encapsulates part of the code needed on the
     * CircleImage's getBaby() method. It builds a new color a bit different
     * compared to the instance of the CircleColor which calls the method
     * @param color the parent color
     * @return a new child color what is little different
     */
    public CircleColor buildBabyColor(CircleColor color) {
        int newR = color.getRed() + randomIntBetween(variationColor.getX(), variationColor.getY());
        int newB = color.getBlue() + randomIntBetween(variationColor.getX(), variationColor.getY());
        int newG = color.getGreen() + randomIntBetween(variationColor.getX(), variationColor.getY());

        if ( newR >= COLOR_LIMITS.getX() ) {
            newR = COLOR_LIMITS.getX();
        }
        if ( newG >= COLOR_LIMITS.getX() ) {
            newG = COLOR_LIMITS.getX();
        }
        if ( newB >= COLOR_LIMITS.getX() ) {
            newB = COLOR_LIMITS.getX();
        }

        if ( newR < COLOR_LIMITS.getY() ) {
            newR = COLOR_LIMITS.getY() ;
        }
        if ( newG < COLOR_LIMITS.getY() ) {
            newG = COLOR_LIMITS.getY();
        }
        if ( newB < COLOR_LIMITS.getY() ) {
            newB = COLOR_LIMITS.getY();
        }

        return new CircleColor(newR, newG, newB);
    }

    /**
     * This method returns a new CircleImage with new "genes"
     * inherits from its parent
     * @param parent the CircleImage parent
     * @return a new instance of the CircleImage with some differences with his parent
     */
    public CircleImage buildBaby(CircleImage parent) {
        float size = parent.getSize() + randomIntBetween(variationColor.getX(), variationColor.getY());

        if ( size < MIN_CIRCLE_SIZE) {
            size = MIN_CIRCLE_SIZE;
        }
        if ( size > maxCircleSize) {
            size = maxCircleSize;
        }

        float x = parent.getPosition().getX() + randomIntBetween(variationPosition.getX(), variationPosition.getY());
        float y = parent.getPosition().getY() + randomIntBetween(variationPosition.getX(), variationPosition.getY());

        return new CircleImage(
                0,
                new Vec2df(x, y),
                size,
                buildBabyColor(parent.getColor()),
                randomIntBetween(maxNumBabies, MIN_NUM_BABIES)
        );
    }

    /**
     * This method returns all the babies made from
     * the parent
     * @param parent the parent which is going to have babies
     * @return all babies from the parent
     */
    public ArrayList<CircleImage> buildBabies(CircleImage parent) {
        ArrayList<CircleImage> babies = new ArrayList<>();
        for ( int i = 0; i < parent.getNumOfBabies(); i++ ) {
            babies.add(buildBaby(parent));
        }
        return babies;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public int getMaxCircleSize() {
        return maxCircleSize;
    }

    public Vec2di getVariationSize() {
        return variationSize;
    }

    public Vec2di getVariationPosition() {
        return variationPosition;
    }

    public Vec2di getVariationColor() {
        return variationColor;
    }

    public int getMaxNumBabies() {
        return maxNumBabies;
    }

    public void setMaxCircleSize(int maxCircleSize) {
        this.maxCircleSize = maxCircleSize;
    }

    public void setVariationSize(Vec2di variationSize) {
        this.variationSize = variationSize;
    }

    public void setVariationPosition(Vec2di variationPosition) {
        this.variationPosition = variationPosition;
    }

    public void setVariationColor(Vec2di variationColor) {
        this.variationColor = variationColor;
    }

    public void setMaxNumBabies(int maxNumBabies) {
        this.maxNumBabies = maxNumBabies;
    }

    @Override
    public String toString() {
        return "Max Circle size: " + maxCircleSize + " variation size " + variationSize
                + " variation position " + variationPosition + " variation color " + variationColor;
    }
}
