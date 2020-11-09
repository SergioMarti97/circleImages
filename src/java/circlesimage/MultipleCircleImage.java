package circlesimage;

import engine.AbstractGame;
import engine.GameContainer;
import engine.gfx.HexColors;
import engine.gfx.Renderer;
import engine.gfx.images.Image;
import engine.vectors.points2d.Vec2di;
import engine.vectors.points3d.Vec3di;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * This class is the program
 *
 * This is not a genetic or evolutionary algorithm,
 * it's only my first approach to this topics
 *
 * This program tries to simulate the evolution
 * of distinct colonies composed of circles
 * which differ in color, size and position
 *
 * To avoid some problems, it has been realized
 * some particular adjustment. Like:
 * - The individuals can move each other to avoid
 * the colony get stuck in one point
 * - The individuals have a penalty for proximity
 * with other individuals. This penalty can be modified
 * by the user on the program, or much better, on the
 * parameters.txt file. By default, the penalty is
 * 0.001 less score for close individuals
 * - It not simulates the evolution theory at all.
 * because in each update there only remains five hundred
 * living circles with the highest score of all
 * Evolution doesn't works like this, it's more
 * complex an there are more factors to have in account.
 * - To make more beautiful shapes, the are two circles
 * arrays or two circles populations. The population
 * of living circles and the population of die circles.
 * The living circles can have children and they can
 * move other circles. The died circles can have children
 * and they remains in their spot. In each frame the
 * chanel alpha of the died circles updates to make
 * they more transparent, to finally disappear and
 * then get removed. This allows more
 * beautiful transition
 *
 * @class MultipleCircleImage
 * @author Sergio Martí Torregrosa
 * @date 07/11/2020
 */
public class MultipleCircleImage extends AbstractGame {

    /**
     * The screen dimensions
     * By default 540 width | 360 height | 2 scale
     */
    private static final Vec3di screenDimensions = new Vec3di(540, 360, 2);

    /**
     * The decrease of the alpha channel each time
     */
    private final int ALPHA_DECREASE = 5;

    /**
     * The circles on screen what can have babies
     */
    private ArrayList<CircleImage> circles;

    /**
     * The died circles, they can't have babies
     */
    private ArrayList<CircleImage> diedCircles;

    /**
     * The factory class which builds new circles
     */
    private CircleImageFactory factory;

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
     * The maximum and minimum number circles what
     * can have babies
     * X = maximum, by default 500
     * Y = minimum, by default 20
     */
    private Vec2di circlePopulationLimits = new Vec2di();

    /**
     * The num of circles to add each time
     * By default 10
     */
    private int numCirclesIncrement;

    /**
     * The number of babies what has each circle
     * By default 3
     */
    private int numBabiesByCircle;

    /**
     * The penalty proximity for the circles
     * To prevent circles from stalling at one point,
     * there is a proximity penalty
     * By default 0.001
     */
    private double penaltyProximity;

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
     * A flag for showing always texts
     * By default, false
     */
    private boolean isShowingAlwaysText;

    /**
     * The constructor of the application
     * @param title the title of the application. It will be showed on the main bar
     *              of the application
     */
    private MultipleCircleImage(String title) {
        super(title);
    }

