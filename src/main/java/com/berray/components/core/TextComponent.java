package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.raylib.Jaylib;
import com.raylib.Raylib;

import java.util.function.Consumer;

import static com.raylib.Jaylib.*;

public class TextComponent extends Component {
    private String text;
    private int fontHeight = 20;
    private int width;

    public TextComponent(String text) {
        super("text");
        setText(text);
    }

    public void setText(String text) {
        this.text = text;
        int newWidth = MeasureText(text, fontHeight);
        if (newWidth != width && gameObject != null) {
            // when the width of the text changes, recalculate transform (as the component might
            // be moved when center or right aligned)
            gameObject.setTransformDirty();
        }
        this.width = newWidth;
    }

    public String getText() {
        return text;
    }

    private Vec2 getSize() {
        return new Vec2(width, fontHeight);
    }

    @Override
    public void draw() {
        rlPushMatrix();
        {
            Raylib.Color color = gameObject.getOrDefault("color", Jaylib.BLACK);
            rlMultMatrixf(gameObject.getWorldTransform().toFloatTransposed());
            DrawText(text, 0, 0, fontHeight, color);
        }
        rlPopMatrix();
    }

    @Override
    public void add(GameObject gameObject) {
        registerGetter("size", this::getSize);
        registerGetter("render", () -> true);
        registerBoundProperty("text", this::getText, this::setText);
    }

    public static TextComponent text(String text) {
        return new TextComponent(text);
    }
}
