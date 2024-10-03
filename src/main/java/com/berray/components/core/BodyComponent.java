package com.berray.components.core;

import com.berray.GameObject;
import com.berray.event.Event;
import com.berray.event.UpdateEvent;
import com.berray.math.Collision;
import com.berray.math.Matrix4;
import com.berray.math.Vec2;
import com.berray.math.Vec3;

import java.util.List;

/**
 * Adds physics and collision detection to an object.
 */
public class BodyComponent extends Component {
  public static final float DEFAULT_JUMP_FORCE = 640f;
  private static final float MAX_VEL = 65536f;

  /**
   * true when the object cannot move.
   */
  private final boolean isStatic;
  /**
   * current jump force
   */
  private float jumpForce = DEFAULT_JUMP_FORCE;
  /**
   * mass of the object. determines the pushback force in collisions.
   */
  private float mass = 1.0f;
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
  /**
   *
   */
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
    registerGetter("grounded", this::isGrounded);
    registerGetter("static", this::isStatic);
    registerGetter("falling", this::isFalling);
    registerAction("jump", this::jump, JumpAction::new);

    if (gameObject.is("area")) {
      on("collideUpdate", this::collideUpdate);
      on("physicsResolve", this::onPhysicsResolve);
    }
    on("update", this::update);
  }

  /**
   * Returns true when the object is standing on another.
   *
   * @type property
   */
  public boolean isGrounded() {
    return curPlatform != null;
  }

  /**
   * Returns true when the object is static and cannot move.
   *
   * @type property
   */
  private boolean isStatic() {
    return isStatic;
  }

  /**
   * Returns true when the object is falling (moving down in direction of gravity).
   *
   * @type property
   */
  private boolean isFalling() {
    Vec3 gravityDirection = gameObject.getGame().getGravityDirection();
    if (gravityDirection == null) {
      return false;
    }
    return this.vel.dot(new Vec2(gravityDirection.getX(), gravityDirection.getY())) > 0;
  }

  /**
   * Returns true when the object is jumping (moving up in direction of gravity).
   *
   * @type property
   */
  private boolean isJumping() {
    Vec3 gravityDirection = gameObject.getGame().getGravityDirection();
    if (gravityDirection == null) {
      return false;
    }
    return this.vel.dot(new Vec2(gravityDirection.getX(), gravityDirection.getY())) < 0;
  }

  /**
   * Adds a jump force to the object.
   *
   * @type action
   */
  public void jump(JumpAction params) {
    Vec3 gravityDirection = gameObject.getGame().getGravityDirection();
    if (gravityDirection == null) {
      return;
    }
    Float force = params.getForce();
    if (force == null) {
      force = jumpForce;
    }
    curPlatform = null;
    lastPlatformPos = null;
    // jump in the opposite direction as the gravity
    this.vel = new Vec2(gravityDirection.getX(), gravityDirection.getY()).scale(-force);
  }


  public void collideUpdate(Event event) {
    GameObject other = event.getParameter(0);
    Collision collision = event.getParameter(1);

    if (collision == null || collision.isResolved() || !other.is("body")) {
      return;
    }

    gameObject.trigger("beforePhysicsResolve", collision);
    Collision reverseCollision = collision.reverse();
    other.trigger("beforePhysicsResolve", reverseCollision);
    // user can mark 'resolved' in beforePhysicsResolve to stop a resolution
    if (collision.isResolved() || reverseCollision.isResolved()) {
      return;
    }

    BodyComponent otherBody = other.getComponent(BodyComponent.class);
    // only try to move an object if the displacement is > 0
    if (otherBody != null && !collision.getDisplacement().equals(Vec2.origin())) {
      if (this.isStatic && otherBody.isStatic) {
        // both objects are static: do nothing to resolve the collision
        return;
      }
      if (!this.isStatic && !otherBody.isStatic) {
        // both objects are dynamic. bounce the object back, based on their mass ratio
        float totalMass = this.mass + otherBody.mass;
        Vec2 displacement = getLocalCoordinateDisplacement(gameObject, collision.getDisplacement());
        Vec2 reverseDisplacement = getLocalCoordinateDisplacement(other, collision.getDisplacement().negate());
        gameObject.doAction("moveBy", displacement.scale(otherBody.mass / totalMass));
        other.doAction("moveBy", reverseDisplacement.scale(this.mass / totalMass));
      } else {
        // if one is static and one is not, resolve the non-static one
        if (!this.isStatic) {
          gameObject.doAction("moveBy", getLocalCoordinateDisplacement(gameObject, collision.getDisplacement()));
        } else {
          other.doAction("moveBy", getLocalCoordinateDisplacement(other, reverseCollision.getDisplacement()));
        }
      }
    }

    collision.setResolved(true);
    reverseCollision.setResolved(true);
    gameObject.trigger("physicsResolve", collision);
    other.trigger("physicsResolve", reverseCollision);
  }

  /**
   * Calculate the displacement in the objects local coordinate system.
   */
  private static Vec2 getLocalCoordinateDisplacement(GameObject gameObject1, Vec2 worldDisplacement) {
    Matrix4 inverseTransform = gameObject1.getWorldTransformWithoutAnchor().inverse();
    Vec3 localDisplacement = inverseTransform.multiply(worldDisplacement.getX(), worldDisplacement.getY(), 0);
    Vec3 localZero = inverseTransform.multiply(Vec3.origin());
    Vec3 localRelativeDisplacement = localDisplacement.sub(localZero);
    Vec2 displacement = new Vec2(localRelativeDisplacement.getX(), localRelativeDisplacement.getY());
    return displacement;
  }

  private void onPhysicsResolve(Event event) {
    Collision col = event.getParameter(0);
    Vec3 gravity = gameObject.getGame().getGravity();
    if (gravity != null) {
      Vec2 gravity2d = new Vec2(gravity.getX(), gravity.getY());
      if (col.isBottom(gravity2d) && this.isFalling()) {
        this.vel = this.vel.reject(gravity2d.normalize());
        curPlatform = col.getOther();
        lastPlatformPos = curPlatform.get("pos");
        if (willFall) {
          willFall = false;
        } else {
          gameObject.trigger("ground", curPlatform);
        }
      } else if (col.isTop(gravity2d) && this.isJumping()) {
        this.vel = this.vel.reject(gravity2d.normalize());
        gameObject.trigger("headbutt", col.getOther());
      }
    }
  }

  /**
   * Params:
   * <p>
   * - float deltaTime
   */
  public void update(Event event) {
    float deltaTime = event.getParameter(0);
    Vec3 gravityDirection = gameObject.getGame().getGravityDirection();
    if (gravityDirection == null || isStatic) {
      return;
    }

    if (willFall) {
      curPlatform = null;
      lastPlatformPos = null;
      gameObject.trigger("fallOff");
      willFall = false;
    }

    boolean addGravity = true;

    if (curPlatform != null) {
      if (
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
      Vec3 gravity3d = gameObject.getGame().getGravity();
      Vec2 gravity = new Vec2(gravity3d.getX(), gravity3d.getY());
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

    this.vel = this.vel.scale(1 - this.drag);
    gameObject.doAction("move", this.vel, deltaTime);
  }

  /**
   * Creates a body component.
   *
   * @param isStatic when true the body cannot move and therefore will not move in case of a collision
   * @type creator
   */
  public static BodyComponent body(boolean isStatic) {
    return new BodyComponent(isStatic);
  }

  private static class JumpAction extends Action {
    private JumpAction(List<Object> params) {
      super(params);
    }

    public Float getForce() {
      return getParameter(0);
    }

  }
}
