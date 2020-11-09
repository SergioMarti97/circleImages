package circlesimage;

/**
 * This class collects all the code referred for
 * the color of the circles <class>CircleImage</class>
 *
 * This is done because the colors on the screen are
 * hex values. To make a new color from other, firstly
 * is needed to decompose the color in its four channels
 * (red, green, blue and alpha, all values from 0 to 255)
 * Before, the channels are modified and finally the new
 * color is build turning the four channels in a hex code
 *
 * This is needed because the engine what is used
 * in this approach doesn't have a class which manages
 * the colors
 *
 * @class CircleColor
 * @author Sergio Martí Torregrosa
 * @date 5/11/2020
 */
public class CircleColor {

    /**
     * The red chanel
     */
    private int red;

    /**
     * The green chanel
     */
    private int green;

    /**
     * The blue channel
     */
    private int blue;

    /**
     * The alpha channel
     */
    private int alpha;

    /**
     * Constructor full parametrized
     * @param red chanel
     * @param green channel
     * @param blue channel
     * @param alpha channel
     */
    public CircleColor(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    /**
     * Constructor
     * @param red channel
     * @param green channel
     * @param blue channel
     */
    public CircleColor(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    /**
     * Constructor with the hexadecimal code of the color
     * @param hexCode the hex code of the color
     */
    public CircleColor(int hexCode) {
        alpha = ((hexCode >> 24) & 0xff);
        red = ((hexCode >> 16) & 0xff);
        green = ((hexCode >> 8) & 0xff);
        blue = (hexCode & 0xff);
    }

    /**
     * Copy constructor
     * @param color the instance of this class from the information is copied
     */
    public CircleColor(CircleColor color) {
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
        this.alpha = color.getAlpha();
    }

    /**
     * Void constructor
     */
    public CircleColor() {
        red = 0;
        green = 0;
        blue = 0;
        alpha = 0;
    }

    /**
     * This method is really important, because
     * builds the new hex code of the color
     * @return the hex code for this color
     */
    public int getCode() {
        return (alpha << 24 | red << 16 | green << 8 | blue);
    }

    /**
     * This method also is really important, because
     * measures the similarity with other color
     * It doesn't have in account the alpha channel
     * @param color the color to compare
     * @return the score which measures the similarity with the other color
     */
    public double getSimilarity(CircleColor color) {
        double similarityR = 1 - ((red - color.getRed()) * (red - color.getRed()) / (255.0 * 255.0));
        double similarityG = 1 - ((green - color.getGreen()) * (green - color.getGreen()) / (255.0 * 255.0));
        double similarityB = 1 - ((blue - color.getBlue()) * (blue - color.getBlue()) / (255.0 * 255.0));

        return  (similarityR + similarityG + similarityB) / 3.0;
    }

    /**
     * This method also is really important, because
     * measures the similarity with other color
     * It has in account the alpha channel
     * @param color the color to compare
     * @return the score which measures the similarity with the other color
     */
    public double getSimilarityAlphaAlso(CircleColor color) {
        double similarityR = 1 - ((red - color.getRed()) * (red - color.getRed()) / (255.0 * 255.0));
        double similarityG = 1 - ((green - color.getGreen()) * (green - color.getGreen()) / (255.0 * 255.0));
        double similarityB = 1 - ((blue - color.getBlue()) * (blue - color.getBlue()) / (255.0 * 255.0));
        double similarityA = 1 - ((alpha - color.getAlpha()) * (alpha - color.getAlpha()) / (255.0 * 255.0));

        return  (similarityR + similarityG + similarityB + similarityA) / 4.0;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    @Override
    public String toString() {
        return red + "r " + green + "g " + blue + "b " + alpha + "a";
    }

}
