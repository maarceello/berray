package com.berray.components.core;

import com.berray.GameObject;
import com.berray.event.CoreEvents;
import com.berray.event.Event;
import com.berray.event.UpdateEvent;

import java.util.List;

/**
 * Adds keyframe animation to any property which can be interpolated.
 * Note that this component can be added multiple times to a game object, each time with another animated property
 *
 * @param <E> Type of the property which is animated.
 */
public class AnimateComponent<E> extends Component {
  private List<KeyFrame<E>> keyFrames;
  /** true when the animation should be repeated. */
  private boolean loop = false;
  /** true when the animation reverse at the end and play again from end to start. */
  private boolean pingpong = false;

  /** true when the animation is running. */
  private boolean running = false;

  /** Current frame number since the animation started. */
  private int currentFrame = 0;

  /** Currently elapsed time since the animation started. */
  private float currentTime = 0.0f;
  /** Direction the animation is running. Can be 1 and -1. */
  private int animationDirection = 1;
  /**
   * Index of keyframe which is currently playing. If the animation is between 2 keyframes, this is the previous keyframe
   * index. The next keyframe index depends on the direction the animation is playing.
   */
  private int currentKeyFrame;

  public AnimateComponent(String property) {
    super("animate["+property+"]", property);
  }

  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);
    on(CoreEvents.UNBIND, this::processUpdate);
  }

  private  void processUpdate(UpdateEvent e) {
    if (!running) {
      return;
    }

    currentFrame += animationDirection;
    if (currentFrame >= keyFrames.size() || currentFrame < 0 ) {


    }



  }

  /** Represents a key frame, either based on frame counting or on elapsed animation time. */
  public static class KeyFrame<E> {
    private int frame;
    private float time;
    private E value;

    public KeyFrame() {
    }

    public KeyFrame(int frame, float time, E value) {
      this.frame = frame;
      this.time = time;
      this.value = value;
    }

    public int getFrame() {
      return frame;
    }

    public void setFrame(int frame) {
      this.frame = frame;
    }

    public float getTime() {
      return time;
    }

    public void setTime(float time) {
      this.time = time;
    }

    public E getValue() {
      return value;
    }

    public void setValue(E value) {
      this.value = value;
    }
  }
}
