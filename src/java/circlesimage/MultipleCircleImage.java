package circlesimage;

import engine.AbstractGame;
import engine.GameContainer;
import engine.gfx.Renderer;
import engine.gfx.images.Image;
import engine.vectors.points3d.Vec3di;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
     * The circles on screen what can have babies
     */
    private CircleImagePopulation population;

    /**
     * This renderer only takes in account the drawing of circles,
     * and don't has in account the texts and the background image
     * This is useful for compare the image made by the circles
     * and the background image
     */
    private Renderer populationRenderer;

    /**
     * This are the paths where are the background images
     * what the program uses
     */
    private String[] backgroundsImagesPaths = new String[10];

    /**
     * The background images
     */
    private Image[] backgrounds;

    /**
     * The path for the screen shoots images what the
     * program can make
     */
    private String screenShootPath;

    /**
     * Needed time for fade off the text on screen
     */
    private double timeToFadeOffTexts;

    /**
     * The actual index of the image what is using the program
     */
    private int indexBackground = 0;

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
     * The user can change the number of live
     * circles, and this is the number of
     * living circles to increase each time the
     * user wants
     * By default it's 10
     */
    private int circlesIncrement;

    /**
     * The text color
     */
    private CircleColor textColor;

    /**
     * The color of the box where is the text
     */
    private CircleColor textBoxColor;

    /**
     * The border of the box where is the text
     */
    private CircleColor textBoxStrokeColor;

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
            circlesIncrement = Integer.parseInt(splittedLine[1]);
        }
        if ( splittedLine[0].equalsIgnoreCase("num-babies-by-circle") ) {
            population.setNumBabiesByCircle(Integer.parseInt(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("make-babies-cap") ) {
            population.setMakeBabiesCap(Double.parseDouble(splittedLine[1]));
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
     * This method manages the setting of cosmetic
     * part of the program
     * @param splittedLine the line with the information
     */
    private void setProgramCosmetics(String[] splittedLine) {
        if ( splittedLine[0].equalsIgnoreCase("text-color") ) {
            textColor = new CircleColor(HexColors.getHexColor(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("text-box-color") ) {
            textBoxColor = new CircleColor(HexColors.getHexColor(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("text-box-stroke-color") ) {
            textBoxStrokeColor = new CircleColor(HexColors.getHexColor(splittedLine[1]));
        }
        if ( splittedLine[0].equalsIgnoreCase("show-texts-on-screen") ) {
            isShowingAlwaysText = splittedLine[1].equalsIgnoreCase("true");
        }
        if ( splittedLine[0].equalsIgnoreCase("time-fade-off-text") ) {
            timeToFadeOffTexts = Double.parseDouble(splittedLine[1]);
        }
    }

    /**
     * This method sets the screen shoot path
     * @param splittedLine the line which contains all the information
     */
    private void setScreenShootPath(String[] splittedLine) {
        if ( splittedLine[0].equalsIgnoreCase("screen-shoot-path") ) {
            screenShootPath = splittedLine[1];
        }
    }

    /**
     * This method manages the setting of the paths where are
     * the background images what the program is going to use
     * @param splittedLine the line which contains all information
     */
    private void setBackgroundsImagesPaths(String[] splittedLine) {
        if ( splittedLine[0].matches("image-[0-9]") ) {
            int imageIndex = Integer.parseInt(splittedLine[0].split("-")[1]);
            switch ( imageIndex ) {
                case 0: default:
                    backgrounds[0] = new Image(splittedLine[1]);
                    break;
                case 1:
                    backgrounds[1] = new Image(splittedLine[1]);
                    break;
                case 2:
                    backgrounds[2] = new Image(splittedLine[1]);
                    break;
                case 3:
                    backgrounds[3] = new Image(splittedLine[1]);
                    break;
                case 4:
                    backgrounds[4] = new Image(splittedLine[1]);
                    break;
                case 5:
                    backgrounds[5] = new Image(splittedLine[1]);
                    break;
                case 6:
                    backgrounds[6] = new Image(splittedLine[1]);
                    break;
                case 7:
                    backgrounds[7] = new Image(splittedLine[1]);
                    break;
                case 8:
                    backgrounds[8] = new Image(splittedLine[1]);
                    break;
                case 9:
                    backgrounds[9] = new Image(splittedLine[1]);
                    break;
            }
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
                setBackgroundsImagesPaths(splittedLine);
                setScreenShootPath(splittedLine);
                setProgramCosmetics(splittedLine);
                line = br.readLine();
            }
            br.close();
        } catch ( IOException e ) {
            System.out.println("The file can't be read!");
            e.printStackTrace();
        }
    }

    /**
     * This method sets the backgrounds array
     * for avoid null pointer problems
     */
    private void initializeBackgrounds() {
        backgrounds = new Image[10];
        for ( int i = 0; i < backgroundsImagesPaths.length; i++ ) {
            backgrounds[i] = new Image();
        }
    }

    @Override
    public void initialize(GameContainer gameContainer) {
        population = new CircleImagePopulation();
        populationRenderer = new Renderer(gameContainer);
        buffer = populationRenderer.getP();
        initializeBackgrounds();

        readParameters();

        population.buildPopulation(gameContainer);
        population.updateCollisions(gameContainer);
        population.calculateCirclesScore(backgrounds[0]);
    }

    /**
     * This method makes an screen shoot of the
     * state of the circles
     * @param gc the game container object with the window object
     */
    private void makeScreenShoot(GameContainer gc) {
        File file = new File(screenShootPath + "screenShoot.jpg");
        String imageFormat = "jpg";

        BufferedImage image = new BufferedImage(gc.getWidth(), gc.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics g = image.getGraphics();
        g.drawImage(gc.getWindow().getImage(), 0, 0, image.getWidth(), image.getHeight(), null);

        try {
            ImageIO.write(image, imageFormat, file);
            System.out.println("Screen shoot taken");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method updates the user input
     * @param gc the game container object, which has the input object
     */
    private void updateUserInput(GameContainer gc) {
        if ( gc.getInput().isKeyDown(KeyEvent.VK_SPACE) ) {
            population.buildPopulation(gc);
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_B) ) {
            isShowingBackgroundImage = !isShowingBackgroundImage;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_S) ) {
            isShowingCirclesScore = !isShowingCirclesScore;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_Q) ) {
            makeScreenShoot(gc);
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_UP) ) {
            population.getCirclePopulationLimits().addToX(circlesIncrement);
            isShowingText = true;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_DOWN) ) {
            if ( population.getCirclePopulationLimits().getX() > 0 ) {
                population.getCirclePopulationLimits().addToX(-circlesIncrement);
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
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD0) ) {
            indexBackground = 0;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD1) ) {
            indexBackground = 1;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD2) ) {
            indexBackground = 2;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD3) ) {
            indexBackground = 3;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD4) ) {
            indexBackground = 4;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD5) ) {
            indexBackground = 5;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD6) ) {
            indexBackground = 6;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD7) ) {
            indexBackground = 7;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD8) ) {
            indexBackground = 8;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD9) ) {
            indexBackground = 9;
        }
    }

    /**
     * This method updates the color
     * text, for make a more beautiful transition
     * when the texts are updated
     * @param elapsedTime the time elapsed between frames
     */
    private void updateColorText(float elapsedTime) {
        assert elapsedTime != 0;
        int alphaDecrease = (int) (timeToFadeOffTexts / (elapsedTime * 255));

        if ( isShowingText ) {
            if ( textColor.getAlpha() <= alphaDecrease) {
                textColor.setAlpha(255);
                textBoxColor.setAlpha(255);
                textBoxStrokeColor.setAlpha(255);
                isShowingText = false;
            } else {
                textColor.setAlpha(textColor.getAlpha() - 1);
                textBoxColor.setAlpha(textBoxColor.getAlpha() - 1);
                textBoxStrokeColor.setAlpha(textBoxStrokeColor.getAlpha() - 1);
            }
        }
    }

    @Override
    public void update(GameContainer gameContainer, float v) {
        updateUserInput(gameContainer);
        updateBackgroundImage(gameContainer);
        updateColorText(v);

        population.update(gameContainer, v, backgrounds[indexBackground]);

        fitnessImage = BuffersFitnessCalculator.calculateImageFitness(backgrounds[indexBackground].getP(), buffer);
        buffer = populationRenderer.getP();
    }

    /**
     * This method draws the background
     * @param r the renderer object with all drawing methods
     */
    private void drawBackground(Renderer r) {
        if ( isShowingBackgroundImage ) {
            r.drawImage(backgrounds[indexBackground], 0, 0);
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
        r.drawRectangle(5, 5, 350, 75, textBoxStrokeColor.getCode());

        r.drawText("Circles alive: " + population.getCircles().size(),
                10, 10, textColor.getCode());
        r.drawText("Drawn circles: " + (population.getCircles().size() + population.getDiedCircles().size()),
                10, 30, textColor.getCode());
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
        population.drawCircles(populationRenderer, false, false);
        drawBackground(renderer);
        population.drawCircles(renderer, isShowingBackgroundImage, isShowingCirclesScore);
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
