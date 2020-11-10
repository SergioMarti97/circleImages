package testing;

import circlesimage.CircleColor;
import circlesimage.CircleImage;
import engine.AbstractGame;
import engine.GameContainer;
import engine.gfx.HexColors;
import engine.gfx.Renderer;
import engine.vectors.points2d.Vec2df;

import java.util.ArrayList;

public class TestingCirclesCollision extends AbstractGame {

    private ArrayList<CircleImage> circles;

    private TestingCirclesCollision(String title) {
        super(title);
    }

    private int randomIntBetween(int max, int min) {
        return (int)(Math.random() * (max - min)) + min;
    }

    private CircleImage buildRandomCircleImage(GameContainer gc) {
        return new CircleImage(
                0,
                new Vec2df(
                        randomIntBetween(gc.getWidth(), 0),
                        randomIntBetween(gc.getHeight(), 0)
                ),
                randomIntBetween(100, 10),
                new CircleColor(
                        randomIntBetween(255, 0),
                        randomIntBetween(255, 0),
                        randomIntBetween(255, 0),
                        255
                ),
                0
        );
    }

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
        circles = buildRandomCircleImageArray(gameContainer, 10);
    }

    private boolean doCirclesOverlap(CircleImage f, CircleImage s) {
        return Math.abs((f.getPosition().getX() - s.getPosition().getX()) * (f.getPosition().getX() - s.getPosition().getX()) +
                        (f.getPosition().getY() - s.getPosition().getY()) * (f.getPosition().getY() - s.getPosition().getY()))
                <= (f.getSize() + s.getSize()) * (f.getSize() + s.getSize());
    }

    private void updateCircleTargetOverlap(CircleImage c, CircleImage t) {
        if ( c.getId() != t.getId() ) {
            if ( doCirclesOverlap(c, t) ) {

                float dist = (float)Math.sqrt(
                        (c.getPosition().getX() - t.getPosition().getX()) * (c.getPosition().getX() - t.getPosition().getX()) +
                                (c.getPosition().getY() - t.getPosition().getY()) * (c.getPosition().getY() - t.getPosition().getY())
                );

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

    private void updateCirclesOverlap() {
        for ( CircleImage c : circles ) {
            for ( CircleImage t : circles ) {
                updateCircleTargetOverlap(c, t);
            }
        }
    }

    @Override
    public void update(GameContainer gameContainer, float v) {
        CircleImage c = circles.get(circles.size() - 1);
        c.setPosition(new Vec2df(
                gameContainer.getInput().getMouseX(),
                gameContainer.getInput().getMouseY()
        ));
        updateCirclesCollisionEdges(gameContainer);
        updateCirclesOverlap();
    }

    private void drawAllCircles(Renderer r) {
        for ( CircleImage c : circles ) {
            r.drawFillCircle((int)c.getPosition().getX(), (int)c.getPosition().getY(), (int)c.getSize(), c.getColor().getCode());
            r.drawCircle((int)c.getPosition().getX(), (int)c.getPosition().getY(), (int)c.getSize(), HexColors.WHITE);
        }
    }

    @Override
    public void render(GameContainer gameContainer, Renderer renderer) {
        drawAllCircles(renderer);
    }

    public static void main(String[] args) {
        GameContainer gc = new GameContainer(new TestingCirclesCollision("Circle static collision test"));
        gc.start();
    }

}
