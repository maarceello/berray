package com.berray.components.core;

import com.berray.GameObject;
import com.berray.assets.Animation;
import com.berray.assets.Asset;
import com.berray.assets.AssetType;
import com.berray.assets.SpriteSheet;
import com.berray.event.Event;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.raylib.Raylib;

import java.util.List;

import static com.berray.assets.AssetManager.getAsset;
import static com.berray.assets.AssetManager.getSpriteSheet;
import static com.raylib.Jaylib.WHITE;
import static com.raylib.Raylib.*;

public class SpriteComponent extends Component {
  /**
   * Name of texture or sprite sheet asset.
   */
  public String texture;
  /**
   * Name of animation when the asset is a sprite sheet.
   */
  private String anim = null;
  /**
   * current frame number
   */
  private int frameNo = 0;
  /**
   * duration how long the animation is displayed already
   */
  private float frameDuration = 0;
  private Animation currentAnimation;

  private boolean flipX;
  private boolean flipY;

  // Construct
  public SpriteComponent(String texture) {
    super("sprite");
    this.texture = texture;
  }

  @Override
  public void draw() {
    rlPushMatrix();
    {
      rlMultMatrixf(gameObject.getWorldTransform().toFloatTransposed());

      Asset asset = getAsset(texture);
      if (asset.getType() == AssetType.SPRITE) {
        DrawTexture(asset.getAsset(), 0, 0, WHITE);
      } else if (asset.getType() == AssetType.SPRITE_SHEET) {
        SpriteSheet spriteSheet = asset.getAsset();

        Rect frameRect;
        if (anim != null && spriteSheet.hasAnimations()) {
          // we have an animation, then get the frame no relative to the animation
          Animation animation = spriteSheet.getAnimation(anim);
          frameRect = animation.getFrame(frameNo);
        } else {
          // sprite sheet doesn't have animations. Then get the frame relative to the sheet.
          frameRect = spriteSheet.getFrame(frameNo);
        }
        Rectangle rectangle = frameRect.toRectangle();
        if (flipX) {
          rectangle.width(-frameRect.getWidth());
        }
        if (flipY) {
          rectangle.height(-frameRect.getHeight());
        }
        DrawTextureRec(spriteSheet.getTexture(), rectangle, Vec2.origin().toVector2(), WHITE);
      }
    }
    rlPopMatrix();
  }

  @Override
  public void add(GameObject gameObject) {
    registerGetter("size", this::getSize);
    registerGetter("render", () -> true);
    registerGetter("curAnim", this::getAnim);
    registerMethod("frame", this::getFrameNo, this::setFrameNo);
    registerSetter("flipX", this::setFlipX);
    registerSetter("flipY", this::setFlipY);
    registerAction("play", this::play);
    registerAction("stop", this::stop);
    on("update", this::update);
  }

  public int getFrameNo() {
    return frameNo;
  }

  public void setFrameNo(int frameNo) {
    this.frameNo = frameNo;
  }

  public SpriteComponent frame(int frame) {
    this.frameNo = frame;
    return this;
  }

  public String getAnim() {
    return anim;
  }

  public SpriteComponent anim(String animationName) {
    SpriteSheet spriteSheet = getSpriteSheet(texture);
    Animation animation = spriteSheet.getAnimation(animationName);
    if (animation == null) {
      throw new IllegalStateException("animation " + animationName + " not found in asset " + texture);
    }
    this.anim = animationName;
    this.currentAnimation = animation;
    this.frameNo = 0;
    this.frameDuration = 0;
    return this;
  }

  public void setFlipX(boolean flipX) {
    this.flipX = flipX;
  }

  public void setFlipY(boolean flipY) {
    this.flipY = flipY;
  }

  private void update(Event event) {
    float deltaTime = event.getParameter(0);
    if (currentAnimation != null) {
      frameDuration += deltaTime;
      float frameTime = 1.0f / currentAnimation.getSpeed();
      if (frameDuration >= frameTime) {
        // next frame
        frameDuration -= frameTime;
        // last frame reached?
        frameNo++;
        if (frameNo >= currentAnimation.getNumFrames()) {
          // when loop, rollback to first frame, else stay on last frame
          if (currentAnimation.isLoop()) {
            frameNo = 0;
          } else {
            frameNo = currentAnimation.getNumFrames() - 1;
            // stop animation
            stop();
          }
        }
      }
    }
  }

  private void stop() {
    currentAnimation = null;
    gameObject.trigger("animEnd", anim);
  }

  public void play(List<Object> params) {
    String animationName = (String) params.get(0);
    anim(animationName);
    gameObject.trigger("animStart", anim);
  }

  private Vec2 getSize() {
    Asset asset = getAsset(texture);
    if (asset.getType() == AssetType.SPRITE) {
      Raylib.Texture texture = asset.getAsset();
      return new Vec2(texture.width(), texture.height());
    } else if (asset.getType() == AssetType.SPRITE_SHEET) {
      SpriteSheet spriteSheet = asset.getAsset();
      return new Vec2(spriteSheet.getSpriteWidth(), spriteSheet.getSpriteHeight());
    }
    return Vec2.origin();
  }

  public boolean isRender() {
    return true;
  }

  // Static method to just call "sprite()" get the sprite from the asset manager and put in into the texture for the sprite component
  public static SpriteComponent sprite(String name) {
    return new SpriteComponent(name);
  }
}
