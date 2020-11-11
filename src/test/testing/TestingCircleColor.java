package testing;

import circlesimage.CircleColor;
import engine.AbstractGame;
import engine.GameContainer;
import engine.gfx.HexColors;
import engine.gfx.Renderer;

import java.awt.event.KeyEvent;

/**
 * This class is for testing the class CircleColor
 *
 * @class TestingCircleColor
 * @autor Sergio Martí Torregrosa
 * @date 05/11/2020
 */
public class TestingCircleColor extends AbstractGame {

    private CircleColor color1 = new CircleColor(255, 255, 255, 255);

    private CircleColor color2 = new CircleColor(100, 100, 100, 100);

    private double similarity;

    public TestingCircleColor(String title) {
        super(title);
    }

    @Override
    public void initialize(GameContainer gameContainer) {
        similarity = color1.getSimilarity(color2);
    }

    @Override
    public void update(GameContainer gameContainer, float v) {
        if ( gameContainer.getInput().isKeyHeld(KeyEvent.VK_UP) ) {
            if ( color2.getRed() < 255 && color2.getGreen() < 255 && color2.getBlue() < 255 && color2.getAlpha() < 255 ) {
                color2.setRed(color2.getRed() + 1);
                color2.setGreen(color2.getGreen() + 1);
                color2.setBlue(color2.getBlue() + 1);
                color2.setAlpha(color2.getAlpha() + 1);
            }
        }
        if ( gameContainer.getInput().isKeyHeld(KeyEvent.VK_DOWN) ) {
            if ( color2.getRed() > 0 && color2.getGreen() > 0 && color2.getBlue() > 0 && color2.getAlpha() > 0 ) {
                color2.setRed(color2.getRed() - 1);
                color2.setGreen(color2.getGreen() - 1);
                color2.setBlue(color2.getBlue() - 1);
                color2.setAlpha(color2.getAlpha() - 1);
            }
        }

        similarity = color1.getSimilarity(color2);
    }

    @Override
    public void render(GameContainer gameContainer, Renderer renderer) {
        renderer.drawText(String.format("Similarity: %.3f%%", similarity * 100), 10, 10, HexColors.WHITE);
        renderer.drawFillRectangle(10, 40, 30, 30, color1.getCode());
        renderer.drawRect(10, 40, 30, 30, HexColors.WHITE);
        renderer.drawFillRectangle(10, 70, 30, 30, color2.getCode());
        renderer.drawRect(10, 70, 30, 30, HexColors.WHITE);
    }

    public static void main(String[] args) {
        GameContainer gc = new GameContainer(new TestingCircleColor("Testing circle color"));
        gc.start();
    }

}
