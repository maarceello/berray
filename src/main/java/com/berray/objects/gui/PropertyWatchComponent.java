package com.berray.objects.gui;

import com.berray.GameObject;
import com.berray.components.core.Component;
import com.berray.event.CoreEvents;
import com.berray.event.PropertyChangeEvent;
import com.berray.event.SceneGraphEvent;
import com.berray.event.UpdateEvent;

/** Gui helper component to watch parent bound objects. */
public class PropertyWatchComponent extends Component {
  private String boundProperty;

  public PropertyWatchComponent(String... dependencies) {
    super("propertyWatch", dependencies);
  }

  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);

    on(CoreEvents.SCENE_GRAPH_ADDED,  this::processSceneGraphAdded);
    on(CoreEvents.SCENE_GRAPH_REMOVED,  this::processSceneGraphRemoved);
    on(CoreEvents.UPDATE,  this::processUpdate);
  }

  private void processSceneGraphAdded(SceneGraphEvent event) {
    // * add event listener "add to scene graph":
    //   * find next panel in parent hierarchie with bound object (calculated by property or set directly)
    //   * add property listener on panel with property "bound object" (with owner "this")
    //   * get (initial) bound object from parent panel und resolve property. set as "bound object" if non null

    // get next panel. Either it is our game object or one of the parents in the scene graph.
    Panel panel = getPanel();
    if (panel == null) {
      // no panel in the scene graph
      return;
    }

    panel.get

  }

  private Panel getPanel() {
    if (gameObject instanceof Panel) {
      return (Panel) gameObject;
    }

    Panel currentPanel

    return gameObject.findParent(Panel.class);
  }

  private void processSceneGraphRemoved(SceneGraphEvent event) {
    // * add event listener "remove from scene graph"
    //   * find next panel in parent hierarchie
    //   * remove all listeners with owner "this"
    //   * if current "bound object" is not null
    //   * clear "bound object"
    //   * fire property changed "bound object"
  }


  private void processBoundObjectChanged(PropertyChangeEvent event) {
    // * get bound object from parent
    // * if it is different from the current object
    //   * set "bound object" to new object
    //   * fire property change "bound object"
  }

  private void processUpdate(UpdateEvent event) {
    // * get bound object from parent
    // * resolve property against parents bound object
    // * if new object is not equals to the current bound object
    //   * set new bound object
    //   * fire property changed "bound object"
  }



  private String getBoundProperty() {
    return boundProperty;
  }

  private void setBoundProperty(String boundProperty) {
    this.boundProperty = boundProperty;
  }
}
