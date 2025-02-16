package com.berray.objects.gui;

import com.berray.GameObject;
import com.berray.components.core.PosComponent2d;
import com.berray.components.core.SpriteComponent;
import com.berray.math.Vec2;
import com.berray.objects.gui.layout.GridLayout;
import com.berray.objects.gui.layout.LayoutManager;
import com.berray.objects.gui.layout.NopLayoutManager;
import com.berray.objects.gui.layout.RowLayout;
import com.berray.objects.gui.model.MultipleButtonModel;
import com.berray.objects.gui.model.PropertySliderModel;
import com.berray.objects.gui.model.SingleButtonModel;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class GuiPanelReader {

  private Map<String, Supplier<? extends GameObject>> types = new HashMap<>();
  private Map<String, Function<Map<String, Object>,Object>> beanReader = new HashMap<>();
  private Map<String, Function<Object, Object>> properties = new HashMap<>();

  public GuiPanelReader() {
    types.put("frame", Frame::new);
    types.put("panel", Panel::panel);
    types.put("property-panel", Panel::panelWithBoundProperty);
    types.put("loop-panel", LoopPanel::panelLoop);
    types.put("slider", Slider::slider);
    types.put("label", Label::label);
    types.put("checkbox", Button::checkbox);
    types.put("radiobutton", Button::radioButton);
    types.put("sprite", () -> GameObject.makeGameObject(SpriteComponent.sprite(null)));

    beanReader.put("property-slider-model", this::readPropertySliderModel);
    beanReader.put("checkbox-button-model", this::readCheckboxButtonModel);
    beanReader.put("radio-button-model", this::readRadioButtonModel);
    beanReader.put("grid-layout", this::readGridLayout);
    beanReader.put("row-layout", this::readRowLayout);

    properties.put("size", this::readVec2);
    properties.put("pos", this::readVec2);
    properties.put("layoutManager", this::readLayout);
    properties.put("border", this::readString);
    properties.put("title", this::readString);
    properties.put("value", this::readString);
    properties.put("texture", this::readString);
    properties.put("label", this::readString);
    properties.put("actionId", this::readString);
    properties.put("model", this::readYamlObject);
    properties.put("template", this::readTemplate);
    properties.put("boundProperty", this::readString);
  }

  private Object readGridLayout(Map<String, Object> data) {
    GridLayout gridLayout = new GridLayout();
    gridLayout.setColumns(checkList(data.get("columns"), "columns", Integer.class));
    gridLayout.setRows(checkList(data.get("rows"), "rows", Integer.class));
    if (data.get("columnSpacing") != null) {
      gridLayout.setColumnSpacing(checkType(data.get("columnSpacing"), Integer.class, "'columnSpacing' must be an Integer, but is a "));
    }
    if (data.get("rowSpacing") != null) {
      gridLayout.setRowSpacing(checkType(data.get("rowSpacing"), Integer.class, "'rowSpacing' must be an Integer, but is a "));
    }
    return gridLayout;
  }

  private Object readRowLayout(Map<String, Object> data) {
    RowLayout gridLayout = new RowLayout();
    gridLayout.setWidthString(checkType(data.get("columns"), String.class, "columns must be a string"));
    gridLayout.setHeightString(checkType(data.get("height"), String.class, "height must be a string"));
    return gridLayout;
  }


  private Object readPropertySliderModel(Map<String, Object> data) {
    Object minObject = data.get("min");
    Object maxObject = data.get("max");
    Object valueObject = data.get("value");
    PropertySliderModel model = new PropertySliderModel();
    if (minObject != null) {
      if (minObject instanceof Number) {
        model.minFixed(((Number) minObject).intValue());
      } else if (minObject instanceof String) {
        model.minProperty((String) minObject);
      } else {
        throw new IllegalStateException("'min' must be either a number (integer) or a property (string), but it is " + minObject.getClass().getName());
      }
    }
    if (maxObject != null) {
      if (maxObject instanceof Number) {
        model.maxFixed(((Number) maxObject).intValue());
      } else if (maxObject instanceof String) {
        model.maxProperty((String) maxObject);
      } else {
        throw new IllegalStateException("'max' must be either a number (integer) or a property (string), but it is " + maxObject.getClass().getName());
      }
    }
    if (valueObject == null) {
      throw new IllegalStateException("'value' property is required");
    }
    if (!(valueObject instanceof String) ) {
      throw new IllegalStateException("'value' must be a property (string), but it is " + valueObject.getClass().getName());
    }
    model.valueProperty((String) valueObject);
    return model;
  }

  private Object readCheckboxButtonModel(Map<String, Object> data) {
    Object valueObject = data.get("value");
    SingleButtonModel model = SingleButtonModel.checkboxButtonModel();
    if (valueObject == null) {
      throw new IllegalStateException("'value' property is required");
    }
    if (!(valueObject instanceof String) ) {
      throw new IllegalStateException("'value' must be a property (string), but it is " + valueObject.getClass().getName());
    }
    model.valueProperty((String) valueObject);
    return model;
  }

  private Object readRadioButtonModel(Map<String, Object> data) {
    MultipleButtonModel model = MultipleButtonModel.radioButtonModel();
    Object valueObject = data.get("value");
    if (valueObject == null) {
      throw new IllegalStateException("'value' property is required");
    }
    if (!(valueObject instanceof String) ) {
      throw new IllegalStateException("'value' must be a property (string), but it is " + valueObject.getClass().getName());
    }
    model.valueProperty((String) valueObject);

    Object armedObject = data.get("armed");
    if (armedObject != null) {
      if (!(armedObject instanceof String)) {
        throw new IllegalStateException("'value' must be a property (string), but it is " + armedObject.getClass().getName());
      }
      model.armedProperty((String) armedObject);
    }

    return model;
  }

  public <E> E read(InputStream inputStream) {
    Yaml yaml = new Yaml();
    return (E) readYamlObject(yaml.<Map<String, Object>>load(inputStream));
  }

  private Object readYamlObject(Object dataObject) {
    Map<String, Object> data = checkType(dataObject, Map.class, "data must be a yaml object, but is a ");
    Object type = data.get("type");
    if (type == null) {
      throw new IllegalStateException("no 'type' parameter in object");
    }
    if (!(type instanceof String)) {
      throw new IllegalStateException("'type' must be a string, but is a "+type.getClass().getName());
    }

    if (types.containsKey(type)) {
      return readGameObject(types.get(type).get(), data);
    }
    if (beanReader.containsKey(type)) {
      return beanReader.get(type).apply(data);
    }

    throw new IllegalStateException("'"+type+"' is not registered");
  }

  @SuppressWarnings("unchecked")
  private Vec2 readVec2(Object value) {
    if (!(value instanceof List)) {
      throw new IllegalStateException("vec2 must be a list of numbers, ie. [0.0, 0.0]. current type: "+value.getClass().getName());
    }
    List<Object> list = (List<Object>) value;
    if (list.size() < 2) {
      throw new IllegalStateException("vec2 must be a list of at least 2 numbers, ie. [0.0, 0.0]. current size of the list: "+list.size());
    }
    Number first = checkType(list.get(0), Number.class, "First component in Vec2 must be a Number, but is of type ");
    Number second = checkType(list.get(1), Number.class, "Second component in Vec2 must be a Number, but is of type ");

    return new Vec2(first.floatValue(), second.floatValue());
  }

  private LayoutManager readLayout(Object value) {
    if (value == null) {
      throw new IllegalStateException("layoutManager must not be null");
    }
    if (value instanceof String) {
      switch ((String)value) {
        case "nop":
          return new NopLayoutManager();
        default:
          throw new IllegalStateException("unknown layout manager: "+value);
      }
    }
    if (value instanceof Map) {
      Object layoutObject = readYamlObject(value);
      if (!(layoutObject instanceof LayoutManager)) {
        throw new IllegalStateException("layout manager must deserialize to an instance of LayoutManager, but is "+layoutObject.getClass().getName());
      }
      return (LayoutManager) layoutObject;
    }
    throw new IllegalStateException("layout manager must be a string or an object, but is "+value.getClass().getName());
  }

  private String readString(Object value) {
    return value.toString();
  }

  private <E> E checkType(Object o, Class<E> requiredType, String error) {
    if (!requiredType.isAssignableFrom(o.getClass())) {
      throw new IllegalStateException(error+ o.getClass().getName());
    }
    return requiredType.cast(o);
  }

  private <E> List<E> checkList(Object data, String property, Class<E> requiredListElementType) {
    if (data == null) {
      throw new IllegalStateException("'"+property+"' must not be null");
    }
    if (!(data instanceof List)) {
      throw new IllegalStateException("'"+property+"' must be a List, but is a "+data.getClass().getName());
    }
    List<Object> list = (List<Object>) data;
    for (int i = 0; i < list.size(); i++) {
      Object element = list.get(i);
      if (element == null) {
        throw new IllegalStateException("List Element "+i+" of '"+property+"' must not be null");
      }
      if (!requiredListElementType.isAssignableFrom(element.getClass())) {
        throw new IllegalStateException("List Element "+i+" of '"+property+"' must be of type "+requiredListElementType.getName()+", but is "+element.getClass().getName());
      }
    }

    return (List<E>) list;
  }


  private GameObject readGameObject(GameObject gameObject, Map<String, Object> data) {
    if (!gameObject.is("pos")) {
      gameObject.addComponents(PosComponent2d.pos(0,0));
    }
    for (Map.Entry<String, Object> entry : data.entrySet()) {
      // skip special keys and empty values
      String key = entry.getKey();
      if (entry.getValue() == null || Arrays.asList("type", "children").contains(key)) {
        continue;
      }
      if (!properties.containsKey(key)) {
        throw new IllegalStateException("property with name '"+key+"' not registered");
      }
      // read value
      Object value = properties.get(key).apply(entry.getValue());
      gameObject.set(key, value);
    }

    // read children
    Object children = data.get("children");
    if (children != null) {
      List<Object> childrenList = checkType(children, List.class, "children must be a list, but is a ");
      for (Object child : childrenList) {
        Map<String, Object> childProperties = checkType(child, Map.class, "child entry must be an object, but is a ");
        Object deserializedChild = readYamlObject(childProperties);
        GameObject childGameObject = checkType(deserializedChild, GameObject.class, "deserialized child must be a game object, but is a ");
        gameObject.add(childGameObject);
      }
    }
    return gameObject;
  }

  private Supplier<GameObject> readTemplate(Object dataObject) {
    return () -> (GameObject) readYamlObject(dataObject);
  }

  public static void main(String[] args) {
    new GuiPanelReader().read(GuiPanelReader.class.getResourceAsStream("/battlemesh/infopane.yaml"));

  }
}
