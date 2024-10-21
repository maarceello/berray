package com.berray.components.core;

import com.berray.GameObject;
import com.berray.math.Matrix4;

import java.util.List;

/**
 * Adds a position vector to the Game Object.
 */
public abstract class PosComponent<E> extends Component {
  protected E pos;

  public PosComponent(E pos) {
    super("pos");
    this.pos = pos;
  }

  @Override
  public void add(GameObject gameObject) {
    registerBoundProperty("pos", this::getPos, this::setPos);
    registerGetter("posTransform", this::getTransform);
    registerAction("move", this::moveAction, MoveAction::new);
    registerAction("moveBy", this::moveByAction, MoveAction::new);
  }

  /**
   * Returns the transformation matrix.
   *
   * @type property
   */
  protected abstract Matrix4 getTransform();

  /**
   * Returns the current position.
   *
   * @type property
   */
  public E getPos() {
    return pos;
  }

  /**
   * Sets the current position. Note that this will force recalculation of the transformation matrix.
   *
   * @type property
   */
  public void setPos(E pos) {
    this.pos = pos;
    this.gameObject.setTransformDirty();
  }

  /**
   * Moves the position by a delta, respecting the frame time. So the delta is interpreted
   * as movement per second.
   *
   * @type action
   */
  public void moveAction(MoveAction<E> params) {
    E velocity = params.getVector();
    float deltaTime = params.getDeltaTime();
    setPos(move(pos, velocity, deltaTime));
  }


  /**
   * Moves the position by a delta. This action does ignore the frame time.
   *
   * @type action
   */
  public void moveByAction(MoveAction<E> params) {
    E amount = params.getVector();
    setPos(move(pos, amount));
  }

  /**
   * Moves the vector <code>pos</code> by amount <code>amount</code>
   */
  protected abstract E move(E pos, E amount);

  protected abstract E move(E pos, E amount, float scale);


  protected static class MoveAction<E> extends Action {
    public MoveAction(List<Object> params) {
      super(params);
    }

    public E getVector() {
      return getParameter(0);
    }

    public float getDeltaTime() {
      return getParameter(1);
    }
  }
}