    /**
     * This method sets the screen parameters extracted from the
     * parameters.txt document
     * @param splittedLine the splitted line with the information
     */
    private void setScreenParameters(String[] splittedLine) {
        if ( splittedLine[0].equalsIgnoreCase("width") ) {
            screenDimensions.setX(Integer.parseInt(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("height") ) {
            screenDimensions.setY(Integer.parseInt(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("scale") ) {
            screenDimensions.setZ(Integer.parseInt(splittedLine[1]));
        }
    }

    /**
     * This method sets the population parameters extracted from the
     * parameters.txt document
     * @param splittedLine the splitted line with the information
     */
    private void setPopulationParameters(String[] splittedLine) {
        if ( splittedLine[0].equalsIgnoreCase("max-initial-circles") ) {
            circlePopulationLimits.setX(Integer.parseInt(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("min-circles") ) {
            circlePopulationLimits.setY(Integer.parseInt(splittedLine[1]));
        }

        if ( splittedLine[0].equalsIgnoreCase("num-circles-increment") ) {
            numCirclesIncrement = Integer.parseInt(splittedLine[1]);
        }
        if ( splittedLine[0].equalsIgnoreCase("num-babies-by-circle") ) {
            numBabiesByCircle = Integer.parseInt(splittedLine[1]);
        }
        if ( splittedLine[0].equalsIgnoreCase("penalty-proximity") ) {
            penaltyProximity = Double.parseDouble(splittedLine[1]);
        }
    }

    /**
     * This method sets the parameters of the CircleFactory
     * @param splittedLine the splitted line with all information
     */
    private void setVariationCircleImages(String[] splittedLine) {
        if ( splittedLine[0].equalsIgnoreCase("max-circle-size") ) {
            factory.setMaxCircleSize(Integer.parseInt(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("max-variation-size") ) {
            factory.getVariationSize().setX(Integer.parseInt(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("min-variation-size") ) {
            factory.getVariationSize().setY(Integer.parseInt(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("max-variation-position") ) {
            factory.getVariationPosition().setX(Integer.parseInt(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("min-variation-position") ) {
            factory.getVariationPosition().setY(Integer.parseInt(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("max-variation-position") ) {
            factory.getVariationColor().setX(Integer.parseInt(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("min-variation-position") ) {
            factory.getVariationColor().setY(Integer.parseInt(splittedLine[1]));
        }
    }

    /**
     * This method reads a text file (parameters.txt)
     * and sets all the parameters of the program
     */
    private void readParameters() {
        File file = new File("C:\\Users\\Sergio\\IdeaProjects\\engine-circlesimage\\parameters.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while ( line != null ) {
                String[] splittedLine = line.split(" ");

                setScreenParameters(splittedLine);
                setPopulationParameters(splittedLine);
                setVariationCircleImages(splittedLine);

                if ( splittedLine[0].equalsIgnoreCase("show-texts-on-screen") ) {
                    isShowingAlwaysText = splittedLine[1].equalsIgnoreCase("true");
                }

                line = br.readLine();
            }
            br.close();
        } catch ( IOException e ) {
            System.out.println("The file can't be read!");
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(GameContainer gameContainer) {
        factory = new CircleImageFactory();
        readParameters();
        background = new Image("/david.jpg");
        circles = factory.buildRandomCircleImageArray(gameContainer, circlePopulationLimits.getY());
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
    private void killWorstCircles() {
        while ( circles.size() > circlePopulationLimits.getX() ) {
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
            circles = factory.buildRandomCircleImageArray(gc, circlePopulationLimits.getY());
            diedCircles.clear();
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_B) ) {
            isShowingBackgroundImage = !isShowingBackgroundImage;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_S) ) {
            isShowingCirclesScore = !isShowingCirclesScore;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_UP) ) {
            circlePopulationLimits.addToX(numCirclesIncrement);
            isShowingText = true;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_DOWN) ) {
            if ( circlePopulationLimits.getX() > 0 ) {
                circlePopulationLimits.addToX(-numCirclesIncrement);
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
        r.drawFillCircle(
                (int)c.getPosition().getX(),
                (int)c.getPosition().getY(),
                (int)c.getSize(),
                c.getColor().getCode()
        );

        if ( isShowingBackgroundImage ) {
            r.drawCircle(
                    (int)c.getPosition().getX(),
                    (int)c.getPosition().getY(),
                    (int)c.getSize(),
                    HexColors.WHITE
            );
        }

        if ( isDrawingScore ) {
            r.drawText(
                    String.format("%.2f%%", c.getScore() * 100),
                    (int)c.getPosition().getX(),
                    (int)c.getPosition().getY(),
                    HexColors.WHITE
            );
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
        r.drawFillRectangle(5, 5, 277, 55, textBoxColor.getCode());
        r.drawRectangle(5, 5, 277, 55, textColor.getCode());
        r.drawText("Num circles: " + circles.size(), 10, 10, textColor.getCode());
        r.drawText("Drawn circles: " + numDrawnCircles, 10, 30, textColor.getCode());
        numDrawnCircles = 0;
    }


    /**
     * This method shows the texts of the program,
     * having in account the flags of showing texts
     * @param r the renderer object with all drawing methods
     */
    private void showTexts(Renderer r) {
        if ( isShowingAlwaysText ) {
            drawTexts(r);
        } else {
            if ( isShowingText ) {
                drawTexts(r);
            }
        }
    }

    @Override
    public void render(GameContainer gameContainer, Renderer renderer) {
        drawBackground(renderer);
        drawAllCircles(renderer, isShowingCirclesScore);
        showTexts(renderer);
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
