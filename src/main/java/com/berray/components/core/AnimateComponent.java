package com.berray.components.core;

import com.berray.GameObject;
import com.berray.event.CoreEvents;
import com.berray.event.UpdateEvent;
import com.berray.math.Vec2;

import java.util.ArrayList;
import java.util.List;

import static com.berray.event.CoreEvents.ANIMATION_END;

/**
 * Adds keyframe animation to any property which can be interpolated.
 * Note that this component can be added multiple times to a game object, each time with another animated property
 *
 * @param <E> Type of the property which is animated.
 */
public class AnimateComponent<E> extends Component {
  /**
   * The animated property.
   */
  private final String property;
  private List<KeyFrame<E>> keyFrames = new ArrayList<>();
  /**
   * true when the animation should be repeated.
   */
  private boolean loop = false;
  /**
   * true when the animation should be cyclic (repeated backwards).
   */
  private boolean pingPong = false;


  /**
   * true when the animation is running.
   */
  private boolean running = true;

  /**
   * Currently elapsed time since the animation started.
   */
  private float currentFrameTime = 0.0f;
  /**
   * Index of keyframe which is currently playing. If the animation is between 2 keyframes, this is the previous keyframe
   * index. The next keyframe index depends on the direction the animation is playing.
   */
  private int currentKeyFrameIndex = -1;
  /**
   * Function to use when two keyframes should be interpolated.
   */
  private InterpolateFunction<E> interpolateFunction;

  public AnimateComponent(String property) {
    super("animate[" + property + "]", property);
    this.property = property;
  }

  public AnimateComponent(String property, boolean loop, boolean pingPong, InterpolateFunction<E> interpolateFunction, List<KeyFrame<E>> keyFrames) {
    super("animate[" + property + "]", property);
    this.property = property;
    this.keyFrames = keyFrames;
    this.loop = loop;
    this.pingPong = pingPong;
    this.interpolateFunction = interpolateFunction;
  }

