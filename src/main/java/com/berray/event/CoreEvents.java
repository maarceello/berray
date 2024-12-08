package com.berray.event;

public interface CoreEvents {
  String ADD = AddEvent.EVENT_NAME;
  String SCENE_GRAPH_ADDED = SceneGraphEvent.EVENT_NAME_ADDED;
  String SCENE_GRAPH_REMOVED = SceneGraphEvent.EVENT_NAME_REMOVED;
  String PROPERTY_CHANGED = PropertyChangeEvent.EVENT_NAME;
  String UPDATE = UpdateEvent.EVENT_NAME;
  // physics events
  String PHYSICS_RESOLVE = PhysicsResolveEvent.EVENT_NAME;
  String PHYSICS_COLLIDE_UPDATE = PhysicsCollideUpdateEvent.EVENT_NAME;
  String PHYSICS_BEFORE_RESOLVE = PhysicsBeforeResolveEvent.EVENT_NAME;
  String PHYSICS_HEADBUTT = PhysicsEvent.EVENT_NAME_HEADBUTT;
  String PHYSICS_GROUND = PhysicsEvent.EVENT_NAME_GROUND;
  String PHYSICS_FALL = PhysicsEvent.EVENT_NAME_FALL;
  String PHYSICS_FALL_OFF = PhysicsEvent.EVENT_NAME_FALL_OFF;
  String PHYSICS_COLLIDE = PhysicsCollideEvent.EVENT_NAME;
  String PHYSICS_COLLIDE_END = PhysicsCollideEndEvent.EVENT_NAME;
  // animation events
  String ANIMATION_END = AnimationEvent.EVENT_NAME_ANIMATION_END;
  String ANIMATION_START = AnimationEvent.EVENT_NAME_ANIMATION_END;
  // key events
  String KEY_PRESS = KeyEvent.EVENT_NAME_KEY_PRESS;
  String KEY_DOWN = KeyEvent.EVENT_NAME_KEY_DOWN;
  String KEY_UP = KeyEvent.EVENT_NAME_KEY_UP;
  // mouse events
  String MOUSE_CLICK = MouseEvent.EVENT_NAME_MOUSE_CLICK;
  String MOUSE_RELEASE = MouseEvent.EVENT_NAME_MOUSE_RELEASE;
  String MOUSE_PRESS = MouseEvent.EVENT_NAME_MOUSE_PRESS;
  String MOUSE_MOVE = MouseEvent.EVENT_NAME_MOUSE_MOVE;
  String MOUSE_WHEEL_MOVE = MouseEvent.EVENT_NAME_MOUSE_WHEEL_MOVE;
  String DRAG_START = MouseEvent.EVENT_NAME_DRAG_START;
  String DRAGGING = MouseEvent.EVENT_NAME_DRAGGING;
  String DRAG_FINISH = MouseEvent.EVENT_NAME_DRAG_FINISH;
  String HOVER = MouseEvent.EVENT_NAME_HOVER;
  String HOVER_ENTER = MouseEvent.EVENT_NAME_HOVER_ENTER;
  String HOVER_LEAVE = MouseEvent.EVENT_NAME_HOVER_LEAVE;

  // gui events
  String ACTION_PERFORMED = ActionEvent.EVENT_NAME;
  String BIND = BindEvent.EVENT_NAME_BIND;
  String UNBIND = BindEvent.EVENT_NAME_UNBIND;
}
