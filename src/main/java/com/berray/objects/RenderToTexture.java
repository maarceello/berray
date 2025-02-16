package com.berray.objects;

import com.berray.Game;
import com.berray.GameObject;
import com.berray.math.Color;
import com.berray.math.Vec2;
import com.raylib.Raylib;

import java.util.function.BiConsumer;

import static com.raylib.Raylib.BeginTextureMode;
import static com.raylib.Raylib.ClearBackground;

public class RenderToTexture extends GameObject {
  private Vec2 size;
  private Raylib.RenderTexture renderTexture;

  public RenderToTexture(int width, int height) {
    this.size = new Vec2(width, height);
    this.renderTexture = Raylib.LoadRenderTexture(width, height);
  }

  public Raylib.RenderTexture getRenderTexture() {
    return renderTexture;
  }

  @Override
  public void visitDrawChildren(BiConsumer<String, Runnable> visitor) {
    visitor.accept(get("layer", Game.DEFAULT_LAYER), () ->
    {
      BeginTextureMode(renderTexture);
      ClearBackground(Color.GRAY.toRaylibColor());
    });
    super.visitDrawChildren(visitor);
    visitor.accept(get("layer", Game.DEFAULT_LAYER), Raylib::EndTextureMode);
  }
}
