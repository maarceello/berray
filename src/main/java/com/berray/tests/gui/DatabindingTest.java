package com.berray.tests.gui;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.raylib.Jaylib;

public class DatabindingTest extends BerrayApplication implements CoreComponentShortcuts {
    @Override
    public void game() {

        GameObject testobject = add(
                rect(100,100),
                pos(center()),
                "testobject"
        );

        GameObject text = add(
                pos(0, 0),
                anchor(AnchorType.TOP_LEFT),
                text("foobar")
        );

        on("mousePress", event -> {
            Vec2 pos = event.getParameter(0);
            testobject.set("pos", pos);
        });

        testobject.on("propertyChange", event -> {
            text.set("text", event.getParameter(0)+": "+event.getParameter(1));
        });

    }

    @Override
    public void initWindow() {
        width(500);
        height(500);
        background(Color.GRAY);
        title("Databinding Test");
    }

    public static void main(String[] args) {
        new DatabindingTest().runGame();
    }
}
