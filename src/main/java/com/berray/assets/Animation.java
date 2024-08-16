package com.berray.assets;

import com.berray.math.Rect;

import java.util.ArrayList;
import java.util.List;

public class Animation {
  private int from;
  private int to;
  private int speed;
  /** true when the animation should be repeated. */
  private boolean loop = false;
  /** true when the animation reverse at the end and play again from end to start. */
  private boolean pingpong = false;
  private List<Rect> frames = new ArrayList<>();

  public Animation from(int from) {
    this.from = from;
    return this;
  }

  public Animation to(int to) {
    this.to = to;
    return this;
  }

  public Animation speed(int speed) {
    this.speed = speed;
    return this;
  }

  public Animation pingpong(boolean pingpong) {
    this.pingpong = pingpong;
    return this;
  }

  public Animation loop(boolean loop) {
    this.loop = loop;
    return this;
  }

  public int getFrom() {
    return from;
  }

  public int getTo() {
    return to;
  }

  public int getSpeed() {
    return speed;
  }

  public boolean isLoop() {
    return loop;
  }

  public boolean isPingpong() {
    return pingpong;
  }

  public int getNumFrames() {
    return frames.size();
  }

  public Rect getFrame(int frameNo) {
    return frames.get(frameNo);
  }

  public static Animation anim() {
    return new Animation();
  }

  public void addFrame(Rect rect) {
    frames.add(rect);
  }
}
