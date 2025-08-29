package com.berray;

import com.berray.event.CoreEvents;
import com.berray.event.UpdateEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AnimationManager {
    private List<GameObject> animatedObjects = new ArrayList<>();

    public void addAnimation(GameObject gameObject, AnimationData<?> animationData) {
        gameObject.setProperty("animation", animationData);
        animatedObjects.add(gameObject);
    }

    public void animationUpdate(UpdateEvent event) {
        // Process all animating pieces
        Iterator<GameObject> iterator = animatedObjects.iterator();
        while (iterator.hasNext()) {
            GameObject gameObject = iterator.next();
            // get animation data from current game object
            AnimationData<?> animationData = gameObject.getProperty("animation");
            if (animationData == null)  {
                // if the animation data is null, the object is not animated (anymore)
                iterator.remove();
                continue;
            }

            // add frametime to already elapsed time for this animation
            animationData.update(event.getFrametime());

            Object currentValue = animationData.getCurrentValue();
            gameObject.set(animationData.getProperty(), currentValue);

            // when the animation is done, remove the animation from the object and the object from the animation manager
            if (animationData.getProgress() == 1.0f) {
                gameObject.removeProperty("animation");
                gameObject.trigger(CoreEvents.ANIMATION_END, gameObject, animationData.getProperty());
                iterator.remove();
            }
        }
    }

    private float easeOutQuadratic(float t) {
        return 1 - (1 - t) * (1 - t);
    }

    float simplePow(float x, int p)
    {
        float res = 1.0f;
        for (int i = p; i > 0; i--) {
            res *= x;
        }
        return res;
    }

    float easeInOutExponential(float t)
    {
        if (t < 0.5f) {
            return (float) Math.pow(2.0f, 20.0f * t - 10.0f) * 0.5f;
        }
        return (float) (2.0f - Math.pow(2.0f, -20.0f * t + 10.0f)) * 0.5f;
    }

    float easeOutBack(float t)
    {
        final float c1 = 1.70158f;
        final float c3 = c1 + 1.0f;
        return 1.0f + c3 * simplePow(t - 1.0f, 3) + c1 * simplePow(t - 1.0f, 2);
    }

    float easeInOutQuint(float t)
    {
        if (t < 0.5f) {
            return  16.0f * simplePow(t, 5);
        }
        return 1.0f - simplePow(-2.0f * t + 2, 5) * 0.5f;
    }

    float easeInBack(float t)
    {
        final float  c1 = 1.70158f;
        final float  c3 = c1 + 1.0f;
        return c3 * t * t * t - c1 * t * t;
    }

    float easeOutElastic(float t)
    {
        final float  two_pi = 2.0f * 3.14159265359f;
        final float  c4 = two_pi / 3.0f;
        if (t == 0.0f) {
            return 0.0f;
        }
        if (t == 1.0f) {
            return 1.0f;
        }
        return (float) (Math.pow(2.0f, -10.0f * t) * Math.sin((t * 10.0f - 0.75f) * c4) + 1.0f);
    }
}
