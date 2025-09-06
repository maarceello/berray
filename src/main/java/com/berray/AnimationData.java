package com.berray;

import java.util.function.BiFunction;
import java.util.function.Function;

public class AnimationData<T> {
    private final float duration;
    private final T start;
    private final T end;
    private final T delta;
    private final String property;

    private final Function<Float, Float> easingFunction;
    private final AnimationValueType<T> valueType;

    private float elapsedTime;
    private float progress;
    private T currentDelta;
    private T currentValue;

    public AnimationData(String property, T start, T end, float duration, Function<Float, Float> easingFunction, AnimationValueType<T> valueType) {
        this.start = start;
        this.end = end;
        this.valueType = valueType;
        this.delta = valueType.getSub().apply(end, start);
        this.duration = duration;
        this.property = property;
        this.easingFunction = easingFunction;

        this.elapsedTime = 0;
    }


    public AnimationData(String property, T start, T end, float duration, Function<Float, Float> easingFunction, BiFunction<T, Float, T> scale, BiFunction<T, T, T> add, BiFunction<T, T, T> sub) {
        this(property, start, end, duration, easingFunction, new AnimationValueType<>(scale, add, sub));
    }

    public void update(float frameTime) {
        // add frame time to elapsed time. Clamp at 1.0f, which is the end of the animation.
        this.elapsedTime = this.elapsedTime + frameTime;
        this.progress = easingFunction.apply(Math.min(1f, elapsedTime / duration));
        this.currentDelta = valueType.getScale().apply(delta, getProgress());
        this.currentValue = valueType.getAdd().apply(start, currentDelta);
    }

    public String getProperty() {
        return property;
    }

    public float getProgress() {
        return progress;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public float getDuration() {
        return duration;
    }

    public T getCurrentDelta() {
        return currentDelta;
    }

    public T getCurrentValue() {
        return currentValue;
    }

    public T getEnd() {
        return end;
    }

    public T getStart() {
        return start;
    }

    public T getDelta() {
        return delta;
    }
}
