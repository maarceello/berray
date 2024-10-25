package com.berray.tests.gui;

import com.berray.BerrayApplication;
import com.berray.GameObject;
import com.berray.components.CoreComponentShortcuts;
import com.berray.components.core.AnchorType;
import com.berray.event.CoreEvents;
import com.berray.event.MouseEvent;
import com.berray.event.PropertyChangeEvent;
import com.berray.math.Color;
import com.berray.math.Vec2;

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

        on(CoreEvents.MOUSE_PRESS, (MouseEvent event) -> {
            Vec2 pos = event.getWindowPos();
            testobject.set("pos", pos);
        });

        testobject.on(CoreEvents.PROPERTY_CHANGED, (PropertyChangeEvent event) -> {
            text.set("text", event.getPropertyName()+": "+event.getNewValue());
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
