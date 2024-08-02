package com.berray.assets;

import com.berray.math.Rect;

import java.util.ArrayList;
import java.util.List;

public class Animation {
  private int from;
  private int to;
  private int speed;
  private boolean loop;
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

  public int getNumFrames() {
    return (to - from) + 1;
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
