package circlesimage;

import engine.GameContainer;
import engine.gfx.Renderer;
import engine.gfx.images.Image;
import engine.vectors.points2d.Vec2di;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * This class represents a population of CircleImages
 *
 * manages the process of build the population
 * and the collisions between circles
 *
 * @class CircleImagePopulation
 * @author Sergio Martí Torregrosa
 * @date 09/11/2020
 */
public class CircleImagePopulation {

    /**
     * The circles on screen what can have babies
     */
    private ArrayList<CircleImage> circles;

    /**
     * The factory class which builds new circles
     */
    private CircleImageFactory factory;

    /**
     * The maximum and minimum number circles what
     * can have babies
     * X = maximum, by default 500
     * Y = minimum, by default 20
     */
    private Vec2di circlePopulationLimits = new Vec2di();

    /**
     * The penalty proximity for the circles
     * To prevent circles from stalling at one point,
     * there is a proximity penalty
     * By default 0.001
     */
    private double penaltyProximity;

    /**
     * The number of babies what has each circle
     * By default 3
     */
    private int numBabiesByCircle;

    /**
     * Constructor
     */
    public CircleImagePopulation() {
        circles = new ArrayList<>();
        factory = new CircleImageFactory();
    }

    /**
     * This method builds the population
     * It sets the CircleImage ArrayList to a new ArrayList composed
     * by random CircleImages
     * @param gc the game container object with the width and height
     */
    public void buildPopulation(GameContainer gc) {
        circles = factory.buildRandomCircleImageArray(gc, circlePopulationLimits.getY());
    }

    /**
     * This method calculates the score for the circles
     */
    public void calculateCirclesScore(Image background) {
        for ( CircleImage c : circles ) {
            c.calculateScore(background);
            for ( CircleImage t : circles ) {
                if ( c.getId() != t.getId() ) {
                    float distance2 = calculateDistance2(c, t);
                    float sizes = 5 * (c.getSize() + t.getSize());
                    if ( distance2 <=  (sizes * sizes) ) {
                        c.setScore(c.getScore() - penaltyProximity / 2);
                        t.setScore(t.getScore() - penaltyProximity / 2);
                    }
                }
            }
        }
    }

    /**
     * This method kills the worst circles
     */
    public ArrayList<CircleImage> killWorst() {
        ArrayList<CircleImage> diedCircles = new ArrayList<>();
        circles.sort(Comparator.comparingDouble(CircleImage::getScore));
        while ( circles.size() > circlePopulationLimits.getX() ) {
            CircleImage c = circles.remove(0);
            diedCircles.add(c);
        }
        return diedCircles;
    }

    /**
     * This method add the babies of the circles
     * to the circles array
     */
    public void makeNewCircles() {
        ArrayList<CircleImage> circlesBabies = new ArrayList<>();

        for ( CircleImage c : circles ) {
            for (int i = 0; i < numBabiesByCircle; i++ ) {
                CircleImage t = factory.buildBaby(c);
                circlesBabies.add(t);
            }
        }

        circles.addAll(circlesBabies);

        for ( int i = 0; i < circles.size(); i++ ) {
            circles.get(i).setId(i);
        }
    }

    /**
     * This method manages the collision of the circles with the
     * edges
     * @param gc the game container object with the width and height
     */
    public void updateEdgeCollision(GameContainer gc) {
        for ( CircleImage c : circles ) {
            if ( c.getPosition().getX() - c.getSize() < 0 ) {
                c.getPosition().setX(c.getSize());
            }
            if ( c.getPosition().getX() + c.getSize() >= gc.getWidth() ) {
                c.getPosition().setX(gc.getWidth() - c.getSize());
            }
            if ( c.getPosition().getY() - c.getSize() < 0 ) {
                c.getPosition().setY(c.getSize());
            }
            if ( c.getPosition().getY() + c.getSize() >= gc.getHeight() ) {
                c.getPosition().setY(gc.getHeight() - c.getSize());
            }
        }
    }

