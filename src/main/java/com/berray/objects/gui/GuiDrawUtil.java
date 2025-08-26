package com.berray.objects.gui;

import com.berray.math.Color;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.raylib.Raylib;

public class GuiDrawUtil {

    public static void drawBox(Rect bounds, Color baseColor, Color highlightColor) {
        Raylib.DrawRectangleRounded(bounds.toRectangle(), 0.5f, 3, baseColor.toRaylibColor());
        Raylib.DrawRectangleRoundedLines(bounds.reduce(5).toRectangle(), 0.5f, 3, highlightColor.toRaylibColor());
    }

    public static void drawText(Rect bounds, Vec2 pos, String text, Color baseColor, Color highlightColor) {

    }

}
