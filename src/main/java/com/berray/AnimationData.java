package com.berray;

import java.util.function.BiFunction;
import java.util.function.Function;

public class AnimationData<T> {
    private final float duration;
    private final T start;
    private final T delta;
    private final String property;

    private final Function<Float, Float> easingFunction = EasingFunctions.LINEAR;
    private final BiFunction<T, Float, T> scale;
    private final BiFunction<T, T, T> add;

    private float elapsedTime;
    private float progress;
    private T currentDelta;
    private T currentValue;


    public AnimationData(String property, T start, T end, float duration, BiFunction<T, Float, T> scale, BiFunction<T, T, T> add, BiFunction<T, T, T> sub) {
        this.start = start;
        this.delta = sub.apply(end, start);
        this.duration = duration;
        this.property = property;

        this.scale = scale;
        this.add = add;

        this.elapsedTime = 0;
    }

    public void update(float frameTime) {
        // add frame time to elapsed time. Clamp at 1.0f, which is the end of the animation.
        this.elapsedTime = Math.min(1.0f, this.elapsedTime + frameTime);
        this.progress = easingFunction.apply(Math.min(1f, elapsedTime / duration));
        this.currentDelta = scale.apply(delta, getProgress());
        this.currentValue = add.apply(start, currentDelta);
    }

    public String getProperty() {
        return property;
    }

    public float getProgress() {
        return progress;
    }

    public T getCurrentDelta() {
        return currentDelta;
    }

    public T getCurrentValue() {
        return currentValue;
    }

}
