package com.berray.tests;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.Component;
import com.berray.math.Color;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.berray.objects.gui.GuiDrawUtil;
import com.raylib.Raylib;

import java.util.function.Consumer;

public class RayGuiTest extends BerrayApplication implements CoreComponentShortcuts {

    private GuiRenderer guiRenderer = new GuiRenderer();

    @Override
    public void game() {
        addFpsLabel();
        addTimingsLabel();

        Raylib.GuiSetStyle(Raylib.DEFAULT, Raylib.TEXT_SIZE, 40);

        add(
                new MyComponent(this::drawButton),
                pos(100, 100),
                size(300, 100),
                color(Color.GOLD),
                property("text", "Foobar")
        );
    }

    @Override
    public void initWindow() {
        width(1024);
        height(1024);
        background(Color.GRAY);
        title("RayGuiTest");
    }

    private SizeComponent size(int width, int height) {
        SizeComponent component = new SizeComponent();
        component.setSize(new Vec2(width, height));
        return component;
    }

    public static void main(String[] args) {
        new RayGuiTest().runGame();
    }

    private static class SizeComponent extends Component {
        private Vec2 size;

        public SizeComponent() {
            super("size");
        }

        @Override
        public void add(GameObject gameObject) {
            super.add(gameObject);
            registerBoundProperty("size", this::getSize, this::setSize);
        }

        public Vec2 getSize() {
            return size;
        }

        public void setSize(Vec2 size) {
            this.size = size;
        }
    }


    private static class MyComponent extends Component {

        private final Consumer<GameObject> drawFunction;

        public MyComponent(Consumer<GameObject> drawFunction) {
            super("MyComponent");
            this.drawFunction = drawFunction;
        }

        @Override
        public void add(GameObject gameObject) {
            super.add(gameObject);
            registerGetter("render", () -> true);
        }

        @Override
        public void draw() {
            drawFunction.accept(gameObject);
        }
    }


    private void drawButton(GameObject gameObject) {
        Vec2 size = gameObject.getOrDefault("size", new Vec2(1, 1));
        guiRenderer.drawButton(new Rect(0, 0, size.getX(), size.getY()), gameObject.getProperty("text"));
    }


    private static class GuiRenderer {
        private Color highlightColor = Color.GOLD;
        private Color baseColor = Color.GOLD.darker(0.7f);
        private int fontSize = 20;

        private void drawButton(Rect bounds, String text) {
            GuiDrawUtil.drawBox(bounds, baseColor, highlightColor);

            // get font width and height
            int textWidth = Raylib.MeasureText(text, fontSize);

            //Raylib.DrawText(text, (int) (bounds.getX() + bounds.getHeight()/2), (int) (bounds.getY() + bounds.getWidth()/2), fontSize, Color.BLACK.toRaylibColor());
            //Raylib.DrawText(text, (int) (bounds.getHeight()/2), (int) (bounds.getWidth()/2), fontSize, Color.BLACK.toRaylibColor());
            Raylib.DrawText(text, 0, 0, fontSize, Color.BLACK.toRaylibColor());
        }
    }

}
