# berray

berray is a Java game programming library that helps you make games fast and fun. heavily inspired by kaboom.js/kaplay

its built on top of Jayli, JNI bindings for Raylib 5.0

WIP

# Object tree

* Game
  * root game object
    * sub game objects (with their children game objects)
    * components

# events

| Source     | Event Name | Parameter                                                                    | description                                              |
|------------|------------|------------------------------------------------------------------------------|----------------------------------------------------------|
| GameObject | add        | <ul><li>parent game object - GameObject</li><li>added game object</li></ul>  | fired after a gameobject is added to another game object |
| GameObject | update     | <ul><li>frame time - float</li></ul>                                         | fired each frame to update the game state |

# Roadmap

## core

### Features

- [x] local coordinate system (child objects position is relative to parent position)
  - [x] TODO: fix collision detection
- [x] localArea should respect anchor
- [x] layers
- [x] sprite animation
- [x] sprite atlas loading
- [ ] scenes, scene switching
- [ ] use and unuse
  - [ ] on unuse: recheck dependencies of remaining components
- [ ] events: timing, move out of screen 
- [ ] collision detection: 
  - [ ] support more shapes (only rect at the moment)
  - [ ] skip object combination which are already checked
  - [ ] detect collision with rotated shapes

### Bugs

[ ] fix pong example, collision detection seems broken

## gui
- [x] label
- [ ] button
- [ ] checkbox
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

