package com.berray;

import com.berray.event.CoreEvents;
import com.berray.event.UpdateEvent;
import com.berray.math.Vec2;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnimationManager {
    private List<GameObject> animatedObjects = new ArrayList<>();

    private Map<Class<?>, AnimationValueType<?>> animationValueTypes = new HashMap<>();

    public AnimationManager() {
        this.animationValueTypes.put(Integer.class, new AnimationValueType<Integer>(
                (a, scale) -> (int) (a * scale),
                (a, b) -> a + b,
                (a, b) -> a - b));
        this.animationValueTypes.put(Float.class, new AnimationValueType<Float>(
                (f, scale) -> f * scale,
                (a, b) -> a + b,
                (a, b) -> a - b
        ));
        this.animationValueTypes.put(Vec2.class, new AnimationValueType<Vec2>(Vec2::scale, Vec2::add, Vec2::sub));
    }

    @SuppressWarnings("unchecked")
    public <E> void addAnimation(GameObject gameObject, String property, E newValue, float duration, Function<Float, Float> easingFunction) {
        if (newValue == null) {
            throw new NullPointerException("newValue must not be null");
        }
        AnimationValueType<E> newValueType = (AnimationValueType<E>) animationValueTypes.get(newValue.getClass());
        if (newValueType == null) {
            throw new IllegalStateException("type of newValue ("+newValue.getClass().getName()+") is not registered. Either register the type or use the AnimationData constructor.");
        }
        // get current value from game object
        E current = gameObject.get(property);
        addAnimation(gameObject, new AnimationData<>(property,current, newValue, duration, easingFunction, newValueType));
    }

    public void addAnimation(GameObject gameObject, AnimationData<?> animationData) {
        gameObject.setProperty("animation_"+animationData.getProperty(), animationData);
        animatedObjects.add(gameObject);
    }

    public void animationUpdate(UpdateEvent event) {
        // Process all animating pieces
        Iterator<GameObject> iterator = animatedObjects.iterator();
        while (iterator.hasNext()) {
            GameObject gameObject = iterator.next();
            Set<String> animations = gameObject.getPropertyNames().stream().filter(name -> name.startsWith("animation_")).collect(Collectors.toSet());
            // check if the animation is done
            if (animations.isEmpty()) {
                // yes. remove the game object from the animation manager and continue with the next game object
                iterator.remove();
                continue;
            }
            for (String animationProperty : animations) {
                // get animation data from current game object
                AnimationData<?> animationData = gameObject.getProperty(animationProperty);
                if (animationData == null) {
                    // if the animation data is null, the object is not animated (anymore)
                    iterator.remove();
                    continue;
                }

                // add frametime to already elapsed time for this animation
                animationData.update(event.getFrametime());

                Object currentValue = animationData.getCurrentValue();
                gameObject.set(animationData.getProperty(), currentValue);

                // when the animation is done, remove the animation from the object
                if (animationData.getElapsedTime() / animationData.getDuration() >= 1.0f) {
                    // remove the animation property from the game object
                    gameObject.removeProperty(animationProperty);
                    // make sure that the property has exactly the end value
                    gameObject.set(animationData.getProperty(), animationData.getEnd());
                    // notify the game object that the animation is done
                    gameObject.trigger(CoreEvents.ANIMATION_END, gameObject, animationData.getProperty());
                    // Note: when this is the last animation of the game object, the update in the next frame removes the
                    // game object from the animation manager
                }
            }
        }
    }
}
