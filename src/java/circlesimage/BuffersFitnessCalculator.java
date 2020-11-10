package circlesimage;

/**
 * This class contains static methods
 * for calculate the fitness between to
 * buffers of pixels
 * There are multiple ways to do this
 *
 * @class Fitness calculator
 * @author Sergio Martí Torregrosa
 * @date 10/11/2020
 */
public class BuffersFitnessCalculator {

    /**
     * This method calculates the fitness of two
     * integer arrays (each integer represent a hex code
     * color), with a simple linear loop
     * For each pixel, it calculates the similarity
     * between the two colors
     * @param back the background buffer
     * @param front the the front buffer
     * @return the fitness of the front image with the back image
     */
    public static double calculateImageFitness(int[] back, int[] front) {
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

}