    /**
     * This method says if the two circles are overlap
     * @param f the first circle
     * @param s the second circle
     * @return if the two circles are overlap
     */
    private boolean doCirclesOverlap(CircleImage f, CircleImage s) {
        return Math.abs(
                (f.getPosition().getX() - s.getPosition().getX()) * (f.getPosition().getX() - s.getPosition().getX()) +
                        (f.getPosition().getY() - s.getPosition().getY()) * (f.getPosition().getY() - s.getPosition().getY())
        )
                <= (f.getSize() + s.getSize()) * (f.getSize() + s.getSize());
    }

    /**
     * This method calculates the square of the distance
     * between to circles
     * @param c the circle
     * @param t the target circle
     * @return the square of the distance between the two circles
     */
    private float calculateDistance2(CircleImage c, CircleImage t) {
        return (c.getPosition().getX() - t.getPosition().getX()) * (c.getPosition().getX() - t.getPosition().getX()) +
                (c.getPosition().getY() - t.getPosition().getY()) * (c.getPosition().getY() - t.getPosition().getY());
    }

    /**
     * This method calculates the distance
     * between to circles
     * @param c the circle
     * @param t the target circle
     * @return the distance between the two circles
     */
    private float calculateDistance(CircleImage c, CircleImage t) {
        return (float)Math.sqrt(calculateDistance2(c, t));
    }

    /**
     * This manages the static collision between the circles specified on the
     * parameters
     * @param c the circle
     * @param t the target circle
     */
    private void updateCircleTargetStaticCollision(CircleImage c, CircleImage t) {
        if ( c.getId() != t.getId() ) {
            if ( doCirclesOverlap(c, t) ) {

                float dist = calculateDistance(c, t);

                if ( dist <= 0 ) {
                    dist = 1;
                }

                float overlap = (dist - c.getSize() - t.getSize());
                float differenceX = c.getPosition().getX() - t.getPosition().getX();
                float differenceY = c.getPosition().getY() - t.getPosition().getY();

                c.getPosition().setX(c.getPosition().getX() - (overlap * differenceX / dist));
                c.getPosition().setY(c.getPosition().getY() - (overlap * differenceY / dist));
                t.getPosition().setX(t.getPosition().getX() + (overlap * differenceX / dist));
                t.getPosition().setY(t.getPosition().getY() + (overlap * differenceY / dist));
            }
        }
    }

    /**
     * This method updates the collisions between all circles
     */
    public void updateStaticCollisions() {
        for ( CircleImage c : circles ) {
            for ( CircleImage t : circles ) {
                updateCircleTargetStaticCollision(c, t);
            }
        }
    }

    /**
     * This method draws the circles
     * @param r the renderer object with all drawing methods
     * @param isDrawingScore if the method has to draw the score of the circles
     */
    public void drawCircles(Renderer r, boolean isDrawingBorder, boolean isDrawingScore) {
        for ( CircleImage c : circles ) {
           c.drawYourSelf(r, isDrawingBorder, isDrawingScore);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ArrayList<CircleImage> getCircles() {
        return circles;
    }

    public CircleImageFactory getFactory() {
        return factory;
    }

    public double getPenaltyProximity() {
        return penaltyProximity;
    }

    public int getNumBabiesByCircle() {
        return numBabiesByCircle;
    }

    public Vec2di getCirclePopulationLimits() {
        return circlePopulationLimits;
    }

    public void setCircles(ArrayList<CircleImage> circles) {
        this.circles = circles;
    }

    public void setFactory(CircleImageFactory factory) {
        this.factory = factory;
    }

    public void setPenaltyProximity(double penaltyProximity) {
        this.penaltyProximity = penaltyProximity;
    }

    public void setNumBabiesByCircle(int numBabiesByCircle) {
        this.numBabiesByCircle = numBabiesByCircle;
    }

    public void setCirclePopulationLimits(Vec2di circlePopulationLimits) {
        this.circlePopulationLimits = circlePopulationLimits;
    }

}
