package circlesimage;

import engine.AbstractGame;
import engine.GameContainer;
import engine.gfx.HexColors;
import engine.gfx.Renderer;
import engine.gfx.images.Image;
import engine.vectors.points2d.Vec2df;
import engine.vectors.points3d.Vec3di;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * This class is the program
 *
 * @class MultipleCircleImage
 * @author Sergio Martí Torregrosa
 * @date 07/11/2020
 */
public class MultipleCircleImage extends AbstractGame {

    /**
     * The screen dimensions: 540 width | 360 height | 2 scale
     */
    private static final Vec3di screenDimensions = new Vec3di(540, 360, 2);

    /**
     * The num of circles to add each time
     */
    private final int NUM_CIRCLES_INCREMENT = 10;

    /**
     * Minimum number of circles
     */
    private final int MIN_CIRCLES = 20;

    /**
     * The number of babies what has each circle
     */
    private final int NUM_BABIES_BY_CIRCLE = 3;

    /**
     * The decrease of the alpha channel each time
     */
    private final int ALPHA_DECREASE = 5;

    /**
     * The penalty for proximity
     */
    private final double PENALTY_PROXIMITY = 0.001f;

    /**
     * The circles on screen what can have babies
     */
    private ArrayList<CircleImage> circles;

    /**
     * The died circles, they can't have babies
     */
    private ArrayList<CircleImage> diedCircles;

    /**
     * The text color
     */
    private CircleColor textColor = new CircleColor(HexColors.WHITE);

    /**
     * The box where is the text
     */
    private CircleColor textBoxColor = new CircleColor(HexColors.BLACK);

    /**
     * The background image
     */
    private Image background;

    /**
     * The maximum number circles what can have babies
     */
    private int maxCircles = 500;

    /**
     * A counter for the drawn circles of screen
     */
    private int numDrawnCircles = 0;

    /**
     * The time between each update
     */
    private float time = 0;

    /**
     * A flag for showing or not showing the background image
     */
    private boolean isShowingBackgroundImage = false;

    /**
     * A flag for showing or not showing the score of the circles
     */
    private boolean isShowingCirclesScore = false;

    /**
     * A flag for showing or not showing the text
     */
    private boolean isShowingText = false;

    /**
     * The constructor of the application
     * @param title the title of the application. It will be showed on the main bar
     *              of the application
     */
    private MultipleCircleImage(String title) {
        super(title);
    }

