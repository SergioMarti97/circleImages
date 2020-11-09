package circlesimage;

import engine.AbstractGame;
import engine.GameContainer;
import engine.gfx.HexColors;
import engine.gfx.Renderer;
import engine.gfx.images.Image;
import engine.vectors.points3d.Vec3di;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
 * the colony to get stuck in one point
 * - The individuals have a penalty for proximity
 * with other individuals. This penalty can be modified
 * by the user on the program, or much better, on the
 * parameters.txt file. By default, the penalty is
 * 0.001 less score for close individuals
 * - It doesn't simulate the evolution theory at all.
 * because in each update there only remains five hundred
 * living circles with the highest score of all.
 * Evolution doesn't works like this, it's more
 * complex an there are more factors to have in account.
 * - To make more beautiful shapes, the are two circles
 * arrays or two circles populations: the population
 * of living circles and the population of die circles.
 * The living circles can have children and they can
 * move other circles. The died circles can't have children
 * and they remain in their spot. In each frame the
 * channel alpha of the died circles updates to make
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
    private CircleImagePopulation population;

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
     * This renderer only takes in account the drawing of circles,
     * and don't has in account the texts and the background image
     * This is useful for compare the image made by the circles
     * and the background image
     */
    private Renderer populationRenderer;

    /**
     * The num of circles to add each time
     * By default 10
     */
    private int numCirclesIncrement;

    /**
     * The time between each update
     */
    private float time = 0;

    /**
     * This is the buffered Image composed
     * by the circles
     */
    private int[] buffer;

    /**
     * The fitness what has the image conformed
     * by the circles
     */
    private double fitnessImage = 0.0;

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
    private boolean isShowingAlwaysText = false;

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
            population.getCirclePopulationLimits().setX(Integer.parseInt(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("min-circles") ) {
            population.getCirclePopulationLimits().setY(Integer.parseInt(splittedLine[1]));
        }

        if ( splittedLine[0].equalsIgnoreCase("num-circles-increment") ) {
            numCirclesIncrement = Integer.parseInt(splittedLine[1]);
        }
        if ( splittedLine[0].equalsIgnoreCase("num-babies-by-circle") ) {
            population.setNumBabiesByCircle(Integer.parseInt(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("penalty-proximity") ) {
            population.setPenaltyProximity(Double.parseDouble(splittedLine[1]));
        }
    }

    /**
     * This method sets the parameters of the CircleFactory
     * @param splittedLine the splitted line with all information
     */
    private void setVariationCircleImages(String[] splittedLine) {
        if ( splittedLine[0].equalsIgnoreCase("max-circle-size") ) {
            population.getFactory().setMaxCircleSize(Integer.parseInt(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("max-variation-size") ) {
            population.getFactory().getVariationSize().setX(Integer.parseInt(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("min-variation-size") ) {
            population.getFactory().getVariationSize().setY(Integer.parseInt(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("max-variation-position") ) {
            population.getFactory().getVariationPosition().setX(Integer.parseInt(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("min-variation-position") ) {
            population.getFactory().getVariationPosition().setY(Integer.parseInt(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("max-variation-position") ) {
            population.getFactory().getVariationColor().setX(Integer.parseInt(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("min-variation-position") ) {
            population.getFactory().getVariationColor().setY(Integer.parseInt(splittedLine[1]));
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
        population = new CircleImagePopulation();
        populationRenderer = new Renderer(gameContainer);
        readParameters();
        background = new Image("/david.jpg");
        buffer = populationRenderer.getP();
        population.buildPopulation(gameContainer);
        diedCircles = new ArrayList<>();
        population.calculateCirclesScore(background);
        population.updateStaticCollisions();
    }

    /**
     * This method calculates the fitness of the image
     * conformed by the circles and the background
     * @param back the background image
     * @param front the buffer image created by the circles
     * @return the fitness of the front image with the back image
     */
    private double calculateImageFitness(int[] back, int[] front) {
        CircleColor b;
        CircleColor f;
        double fitness = 0.0;
        for ( int i = 0; i < front.length; i++ ) {
            b = new CircleColor(back[i]);
            f = new CircleColor(front[i]);
            fitness += f.getSimilarityAlphaAlso(b);
        }
        return fitness / (double) front.length;
    }

    /**
     * This method updates the user input
     * @param gc the game container object, which has the input object
     */
    private void updateUserInput(GameContainer gc) {
        if ( gc.getInput().isKeyDown(KeyEvent.VK_SPACE) ) {
            population.buildPopulation(gc);
            diedCircles.clear();
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_B) ) {
            isShowingBackgroundImage = !isShowingBackgroundImage;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_S) ) {
            isShowingCirclesScore = !isShowingCirclesScore;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_UP) ) {
            population.getCirclePopulationLimits().addToX(numCirclesIncrement);
            isShowingText = true;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_DOWN) ) {
            if ( population.getCirclePopulationLimits().getX() > 0 ) {
                population.getCirclePopulationLimits().addToX(-numCirclesIncrement);
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
            buffer = background.getP();
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD2) ) {
            background = new Image("/colorSplash02.jpg");
            buffer = background.getP();
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD3) ) {
            background = new Image("/dynastes_hercules.jpg");
            buffer = background.getP();
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD4) ) {
            background = new Image("/roses.jpg");
            buffer = background.getP();
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD5) ) {
            background = new Image("/stockPhoto01.jpg");
            buffer = background.getP();
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD6) ) {
            background = new Image("/stockPhoto02.jpg");
            buffer = background.getP();
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD7) ) {
            background = new Image("/stockPhoto03.jpg");
            buffer = background.getP();
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD8) ) {
            background = new Image("/stockPhoto04.jpg");
            buffer = background.getP();
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD9) ) {
            background = new Image("/universe.jpg");
            buffer = background.getP();
        }
    }

    /**
     * This method is used to make a more beautiful
     * transition for the circles
     * On this code the alpha chanel of the die circle
     * is decreased until it reaches zero and then the
     * circle is removed from the array
     */
    private void updateDiedCircles() {
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
            population.makeNewCircles();
            time -= 0.15;
        }

        population.updateEdgeCollision(gameContainer);
        population.updateStaticCollisions();

        population.calculateCirclesScore(background);
        diedCircles.addAll(population.killWorst());

        updateDiedCircles();

        updateColorText();

        fitnessImage = calculateImageFitness(background.getP(), buffer);
        buffer = populationRenderer.getP();
    }

    /**
     * This method draws all circles, live circles and died circles
     * @param r the renderer object with all drawing methods
     */
    private void drawAllCircles(Renderer r, boolean isShowingBackgroundImage, boolean isShowingCirclesScore) {
        population.drawCircles(r, isShowingBackgroundImage, isShowingCirclesScore);
        for ( CircleImage died : diedCircles ) {
            died.drawYourSelf(r, isShowingBackgroundImage, isShowingCirclesScore);
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
        r.drawFillRectangle(5, 5, 350, 75, textBoxColor.getCode());
        r.drawRectangle(5, 5, 350, 75, textColor.getCode());
        r.drawText("Circles alive: " + population.getCircles().size(), 10, 10, textColor.getCode());
        r.drawText("Drawn circles: " + (population.getCircles().size() + diedCircles.size()), 10, 30, textColor.getCode());
        r.drawText(String.format("Fitness average: %.3f%%", fitnessImage * 100), 10, 50, textColor.getCode());
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
        drawAllCircles(populationRenderer, false, false);
        drawBackground(renderer);
        drawAllCircles(renderer, isShowingBackgroundImage, isShowingCirclesScore);
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
