package com.berray;

import java.util.function.Function;

/**
 * Easing functions. From <a href="https://easings.net/">https://easings.net/</a>.
 */
public enum EasingFunctions implements Function<Float, Float> {
    LINEAR {
        @Override
        public Float apply(Float progress) {
            return progress;
        }
    },
    EASE_OUT_QUADRATIC {
        @Override
        public Float apply(Float t) {
            return 1 - (1 - t) * (1 - t);
        }
    },
    EASE_IN_OUT_EXPONENTIAL {
        @Override
        public Float apply(Float t) {
            if (t < 0.5f) {
                return (float) Math.pow(2.0f, 20.0f * t - 10.0f) * 0.5f;
            }
            return (float) (2.0f - Math.pow(2.0f, -20.0f * t + 10.0f)) * 0.5f;
        }
    },
    EASE_OUT_BACK {
        @Override
        public Float apply(Float t) {
            final float c1 = 1.70158f;
            final float c3 = c1 + 1.0f;
            return 1.0f + c3 * simplePow(t - 1.0f, 3) + c1 * simplePow(t - 1.0f, 2);
        }
    },

    EASE_IN_OUT_QUINT {
        @Override
        public Float apply(Float t) {
            if (t < 0.5f) {
                return  16.0f * simplePow(t, 5);
            }
            return 1.0f - simplePow(-2.0f * t + 2, 5) * 0.5f;
        }
    },


    EASE_IN_BACK {
        @Override
        public Float apply(Float t) {
            final float  c1 = 1.70158f;
            final float  c3 = c1 + 1.0f;
            return c3 * t * t * t - c1 * t * t;        }
    },
    EASE_OUT_ELASTIC {
        @Override
        public Float apply(Float t) {
            final float  c4 = (float) (2.0f * Math.PI) / 3.0f;
            if (t == 0.0f) {
                return 0.0f;
            }
            if (t == 1.0f) {
                return 1.0f;
            }
            return (float) (Math.pow(2.0f, -10.0f * t) * Math.sin((t * 10.0f - 0.75f) * c4) + 1.0f);
        }
    };


    private static float simplePow(float x, int p)
    {
        float res = 1.0f;
        for (int i = p; i > 0; i--) {
            res *= x;
        }
        return res;
    }

    }
