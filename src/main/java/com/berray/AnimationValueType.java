package com.berray;

import java.util.function.BiFunction;

/** Stores math function for the animated values. */
public class AnimationValueType<T> {
    private final BiFunction<T, Float, T> scale;
    private final BiFunction<T, T, T> add;
    private final BiFunction<T, T, T> sub;

    public AnimationValueType(BiFunction<T, Float, T> scale, BiFunction<T, T, T> add, BiFunction<T, T, T> sub) {
        this.scale = scale;
        this.add = add;
        this.sub = sub;
    }

    public BiFunction<T, Float, T> getScale() {
        return scale;
    }

    public BiFunction<T, T, T> getAdd() {
        return add;
    }

    public BiFunction<T, T, T> getSub() {
        return sub;
    }
}
