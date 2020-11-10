package circlesimage;

import engine.GameContainer;
import engine.gfx.Renderer;
import engine.gfx.images.Image;
import engine.vectors.points2d.Vec2di;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Predicate;

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
     * The decrease of alpha channel each time
     */
    private final int ALPHA_DECREASE = 5;

    /**
     * The circles on screen what can have babies
     */
    private ArrayList<CircleImage> circles;

    /**
     * The factory class which builds new circles
     */
    private CircleImageFactory factory;

    /**
     * This is a counter for live and dead
     * circles
     */
    private Vec2di liveDeadCount = new Vec2di();

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
     * This field specifies how often the
     * circles have babies
     */
    private float updateCapMakeNewBabies;

    /**
     * A counter of time, needed because
     * the circles have babies in a specified
     * intervals of time
     */
    private float time = 0;

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
        setIdToCircles();
    }

    /**
     * This method sets the id to all circles
     */
    public void setIdToCircles() {
        for ( int i = 0; i < circles.size(); i++ ) {
            circles.get(i).setId(i);
        }
    }

    /**
     * This method calculates the score for the circle passed
     * by parameter
     * @param c the instance of CircleImage
     * @param background the image of the background
     */
    private void calculateCircleScore(CircleImage c, Image background) {
        c.calculateScore(background);
        for ( CircleImage t : circles ) {
            if ( !t.isDead() ) {
                if ( c.getId() != t.getId() ) {
                    float distance2 = calculateDistance2(c, t);
                    float sizes = 5 * (c.getSize() + t.getSize());
                    if ( distance2 <= (sizes * sizes) ) {
                        c.setScore(c.getScore() - penaltyProximity / 2);
                        t.setScore(t.getScore() - penaltyProximity / 2);
                    }
                }
            }
        }
    }

    /**
     * This method calculates the score for the circles
     * @param background the image of the background
     */
    public void calculateCirclesScore(Image background) {
        for ( CircleImage c : circles ) {
            if ( !c.isDead() ) {
                calculateCircleScore(c, background);
            }
        }
    }

    /**
     * This method sorts the population by the score of
     * the individuals
     */
    public void sort() {
        ArrayList<CircleImage> circlesAlive = new ArrayList<>();
        for ( CircleImage c : circles ) {
            if ( !c.isDead() ) {
                circlesAlive.add(c);
            }
        }
        circles.removeAll(circlesAlive);
        circles.trimToSize();
        circlesAlive.sort(Comparator.comparingDouble(CircleImage::getScore));
        circles.addAll(circlesAlive);
        //circles.sort(Collections.reverseOrder());
    }

    /**
     * This method kills the worst circles
     */
    public void killWorst() {
        sort();
        if ( circles.size() > circlePopulationLimits.getX() ) {
            for (int i = 0; i < circles.size() - circlePopulationLimits.getX(); i++) {
                if (!circles.get(i).isDead()) {
                    circles.get(i).setDead(true);
                }
            }
        }
    }

    /**
     * This method is used to make a more beautiful
     * transition for the circles
     * On this code the alpha chanel of the die circle
     * is decreased until it reaches zero and then the
     * circle is removed from the array
     */
    public void updateDiedCircles() {
        ArrayList<CircleImage> circlesToRemove = new ArrayList<>();
        for (CircleImage circle : circles) {
            if (circle.isDead()) {
                circle.getColor().setAlpha(circle.getColor().getAlpha() - ALPHA_DECREASE);
                if (circle.getColor().getAlpha() < ALPHA_DECREASE) {
                    circlesToRemove.add(circle);
                }
            }
        }
        circles.removeAll(circlesToRemove);
    }

    /**
     * This method add the babies of the circles
     * to the circles array
     */
    private void makeNewCircles() {
        ArrayList<CircleImage> circlesBabies = new ArrayList<>();

        for ( CircleImage c : circles ) {
            if ( !c.isDead() ) {
                circlesBabies.addAll(factory.buildBabies(c));
            }
        }

        circles.addAll(circlesBabies);
    }

    /**
     * This method add babies of the circles to the
     * circles array, each specified time interval
     * @param elapsedTime the elapsed time between each frame
     */
    public void makeNewCircles(float elapsedTime) {
        time += elapsedTime;
        if ( time >= updateCapMakeNewBabies ) {
            makeNewCircles();
            time -= updateCapMakeNewBabies;
        }
    }

    /**
     * This method manages the collision of the circles with the
     * edges
     * @param gc the game container object with the width and height
     */
    private void updateCircleEdgeCollision(GameContainer gc, CircleImage c) {
        if (c.getPosition().getX() - c.getSize() < 0) {
            c.getPosition().setX(c.getSize());
        }
        if (c.getPosition().getX() + c.getSize() >= gc.getWidth()) {
            c.getPosition().setX(gc.getWidth() - c.getSize());
        }
        if (c.getPosition().getY() - c.getSize() < 0) {
            c.getPosition().setY(c.getSize());
        }
        if (c.getPosition().getY() + c.getSize() >= gc.getHeight()) {
            c.getPosition().setY(gc.getHeight() - c.getSize());
        }
    }

    /**
     * This method says if the two circles are overlap
     * @param f the first circle
     * @param s the second circle
     * @return if the two circles are overlap
     */
    private boolean doCirclesOverlap(CircleImage f, CircleImage s) {
        return Math.abs((f.getPosition().getX() - s.getPosition().getX()) * (f.getPosition().getX() - s.getPosition().getX()) +
                        (f.getPosition().getY() - s.getPosition().getY()) * (f.getPosition().getY() - s.getPosition().getY()))
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
        if ( c.getId() != t.getId() && doCirclesOverlap(c, t) ) {
            float dist = calculateDistance(c, t);

            if ( dist == 0 ) {
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

    /**
     * This method calls the two methods:
     * updateStaticCollisions & updateEdgeCollision
     */
    public void updateCollisions(GameContainer gc) {
        for ( CircleImage c : circles ) {
            if ( !c.isDead() ) {
                updateCircleEdgeCollision(gc, c);

                for ( CircleImage t : circles ) {
                    if ( !t.isDead() ) {
                        updateCircleTargetStaticCollision(c, t);
                    }
                }

            }
        }
    }

    /**
     * This method draws all the circles
     * @param r the renderer object with all drawing methods
     * @param isDrawingScore if the method has to draw the score of the circles
     * @param isDrawingBorder if the method has to draw the border of the circles
     */
    public void drawAllCircles(Renderer r, boolean isDrawingBorder, boolean isDrawingScore) {
        for ( CircleImage c : circles ) {
           c.drawYourSelf(r, isDrawingBorder, isDrawingScore);
        }
    }

    /**
     * This method draws only the live circles
     * @param r the renderer object with all drawing methods
     * @param isDrawingScore if the method has to draw the score of the circles
     * @param isDrawingBorder if the method has to draw the border of the circles
     */
    public void drawLiveCircles(Renderer r, boolean isDrawingBorder, boolean isDrawingScore) {
        for ( CircleImage c : circles ) {
            if ( !c.isDead() ) {
                c.drawYourSelf(r, isDrawingBorder, isDrawingScore);
            }
        }
    }

    /**
     * This method calculates the number of
     * live circles and dead circles of the population
     */
    public void calculateLiveDeadCount() {
        liveDeadCount = new Vec2di();
        for ( CircleImage c : circles ) {
            if ( !c.isDead() ) {
                liveDeadCount.addToX(1);
            } else {
                liveDeadCount.addToY(1);
            }
        }
    }

    public void update(GameContainer gc, float elapsedTime, Image background) {

        ArrayList<CircleImage> babies = new ArrayList<>();

        liveDeadCount = new Vec2di(0, 0);

        // sort the circles by the
        sort();

        if ( circles.size() > circlePopulationLimits.getX() ) {
            for ( int i = 0; i < circles.size() - circlePopulationLimits.getX() - 1; i++ ) {
                circles.get(i).setDead(true);
            }
        }

        setIdToCircles();

        for ( CircleImage c : circles ) {
            if ( !c.isDead() ) {
                // count
                liveDeadCount.addToX(1);

                // Physics
                // Edge collisions:
                updateCircleEdgeCollision(gc, c);

                // static collisions:
                for ( CircleImage t : circles ) {
                    if ( !t.isDead() ) {
                        updateCircleTargetStaticCollision(c, t);
                    }
                }

                // Calculate score
                calculateCircleScore(c, background);

                // Make new circles
                time += elapsedTime;
                if ( time >= updateCapMakeNewBabies ) {
                    babies.addAll(factory.buildBabies(c));
                    time -= updateCapMakeNewBabies;
                }

            } else {
                // count
                liveDeadCount.addToY(1);

                // Decrease the alpha channel
                c.getColor().setAlpha(c.getColor().getAlpha() - ALPHA_DECREASE);
            }
        }

        // Delete all circles what have less than ALPHA_DECREASE
        circles.removeIf((CircleImage c)-> c.getColor().getAlpha() <= ALPHA_DECREASE);

        circles.addAll(babies);

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

    public float getUpdateCapMakeNewBabies() {
        return updateCapMakeNewBabies;
    }

    public Vec2di getLiveDeadCount() {
        return liveDeadCount;
    }

    public int getNumLiveCircles() {
        return liveDeadCount.getX();
    }

    public int getNumDeadCircles() {
        return liveDeadCount.getY();
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

    public void setUpdateCapMakeNewBabies(float updateCapMakeNewBabies) {
        this.updateCapMakeNewBabies = updateCapMakeNewBabies;
    }

    @Override
    public String toString() {
        return "Num individuals: " + circles.size() + " factory [" + factory.toString()
                + "] population limits " + circlePopulationLimits + " proximity penalty " + penaltyProximity
                + " num babies by circle " + numBabiesByCircle + " update cap for make new babies " + updateCapMakeNewBabies;
    }

}