  @Override
  public boolean allowMultiple() {
    return true;
  }

  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);
    on(CoreEvents.UPDATE, this::processUpdate);

    // copy key frame list
    // when ping pong: duplicate keyframes in reverse order, adjust frame counter and frame times
    List<KeyFrame<E>> finalKeyFrames = new ArrayList<>(keyFrames);
    if (pingPong) {
      float lastTime = keyFrames.get(keyFrames.size() - 1).time;
      for (int i = keyFrames.size() - 1; i >= 1; i--) {
        float delta = keyFrames.get(i).time - keyFrames.get(i - 1).time;
        lastTime += delta;
        finalKeyFrames.add(new KeyFrame<>(lastTime, keyFrames.get(i - 1).value));
      }
    }
    this.keyFrames = finalKeyFrames;
  }

  public AnimateComponent<E> loop() {
    return loop(true);
  }

  public AnimateComponent<E> loop(boolean loop) {
    checkConfigurationAllowed();
    this.loop = loop;
    return this;
  }

  public AnimateComponent<E> pingPong() {
    return pingPong(true);
  }

  public AnimateComponent<E> pingPong(boolean pingPong) {
    checkConfigurationAllowed();
    this.pingPong = pingPong;
    return this;
  }

  public AnimateComponent<E> interpolateWith(InterpolateFunction<E> interpolateFunction) {
    checkConfigurationAllowed();
    if (this.interpolateFunction != null) {
      throw new IllegalStateException("interpolate function already set to " + this.interpolateFunction);
    }
    this.interpolateFunction = interpolateFunction;
    return this;
  }

  public AnimateComponent<E> keyFrame(float time, E value) {
    checkConfigurationAllowed();
    this.keyFrames.add(new KeyFrame<>(time, value));
    return this;
  }

  public AnimateComponent<E> keyFrames(List<KeyFrame<E>> keyFrames) {
    checkConfigurationAllowed();
    this.keyFrames.addAll(keyFrames);
    return this;
  }


  private void processUpdate(UpdateEvent e) {
    if (!running) {
      return;
    }

    if (currentKeyFrameIndex < 0) {
      // start the animation
      currentKeyFrameIndex = 0;
      currentFrameTime = 0;
      return;
    }

    currentFrameTime += e.getFrametime();
    // is the current frametime after the last frame?
    if (currentFrameTime > keyFrames.get(keyFrames.size() - 1).time) {
      // single run only?
      if (!loop) {
        // then stop the animation
        currentFrameTime = 0;
        currentKeyFrameIndex = -1;
        running = false;
        emitAnimationEndEvent();
        return;
      }
      // after the last frame and looping: reset animation, but keep the time by which the animation was overflown
      currentKeyFrameIndex = 0;
      currentFrameTime = currentFrameTime % keyFrames.get(keyFrames.size() - 1).time;
    }

    // currentFrameTime is in the animation bounds. Find current and next frames, starting at currentFrameIndex
    KeyFrame<E> currentKeyFrame = keyFrames.get(currentKeyFrameIndex);
    KeyFrame<E> nextKeyFrame;
    do {
      nextKeyFrame = keyFrames.get(currentKeyFrameIndex + 1);
      if (currentFrameTime > currentKeyFrame.time && currentFrameTime < nextKeyFrame.time) {
        break;
      }
      currentKeyFrameIndex += 1;
      currentKeyFrame = nextKeyFrame;
    } while (true);

    // get percent of current frame time between the key frames
    float start = currentKeyFrame.time;
    float end = nextKeyFrame.time;
    float percent = (currentFrameTime - start) / (end - start);

    // interpolate the keyframes
    E interpolatedValue = interpolateFunction.interpolate(currentKeyFrame.value, nextKeyFrame.value, percent);
    // set destination value
    gameObject.set(property, interpolatedValue);
  }

  /**
   * Fired when the collision is resolved. Note: the event is fired for both parties of the collision.
   *
   * @type emit-event
   */
  private void emitAnimationEndEvent() {
    gameObject.trigger(ANIMATION_END, gameObject, property);
  }

  public static AnimateComponent<Float> animateFloat(String property, boolean loop, boolean pingPong, List<KeyFrame<Float>> keyFrames) {
    return animate(property, (a, b, ratio) -> (a + (b - a) * ratio), loop, pingPong, keyFrames);
  }

  public static AnimateComponent<Integer> animateInt(String property, boolean loop, boolean pingPong, List<KeyFrame<Integer>> keyFrames) {
    return animate(property, (a, b, ratio) -> (int) (a + (b - a) * ratio), loop, pingPong, keyFrames);
  }

  public static AnimateComponent<Vec2> animateVec2(String property, boolean loop, boolean pingPong, List<KeyFrame<Vec2>> keyFrames) {
    return animateVec2(property)
        .interpolateWith(Vec2::linearInterpolate)
        .loop(loop)
        .pingPong(pingPong)
        .keyFrames(keyFrames);
  }

  public static AnimateComponent<Vec2> animateVec2(String property) {
    return new AnimateComponent<Vec2>(property)
        .interpolateWith(Vec2::linearInterpolate);
  }

  public static <E> AnimateComponent<E> animate(String property, InterpolateFunction<E> interpolateFunction) {
    return new AnimateComponent<E>(property)
        .interpolateWith(interpolateFunction);
  }

  public static <E> AnimateComponent<E> animate(String property, InterpolateFunction<E> interpolateFunction, boolean loop, boolean pingPong, List<KeyFrame<E>> keyFrames) {
    return new AnimateComponent<>(property, loop, pingPong, interpolateFunction, keyFrames);
  }


  /**
   * Represents a key frame, either based on frame counting or on elapsed animation time.
   */
  public static class KeyFrame<E> {
    private final float time;
    private final E value;

    public KeyFrame(float time, E value) {
      this.time = time;
      this.value = value;
    }

    public float getTime() {
      return time;
    }

    public E getValue() {
      return value;
    }
  }

  /**
   * Function which linearly interpolates between 2 values.
   */
  public interface InterpolateFunction<E> {
    E interpolate(E value1, E value2, float percent);
  }
}
