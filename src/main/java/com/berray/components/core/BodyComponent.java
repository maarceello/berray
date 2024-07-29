package com.berray.components.core;

import com.berray.GameObject;
import com.berray.event.Event;
import com.berray.math.Collision;
import com.berray.math.Vec2;

import java.util.List;

public class BodyComponent extends Component {
  public static final float DEFAULT_JUMP_FORCE = 640f;
  private static final float MAX_VEL = 65536f;
  // note: static is a keyword in java
  /**
   * true when the object cannot move.
   */
  private final boolean isStatic;
  /**
   * true when this object "sits" on another object
   */
  private boolean grounded = false;
  /**
   * current jump force
   */
  private float jumpForce = DEFAULT_JUMP_FORCE;
  /**
   * other object this object sits on
   */
  private GameObject curPlatform;
  /**
   * position of the plattform in the previous update
   */
  private Vec2 lastPlatformPos;
  /**
   * current velocity
   */
  private Vec2 vel = Vec2.origin();
  private boolean willFall = false;
  private boolean stickToPlatform = true;
  private float gravityScale = 1.0f;
  private float drag = 0.0f;

  public BodyComponent(boolean isStatic) {
    super("body", "pos");
    this.isStatic = isStatic;
  }

  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);
    gameObject.registerGetter("grounded", this::isGrounded);
    gameObject.registerAction("jump", this::jump);

    if (gameObject.is("area")) {
      gameObject.on("collideUpdate", this::collideUpdate);
    }
    gameObject.on("update", this::update);
  }

  public void collideUpdate(Event event) {
    GameObject other = event.getParameter(0);
    Collision col = event.getParameter(1);

    if (col == null || col.isResolved() || !other.is("body")) {
      return;
    }

    gameObject.trigger("beforePhysicsResolve", col);
    Collision rcol = col.reverse();
    other.trigger("beforePhysicsResolve", rcol);
  }

  public void update(Event event) {
    float deltaTime = event.getParameter(0);
    if (gameObject.getRoot().get("gravityDirection") != null && !this.isStatic) {
      if (willFall) {
        curPlatform = null;
        lastPlatformPos = null;
        gameObject.trigger("fallOff");
        willFall = false;
      }

      boolean addGravity = true;

      if (curPlatform != null) {
        if (
          // TODO: this prevents from falling when on edge
            !gameObject.<Boolean>doAction("isColliding", curPlatform)
                || !curPlatform.exists()
                || !curPlatform.is("body")
        ) {
          willFall = true;
        } else {
          if (
              lastPlatformPos != null
                  && !curPlatform.<Vec2>get("pos").equals(lastPlatformPos)
                  && stickToPlatform) {
            gameObject.doAction("moveBy", curPlatform.<Vec2>get("pos").sub(lastPlatformPos));
          }
          lastPlatformPos = curPlatform.get("pos");
          addGravity = false;
        }
      }

      if (addGravity) {
        Vec2 prevVel = this.vel;

        // Apply gravity
        Vec2 gravity = gameObject.getRoot().<Vec2>get("gravity");
        this.vel = this.vel.add(gravity.scale(this.gravityScale * deltaTime));

        // Clamp velocity
        float maxVel = MAX_VEL;
        if (this.vel.lengthSquared() > maxVel * maxVel) {
          this.vel = this.vel.normalize().scale(maxVel);
        }

        if (prevVel.dot(gravity) < 0 && this.vel.dot(gravity) >= 0) {
          gameObject.trigger("fall");
        }
      }
    }

    this.vel = this.vel.scale(1 - this.drag);

    gameObject.doAction("move", this.vel, deltaTime);

  }

  public boolean isGrounded() {
    return grounded;
  }

  public void jump(List<Object> params) {
    Float force = (Float) params.get(0);
    if (force == null) {
      force = jumpForce;
    }
    curPlatform = null;
    lastPlatformPos = null;
    // jump in the opposite direction as the gravity
    this.vel = gameObject.getRoot().<Vec2>get("gravityDirection").normalize().scale(-force);
  }

  public static BodyComponent body(boolean isStatic) {
    return new BodyComponent(isStatic);
  }
}
