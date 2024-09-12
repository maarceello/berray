# berray

berray is a Java game programming library that helps you make games fast and fun. heavily inspired by kaboom.js/kaplay

its built on top of Jayli, JNI bindings for Raylib 5.0

WIP

# Quick Start

TODO: sample: pom, simple main

# Concepts

Berray is based on [Kaboom/Kaplay](https://kaplayjs.com/) and therefore shares the same concepts.
The core mechanics is a modified [Entity Component System](https://en.wikipedia.org/wiki/Entity_component_system)
pattern with Game Objects, Components and Events.

The [Game Objects](#gameobject) are organized in a tree, with a root game object as the base the tree. This
root object is managed by the game.
Each Game Object can have zero or more [components](#component), who adds various functionality to the object. A game
object without any
component is simply a container for other (child) game objects. One component is the [pos()](#poscomponent), which adds
a position to the Game Object.

Components add various functions to their game object. They do this by providing additional properties, actions and
events and can process events from other components.

TODO: explain properties (and how to get and set them), actions (and how to play them)

Events are the communication tool in berray. There are two kinds of events: game events and game object events.
Game events are not tied to a specific object, for example keyUp/KeyDown events or mouse events.
Game Object events always have a sending game object.
Events can have parameters, there are event type specific. You can see them in the event documentation.

# Events

## Game

### keyDown, keyUp, keyPress

Fired when the keys are pressed down (multiple events), released (one event) or pressed and released (one event).
Parameters:

* int keyCode - keyCode of the pressed/released key

### mousePress

Fired when a mouse button is pressed

Parameters:

* Vec2 pos - position of the mouse in window coordinates

### hover (on a game object)

Fired when the mouse cursor is moved across an object. Note: the event is only triggered when the mouse cursor moves.
When the cursor is not moved no additional events are send.

Parameters:

* Vec2 pos - position of the mouse in window coordinates

### click (on a game object)

Fired when a game object is clicked.

Parameters:

* Vec2 pos - position of the mouse in window coordinates

## GameObject

| Source     | Event Name | Parameter                                                                   | description                                              |
|------------|------------|-----------------------------------------------------------------------------|----------------------------------------------------------|
| GameObject | add        | <ul><li>parent game object - GameObject</li><li>added game object</li></ul> | fired after a gameobject is added to another game object |
| GameObject | update     | <ul><li>frame time - float</li></ul>                                        | fired each frame to update the game state                |

## Component

### propertyChange

Fired by components, when a property is changed.
Note that the events is fired *before* the change is applied, so the `newValue` from the event must be used.

Parameter:

* String propertyName - name of the property
* Object oldValue - old (replaced) value of the property
* Object newValue - new value of the property

## triggering events

Events can be triggered on the receiving game object. You can supply as many parameters as needed.

```
  gameObject.trigger("eventName", param);
```

If a parameter is expensive to calculate and the event is only sparse subscribed, you can provide the parameter value
in a `Supplier` to lazily calculate the value. The event system caches the value, so it the Supplier will be called only
once, regardless how many subscriber processes the event.

```
  gameObject.trigger("eventName", () -> expensiveCalculation());
```

# Roadmap

## core

### Features

- [x] local coordinate system (child objects position is relative to parent position)
    - [x] TODO: fix collision detection
- [x] localArea should respect anchor
- [x] layers
- [x] sprite animation
- [x] sprite atlas loading
- [x] scenes, scene switching
- [ ] use and unuse
    - [ ] on unuse: recheck dependencies of remaining components
- [ ] events: timing, move out of screen
- [ ] collision detection:
    - [ ] support more shapes (only rect at the moment)
    - [ ] skip object combination which are already checked
    - [ ] detect collision with rotated shapes

### improvements

- [ ] it should be possible to send event parameters lazy. i.e. add a Supplier as parameter and Events.getParameter()
  resolves the suppliers value and caches the result. Motivation: the hover event should calculate the mouse positions
  in object coordinates. This is expensive and maybe there are not even event listeners so the calculation is wasted.

### Bugs

[ ] fix pong example, collision detection seems broken

## gui

- [x] label
- [x] button
- [x] checkbox
- [ ] slider
- [ ] textarea (static), scrollable or autoremove rows after some time
- [ ] textarea/inputfield (enter text in input field)

## 2d stack

- [ ] add 2d camera object
- [ ] add sprite sheet animation
- [ ] add bone animation

## 3d stack

- [ ] create 3d pipeline
- [ ] add 3d camera object
- [ ] add 3d asset loader (ie md3 Models)
- [ ] add 3d parts animation (ie md3 models, https://www.moddb.com/games/quake-iii-arena/addons)
- [ ] add 3d bones animation (natively supported by raylib)

## documentation

- [ ] create documentation from javacoc

# Notes

- BerrayApplication supplies 3 default layers: background, default, gui
    - think about sublayer in one layer (stacking in gui layer)

https://pixijs.com/