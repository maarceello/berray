package com.berray;

/**
 * Holder for some timings.
 */
public class Timings {
  private TimingsWindow previousWindow = new TimingsWindow();
  private TimingsWindow currentWindow = new TimingsWindow();
  private TimingsWindow currentTimings = new TimingsWindow();

  public float getPercentCollisionDetection() {
    return 100f * (previousWindow.timeCollisionDetection + currentWindow.timeCollisionDetection) /
        (currentWindow.sum + previousWindow.sum);
  }

  public float getPercentUpdate() {
    return 100f * (previousWindow.timeUpdate + currentWindow.timeUpdate) /
        (currentWindow.sum + previousWindow.sum);
  }

  public float getPercentDraw() {
    return 100f * (previousWindow.timeDraw + currentWindow.timeDraw) /
        (currentWindow.sum + previousWindow.sum);
  }

  public float getPercentInput() {
    return 100f * (previousWindow.timeInput + currentWindow.timeInput) /
        (currentWindow.sum + previousWindow.sum);
  }

  public float getPercentRaylib() {
    return 100f * (previousWindow.timeRaylib + currentWindow.timeRaylib) /
        (currentWindow.sum + previousWindow.sum);
  }

  private static long run(Runnable runnable) {
    long start = System.nanoTime();
    runnable.run();
    return System.nanoTime() - start;
  }

  public void timeCollisionDetection(Runnable runnable) {
    long time = run(runnable);
    currentTimings.timeCollisionDetection = time;
    currentTimings.sum += time;
  }

  public void timeUpdate(Runnable runnable) {
    long time = run(runnable);
    currentTimings.timeUpdate = time;
    currentTimings.sum += time;
  }

  public void timeRaylib(Runnable runnable) {
    long time = run(runnable);
    currentTimings.timeRaylib = time;
    currentTimings.sum += time;
  }

  public void timeDraw(Runnable runnable) {
    long time = run(runnable);
    currentTimings.timeDraw = time;
    currentTimings.sum += time;
  }

  public void timeInput(Runnable runnable) {
    long time = run(runnable);
    currentTimings.timeInput = time;
    currentTimings.sum += time;
  }

  /**
   * Apply frame timings to sliding window.
   */
  public void apply() {
    currentWindow.timeCollisionDetection += currentTimings.timeCollisionDetection;
    currentWindow.timeDraw += currentTimings.timeDraw;
    currentWindow.timeUpdate += currentTimings.timeUpdate;
    currentWindow.timeInput += currentTimings.timeInput;
    currentWindow.timeRaylib += currentTimings.timeRaylib;
    currentWindow.sum += currentTimings.sum;
    currentTimings.sum = 0;
    // every 5 seconds move the current window
    if (currentWindow.sum > 10_000_000_000L) {
      previousWindow = currentWindow;
      currentWindow = new TimingsWindow();
    }
  }


  private static class TimingsWindow {
    private long timeCollisionDetection = 0;
    private long timeUpdate = 0;
    private long timeDraw = 0;
    private long timeInput = 0;
    private long timeRaylib = 0;
    private long sum = 0;
  }
}
