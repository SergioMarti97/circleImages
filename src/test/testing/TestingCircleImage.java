package testing;

import circlesimage.CircleColor;
import circlesimage.CircleImage;
import engine.AbstractGame;
import engine.GameContainer;
import engine.gfx.HexColors;
import engine.gfx.Renderer;
import engine.gfx.images.Image;
import engine.vectors.points2d.Vec2df;

import java.awt.event.KeyEvent;

public class TestingCircleImage extends AbstractGame {

    private Image backgroundImage;

    private CircleImage circle;

    private Vec2df mousePosition;

    private TestingCircleImage(String title) {
        super(title);
    }

    @Override
    public void initialize(GameContainer gameContainer) {
        backgroundImage = new Image("/dynastes_hercules.jpg");
        mousePosition = new Vec2df(gameContainer.getInput().getMouseX(), gameContainer.getInput().getMouseY());
        circle = new CircleImage(0, mousePosition, 10, new CircleColor(0, 0, 0, 255), 0);
    }

    @Override
    public void update(GameContainer gameContainer, float v) {

        if ( gameContainer.getInput().isKeyDown(KeyEvent.VK_UP) ) {
            if ( circle.getSize() <= 500 ) {
                circle.setSize(circle.getSize() + 1);
            }
        }

        if ( gameContainer.getInput().isKeyDown(KeyEvent.VK_DOWN) ) {
            if ( circle.getSize() >= 1 ) {
                circle.setSize(circle.getSize() - 1);
            }
        }

        mousePosition = new Vec2df(gameContainer.getInput().getMouseX(), gameContainer.getInput().getMouseY());
        circle.setPosition(mousePosition);
    }

    @Override
    public void render(GameContainer gameContainer, Renderer renderer) {
        renderer.drawImage(backgroundImage, 0, 0);
        renderer.drawFillCircle((int)circle.getPosition().getX(), (int)circle.getPosition().getY(), (int)circle.getSize(), circle.getColor().getCode());
        circle.calculateScore(backgroundImage);
        renderer.drawText(String.format("Score: %f", circle.getScore()), 10, 10, HexColors.WHITE);
        circle.setScore(0);
    }

    public static void main(String[] args) {
        GameContainer gc = new GameContainer(new TestingCircleImage("Testing circle image"));
        gc.start();
    }

}
