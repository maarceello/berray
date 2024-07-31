package com.berray.objects.addon;

import com.berray.GameObject;
import com.berray.components.core.AnchorComponent;
import com.berray.components.core.AnchorType;
import com.berray.components.core.PosComponent;
import com.berray.math.Vec2;
import com.berray.objects.core.Label;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ObjectDebug extends GameObject {
  private GameObject objectToMonitor;

  public ObjectDebug(GameObject objectToMonitor) {
    super();
    this.objectToMonitor = objectToMonitor;
    addComponents("debug");
    add(
        Label.label(() -> "Id: "+this.getDebuggedObjectId()),
        PosComponent.pos(new Vec2(0,0)),
        AnchorComponent.anchor(AnchorType.TOP_LEFT)
    );
    add(
        Label.label(() -> "Pos: "+this.getDebuggedObjectPos()),
        PosComponent.pos(new Vec2(0,20)),
        AnchorComponent.anchor(AnchorType.TOP_LEFT)
    );
    add(
        Label.label(() -> "Properties:\n"+this.getDebuggedObjectProperties()),
        PosComponent.pos(new Vec2(0,40)),
        AnchorComponent.anchor(AnchorType.TOP_LEFT)
    );
  }

  public String getDebuggedObjectId() {
    return objectToMonitor == null ? "?" : String.valueOf(objectToMonitor.getId());
  }

  public String getDebuggedObjectPos() {
    return objectToMonitor == null ? "?" : String.valueOf(objectToMonitor.<Object>get("pos"));
  }

  public String getDebuggedObjectProperties() {
    if (objectToMonitor == null) {
      return "?";
    }
    Set<String> propierties = objectToMonitor.getProperties();
    List<String> sortedProperties = new ArrayList<>(propierties);
    Collections.sort(sortedProperties);
    StringBuilder result = new StringBuilder();
    for (String property : sortedProperties) {
      Object value = objectToMonitor.get(property);
      if (value != null) {
        result.append(property+": "+value+"\n");
      }
    }
    return result.toString();
  }

}