    /**
     * This method returns a random int between the specified
     * interval
     * @param max the maximum value of the interval
     * @param min the minimum value of the interval
     * @return a random integer value
     */
    private int randomIntBetween(int max, int min) {
        return (int)(Math.random() * (max - min)) + min;
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
                randomIntBetween(3, 1),
                new CircleColor(
                        randomIntBetween(255, 0),
                        randomIntBetween(255, 0),
                        randomIntBetween(255, 0),
                        255
                )
        );
    }

    /**
     * This method builds an array full of distinct CircleImages
     * @param gc the game container object
     * @param size the size of the array
     * @return an array of circle images
     */
    private ArrayList<CircleImage> buildRandomCircleImageArray(GameContainer gc, int size) {
        ArrayList<CircleImage> arrayList = new ArrayList<>();
        for ( int i = 0; i < size; i++ ) {
            CircleImage c = buildRandomCircleImage(gc);
            c.setId(arrayList.size());
            arrayList.add(c);
        }
        return arrayList;
    }

    @Override
    public void initialize(GameContainer gameContainer) {
        background = new Image("/universe.jpg");
        circles = buildRandomCircleImageArray(gameContainer, MIN_CIRCLES);
        diedCircles = new ArrayList<>();
        calculateCirclesScore();
        updateCirclesOverlap();
    }

    /**
     * This method calculates the score for the circles
     */
    private void calculateCirclesScore() {
        for ( CircleImage c : circles ) {
            c.calculateScore(background);
            for ( CircleImage t : circles ) {
                if ( c.getId() != t.getId() ) {
                    float distance2 = calculateDistance2(c, t);
                    float sizes = 5 * (c.getSize() + t.getSize());
                    if ( distance2 <=  (sizes * sizes) ) {
                        c.setScore(c.getScore() - PENALTY_PROXIMITY / 2);
                        t.setScore(t.getScore() - PENALTY_PROXIMITY / 2);
                    }
                }
            }
        }
    }

    /**
     * This method kills the worst circles
     */
    private void killWorstCircles() {
        while ( circles.size() > maxCircles) {
            CircleImage c = circles.remove(0);
            c.getColor().setAlpha(c.getColor().getAlpha() - ALPHA_DECREASE);
            diedCircles.add(c);
        }
    }

    /**
     * This method add the babies of the circles
     * to the circles array
     */
    private void makeNewCircles() {
        ArrayList<CircleImage> circlesBabies = new ArrayList<>();

        for ( CircleImage c : circles ) {
            for ( int i = 0; i < NUM_BABIES_BY_CIRCLE; i++ ) {
                CircleImage t = c.getBaby();
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
    private void updateCirclesCollisionEdges(GameContainer gc) {
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
    private void updateCircleTargetOverlap(CircleImage c, CircleImage t) {
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
    private void updateCirclesOverlap() {
        for ( CircleImage c : circles ) {
            for ( CircleImage t : circles ) {
                updateCircleTargetOverlap(c, t);
            }
        }
    }

    /**
     * This method updates the user input
     * @param gc the game container object, which has the input object
     */
    private void updateUserInput(GameContainer gc) {
        if ( gc.getInput().isKeyDown(KeyEvent.VK_SPACE) ) {
            circles = buildRandomCircleImageArray(gc, MIN_CIRCLES);
            diedCircles.clear();
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_B) ) {
            isShowingBackgroundImage = !isShowingBackgroundImage;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_S) ) {
            isShowingCirclesScore = !isShowingCirclesScore;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_UP) ) {
            maxCircles += NUM_CIRCLES_INCREMENT;
            isShowingText = true;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_DOWN) ) {
            if ( maxCircles > 0 ) {
                maxCircles -= NUM_CIRCLES_INCREMENT;
            }
            isShowingText = true;
        }
    }

    /**
     * This method updates the user input for change the
     * background image
     * @param gc the game container object which contains
     *           the input object
     */
    private void updateBackgroundImage(GameContainer gc) {
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD1) ) {
            background = new Image("/colorSplash01.jpg");
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD2) ) {
            background = new Image("/colorSplash02.jpg");
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD3) ) {
            background = new Image("/dynastes_hercules.jpg");
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD4) ) {
            background = new Image("/roses.jpg");
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD5) ) {
            background = new Image("/stockPhoto01.jpg");
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD6) ) {
            background = new Image("/stockPhoto02.jpg");
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD7) ) {
            background = new Image("/stockPhoto03.jpg");
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD8) ) {
            background = new Image("/stockPhoto04.jpg");
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD9) ) {
            background = new Image("/universe.jpg");
        }
    }

    /**
     * This method is used to make a more beautiful
     * transition for the circles
     * On this code the alpha chanel of the die circle
     * is decreased until it reaches zero and then the
     * circle is removed from the array
     */
    private void updateDieCircles() {
        ArrayList<Integer> indexToRemove = new ArrayList<>();
        for ( int i = 0; i < diedCircles.size(); i++ ) {
            diedCircles.get(i).getColor().setAlpha(diedCircles.get(i).getColor().getAlpha() - ALPHA_DECREASE);
            if ( diedCircles.get(i).getColor().getAlpha() < ALPHA_DECREASE ) {
                indexToRemove.add(i);
            }
        }
        for ( Integer index : indexToRemove ) {
            diedCircles.remove(index.intValue());
        }
    }

    /**
     * This method updates the color
     * text, for make a more beautiful transition
     * when the texts are updated
     */
    private void updateColorText() {
        if ( isShowingText ) {
            if ( textColor.getAlpha() <= ALPHA_DECREASE ) {
                textColor.setAlpha(255);
                textBoxColor.setAlpha(255);
                isShowingText = false;
            } else {
                textColor.setAlpha(textColor.getAlpha() - 1);
                textBoxColor.setAlpha(textBoxColor.getAlpha() - 1);
            }
        }
    }

    @Override
    public void update(GameContainer gameContainer, float v) {
        updateUserInput(gameContainer);
        updateBackgroundImage(gameContainer);

        time += v;
        if ( time >= 0.15 ) {
            makeNewCircles();
            time -= 0.15;
        }

        updateCirclesCollisionEdges(gameContainer);
        updateCirclesOverlap();

        calculateCirclesScore();
        circles.sort(Comparator.comparingDouble(CircleImage::getScore));
        killWorstCircles();

        updateDieCircles();

        updateColorText();
    }

    /**
     * This method draws the circle on the parameter
     * @param r the renderer object with all drawing methods
     * @param c the circle to draw
     * @param isDrawingScore if the method has to draw the score of the circle
     */
    private void drawCircle(Renderer r, CircleImage c, boolean isDrawingScore) {
        r.drawFillCircle((int)c.getPosition().getX(), (int)c.getPosition().getY(), (int)c.getSize(), c.getColor().getCode());
        if ( isShowingBackgroundImage ) {
            r.drawCircle((int) c.getPosition().getX(), (int) c.getPosition().getY(), (int) c.getSize(), HexColors.WHITE);
        }
        if ( isDrawingScore ) {
            r.drawText(String.format("%.2f%%", c.getScore() * 100), (int) c.getPosition().getX(), (int) c.getPosition().getY(), HexColors.WHITE);
        }
    }

    /**
     * This method draws all the circles (normal circles & die circles)
     * @param r the renderer object with all drawing methods
     * @param isDrawingScore if the method has to draw the score of the circles
     */
    private void drawAllCircles(Renderer r, boolean isDrawingScore) {
        for ( CircleImage c : circles ) {
            drawCircle(r, c, isDrawingScore);
            numDrawnCircles++;
        }

        for ( CircleImage c : diedCircles ) {
            drawCircle(r, c, false);
            numDrawnCircles++;
        }
    }

    /**
     * This method draws the background
     * @param r the renderer object with all drawing methods
     */
    private void drawBackground(Renderer r) {
        if ( isShowingBackgroundImage ) {
            r.drawImage(background, 0, 0);
        } else {
            r.clear(HexColors.WHITE);
        }
    }

    /**
     * This method draws the texts of the program
     * @param r the renderer object with all drawing methods
     */
    private void drawTexts(Renderer r) {
        if ( isShowingText ) {
            r.drawFillRectangle(5, 5, 250, 55, textBoxColor.getCode());
            r.drawRectangle(5, 5, 250, 55, textColor.getCode());
            r.drawText("Num circles: " + circles.size(), 10, 10, textColor.getCode());
            r.drawText("Num circles: " + numDrawnCircles, 10, 30, textColor.getCode());
        }
        numDrawnCircles = 0;
    }

    @Override
    public void render(GameContainer gameContainer, Renderer renderer) {
        drawBackground(renderer);
        drawAllCircles(renderer, isShowingCirclesScore);
        drawTexts(renderer);
    }

    /**
     * The main method, the executing point
     * @param args the arguments of the application
     */
    public static void main(String[] args) {
        GameContainer gc = new GameContainer(new MultipleCircleImage("Testing multiple Circles"));
        gc.setWidth(screenDimensions.getX());
        gc.setHeight(screenDimensions.getY());
        gc.setScale(screenDimensions.getZ());
        gc.start();
    }

}
