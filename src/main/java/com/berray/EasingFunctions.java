package com.berray;

import java.util.function.Function;

public enum EasingFunctions implements Function<Float, Float> {
    LINEAR {
        @Override
        public Float apply(Float progress) {
            return progress;
        }
    };
}
