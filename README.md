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


## Event types

There are some event types:

1. local event, non propagating. This event is send by a component to its game object. The event does not propagate 
   through the scene graph. Examples: "propertyChanged"
2. external event, non propagating. This event is send by another game object (for example the parent or the game). The 
   event does not propagate through the scene graph. Examples: "collide" 
3. event bubbling down the scene graph. This event starts at some point in the scene graph and visits the children of the
   current node. The mechanism by which the event is propagated and the cutoff condition depends on the event.
   Examples: "sceneGraphAdded", "sceneGraphRemoved"
4. event bubbling up the scene graph. This event starts at some point in the scene graph and visits the parents of the
   current node. The mechanism by which the event is propagated and the cutoff condition depends on the event.
   Examples: "actionPerformed"
  

Notes about event parameters:

The first event parameter is always the game object which fired the event. This may be null when the event is global, 
such as `keyPress` or `mouseMove`. 
Games should always use the most specific Event Object. This way changes in the parameters are automatically picked up 
and no migration effort is necessary.

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

# Collision Detection

For performance reasons the collision detection calculation as well as bounding boxes is only done for objects with 
the `area` component. Note that advanced mouse stuff (`MouseCompopnent`) is based on bounding boxes and therefore also 
need the `area` component. 

The collision detection is based on a bounding box. To calculate the bounding box, the game object needs to have at 
least a `size` property. Most drawing components do provide this property. The bounding box will be calculated as the 
rectangle `(0, 0) - (size.width, size.height)`.
Components may additionally supply a custom bounding box with the `localArea` Property. Then this area is used as a bounding box.
Note that `size` and `localArea` are in object space and will be transformed by the game object to world space automatically.

If a game object (or component) needs to set a custom collision detection in global space (i.e. for mouse processing)
the bounding box can be supplied as a `boundingBox` property. The bounding box will be calculated during transformation 
matrix processing and cached until the transformation matrix needs to be recalculated. To update changes in the 
`boundingBox` property the transformation matrix needs to be invalidated (`GameObject#setTransformDirty()`). 


# 3D Stack

Note: the default coordinate system uses y as the up vector.

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
- [x] slider
- [ ] textarea (static), scrollable or autoremove rows after some time
- [ ] textarea/inputfield (enter text in input field)


Some thoughts on gui architecture:

1. Vision

* Berray provides easy and hassle-free gui components with databinding and one default design.

2. Goals

notes: a) must have   b) should have    c) may have 

a. creation of the gui is easy (builder pattern?) and can be read from a file. Interaction ist provided by databinding
  and actions.
b. base components: 
  * frames (closeable, movable, minimizeable, resizeable)
  * panels (programmatically resizeable, scrollable)
  * button (click button, toggle button, radio button, checkbox)
  * scrollbar, progressbar 
  * text display and input (label, text field, text area, no text effects, maybe embedded images)
b. easy layouting. relayouting in case of resize is simple, but may be provided by custom strategies
b. databinding and actions: changing one gui element triggers a (panel global) action which may be processed by listeners
c. custom layout strategies and custom designs are possible and easy
c. auxiliary components
  * combo box
  * tabs
  * file chooser
  * color chooser

3. Processes

* set design manager 
  * game#setDesignManager() - stored in game
* create panel 
  * set layout manager (note: layout manager is required)
  * set size in absolute pixels (note: size is required for outermost panel or frame)
  * set default insets of zero
  * set default border: none
  * set bound object: none
    
* add border to panel
  * set border (name)
  * set layout dirty flag

* add child to panel
  * add child via game object
  * set layout dirty flag

* layout childs in panel according to layout
  * calculate final insets (insets + border) and final inner size 
  * call LayoutManager#layout with panel

* draw panel
  * check layout dirty flag
    * if not: layout panel and clear layout dirty flag
  * draw childs
  * call design manager with panel and border (name) 
    
* create frame
  * create panel as a holder for the frame
  * add default frame border (from design manager?)
  * add panel for title bar
  * create child "content" (panel or game object)

* add border and insets to panel (note: only panels can have borders or insets)
  * set current border
  * set layout dirty flag

* create gui component (ie. button) with bound properties
  * set action id
  * set databinding map

* add gui component (with id) to panel
  * subprocess: "add child to panel"
  * on "add" event
    * gui component registers listener for event "bind"
  * on "add to scene graph" event
    * parent (panel) sends event "bind" with current databinding object
    * register property changed listener from databinding map
    * fire initial property changed event for all source properties
  * on "remove from scene graph" event
      * parent (panel) sends event "unbind" with removed databinding object
      * gui component removes listeners from databinding object
  * panel adds action performed event listener to child (doesn't matter if it is a gui component or not)
    * note: if the panel contains non-gui containers, these are responsible for propagating the action performed event

* execute action in gui component
  * send action performed event 
    * first parameter is game object
    * 2nd parameter is action id (null if it does not exist)
    * other parameter are dependent on the gui component type. Most often this parameter is missing (button) or has the 
      new value of the component (slider, checkbox)
  * components may intercept the action performed event and send another event. For example a radio group component
    may intercept button clicks and translate these to an action performed event with the pressed button index and its 
    own action id 

4. Entities

* frame: is a panel (with border) and has a title bar. can be moved, minimized, closed, resized
  * state: normal, minimized, closed
* panel (containing border)
  * border
  * insets
  * layout dirty flag
  * bound object
* border (has size to all sides)
  * size to all sides
* gui components
  * action id 
  * databinding map
    * source property
    * destination property
    * direction: source->destination, bidirectional
* design manager (can be applied to game objects to create specific drawings. can add components, actions, listeners, everything )
  * method: installPanel (panel)
  * method: installButton (button)
  * method: installLabel (label)
  * method: get border (border name)

* layout manager (lays out childs in a panel)
  * method: layout panel (panel, list of components to layout, rectangle for content)

5. Architecture




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