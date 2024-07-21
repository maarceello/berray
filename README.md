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

| Source     | Event Name | Parameter                                                                   | description                                              |
|------------|------------|-----------------------------------------------------------------------------|----------------------------------------------------------|
| GameObject | add        | <ul><li>parent game object - GameObject</li><li>added game object</li></ul> | fired after a gameobject is added to another game object |
