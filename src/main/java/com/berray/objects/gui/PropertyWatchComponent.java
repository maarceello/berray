package com.berray.objects.gui;

import com.berray.GameObject;
import com.berray.components.core.Component;
import com.berray.event.CoreEvents;
import com.berray.event.UpdateEvent;
import com.berray.objects.guiold.PropertyResolveService;

/** Gui helper component to watch parent bound objects. */
public class PropertyWatchComponent extends Component {
  private String boundProperty;

  public PropertyWatchComponent(String boundProperty) {
    super("propertyWatch");
    this.boundProperty = boundProperty;
  }

  @Override
  public void add(GameObject gameObject) {
    super.add(gameObject);

    on(CoreEvents.UPDATE,  this::processUpdate);

    registerBoundProperty("boundProperty", this::getBoundProperty, this::setBoundProperty);
  }

  /**
   * Returns the next panel up the hierarchie (other than our own game object) which is bound to either an object
   * directly or through a property.
   */
  private Panel getPanel() {
    Panel currentPanel = (Panel) gameObject;

    do {
      currentPanel = currentPanel.findParent(Panel.class);
    } while (currentPanel != null && currentPanel.getPanelType() == PanelType.UNBOUND);

    return currentPanel;
  }

  private void processUpdate(UpdateEvent event) {
    Panel panelWithParentObject = getPanel();
    if (panelWithParentObject == null) {
      // no panel in the scene graph
      return;
    }
    // get bound object from parent panel und resolve property. set as "bound object" (even if null)
    Object parentObject = panelWithParentObject.get("boundObject");
    Object value = parentObject == null ? null : PropertyResolveService.getInstance().getProperty(parentObject, boundProperty);
    gameObject.set("boundObject", value);
  }

  private String getBoundProperty() {
    return boundProperty;
  }

  private void setBoundProperty(String boundProperty) {
    this.boundProperty = boundProperty;
  }
}
