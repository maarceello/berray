package com.berray.components.core;

import com.berray.GameObject;
import com.berray.assets.*;
import com.berray.event.Event;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.raylib.Raylib;

import java.util.List;

import static com.raylib.Jaylib.WHITE;
import static com.raylib.Raylib.*;

public class SpriteComponent extends Component {
    /**
     * Name of texture or sprite sheet asset.
     */
    public String textureName;
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
    public SpriteComponent(String textureName) {
        super("sprite");
        this.textureName = textureName;
    }

    @Override
    public void draw() {
        rlPushMatrix();
        {
            rlMultMatrixf(gameObject.getWorldTransform().toFloatTransposed());

            Asset asset = getAssetManager().getAsset(textureName);
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
            } else {
                throw new IllegalStateException("Illegal asset type for " + textureName + ": " + asset.getType());
            }
        }
        rlPopMatrix();
    }

    @Override
    public void add(GameObject gameObject) {
        registerGetter("size", this::getSize);
        registerGetter("render", () -> true);
        registerGetter("curAnim", this::getAnim);
        registerBoundProperty("frame", this::getFrameNo, this::setFrameNo);
        registerBoundProperty("flipX", this::isFlipX, this::setFlipX);
        registerBoundProperty("flipY", this::isFlipY, this::setFlipY);
        registerAction("play", this::play);
        registerAction("stop", this::stop);
        on("update", this::update);
        if (anim != null) {
            currentAnimation = initializeAnimation(anim);
        }
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
        this.anim = animationName;
        this.frameNo = 0;
        this.frameDuration = 0;
        if (gameObject != null) {
            currentAnimation = initializeAnimation(animationName);
        }
        return this;
    }

    private Animation initializeAnimation(String animationName) {
        SpriteSheet spriteSheet = getAssetManager().getAsset(textureName, AssetType.SPRITE_SHEET).getAsset();
        Animation animation = spriteSheet.getAnimation(animationName);
        if (animation == null) {
            throw new IllegalStateException("animation " + animationName + " not found in asset " + textureName);
        }
        return animation;
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    public boolean isFlipY() {
        return flipY;
    }

    public boolean isFlipX() {
        return flipX;
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
        Asset asset = getAssetManager().getAsset(textureName);
        if (asset.getType() == AssetType.SPRITE) {
            Raylib.Texture texture = asset.getAsset();
            return new Vec2(texture.width(), texture.height());
        } else if (asset.getType() == AssetType.SPRITE_SHEET) {
            SpriteSheet spriteSheet = asset.getAsset();
            return new Vec2(spriteSheet.getSpriteWidth(), spriteSheet.getSpriteHeight());
        }
        return Vec2.origin();
    }

    // Static method to just call "sprite()" get the sprite from the asset manager and put in into the texture for the sprite component
    public static SpriteComponent sprite(String name) {
        return new SpriteComponent(name);
    }
}
