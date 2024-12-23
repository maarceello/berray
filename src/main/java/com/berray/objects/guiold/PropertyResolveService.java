package com.berray.objects.guiold;

import com.berray.GameObject;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyResolveService {
  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{(.+?)}");

  private static final PropertyResolveService INSTANCE = new PropertyResolveService();

  private PropertyResolveService() {
  }

  public static PropertyResolveService getInstance() {
    return INSTANCE;
  }

  public String replaceText(String text, Object dataObject) {
    // find placeholders
    StringBuilder result = new StringBuilder();
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
    int lastMatch = 0;
    while (matcher.find()) {
      // add text from last match to start of this match
      result.append(text, lastMatch, matcher.start());
      String propertyName = matcher.group(1);
      Object value = getProperty(dataObject, propertyName);
      if (value != null) {
        result.append(value);
      }
      lastMatch = matcher.end();
    }
    // append stuff after the last match
    result.append(text.substring(lastMatch));
    return result.toString();
  }

  public Object getProperty(Object dataObject, String propertyName) {
    List<String> properties = splitPropertyNames(propertyName);
    return getProperty(dataObject, properties);
  }

  public void setProperty(Object dataObject, String propertyName, Object newValue) {
    List<String> properties = splitPropertyNames(propertyName);
    setProperty(dataObject, properties, newValue);
  }


  private List<String> splitPropertyNames(String propertyName) {
    List<String> properties = Arrays.asList(propertyName.split("\\."));
    // check if one of the properties is an array or map lookup an split those
    List<String> finalProperties = new ArrayList<>();
    for (String property : properties) {
      int arrayStart = property.indexOf('[');
      if (arrayStart != -1) {
        // split in propertyname and array access
        finalProperties.add(property.substring(0, arrayStart));
        finalProperties.add(property.substring(arrayStart));
      } else {
        finalProperties.add(property);
      }
    }
    return finalProperties;
  }

  /**
   * Returns the value of the property path. Returns null when one of the object on the path is null.
   */
  private void setProperty(Object object, List<String> propertyPath, Object value) {
    // current object is null...don't do anything
    if (object == null) {
      return;
    }
    // property path is empty. fail.
    if (propertyPath.isEmpty()) {
      throw new IllegalStateException("cannot set property: property name is missing");
    }

    // resolve all properties until the second to last one.
    Object destinationObject = getProperty(object, propertyPath.subList(0, propertyPath.size() - 1));
    String property = propertyPath.get(propertyPath.size() - 1);

    PropertySetter propertySetter = getPropertySetter(object);
    propertySetter.accept(destinationObject, property, value);
  }

  /**
   * Returns the value of the property path. Returns null when one of the object on the path is null.
   */
  private Object getProperty(Object object, List<String> propertyPath) {
    // current object is null...return the null
    if (object == null) {
      return null;
    }
    // property path is empty. return the current object
    if (propertyPath.isEmpty()) {
      return object;
    }

    String property = propertyPath.get(0);

    BiFunction<Object, String,  Object> propertyResolver = getPropertyResolver(object);
    Object value = propertyResolver.apply(object, property);
    return getProperty(value, propertyPath.subList(1, propertyPath.size()));
  }

  private BiFunction<Object, String,  Object> getPropertyResolver(Object object) {
    if (object instanceof Map) {
      // property is a map key. valid are 'map.key' and 'map[key]'
      return this::getMapProperty;
    } else if (object instanceof List) {
      return this::getListProperty;
    } else if (object.getClass().isArray()) {
      return this::getArrayProperty;
    } else if (object instanceof EventListenerCapable) {
      return this::getEventListenerCapableProperty;
    } else if (object instanceof GameObject) {
      return this::getGameObjectProperty;
    } else {
      return this::getBeanProperty;
    }
  }

  private Object getBeanProperty(Object object, String property) {
    try {
      String propertyBeanName = property.substring(0, 1).toUpperCase() + property.substring(1);
      return object.getClass().getMethod("get" + propertyBeanName).invoke(object);
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new IllegalArgumentException("cannot get property "+property+" from object of type "+object.getClass().getName(), e);
    }
  }

  private Object getGameObjectProperty(Object object, String property) {
    return ((GameObject) object).get(property);
  }

  private Object getEventListenerCapableProperty(Object object, String property) {
    return ((EventListenerCapable) object).getProperty(property);
  }

  private Object getArrayProperty(Object object, String property) {
    return Array.get(object, toIndex(property));
  }

  private int toIndex(String property) {
    if (property.startsWith("[") && property.endsWith("]")) {
      try {
        return Integer.parseInt(property.substring(1, property.length() - 1));
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("property " + property + " is not an array index.", e);
      }
    }
    throw new IllegalArgumentException("property " + property + " is not an array index, but current object is a list or an array.");
  }

  /**
   * converts the property to a map key. supported are 'map.key' and 'map[key]'.
   */
  private String toMapKey(String property) {
    if (property.startsWith("[") && property.endsWith("]")) {
      return property.substring(1, property.length() - 1);
    }
    return property;
  }

  @SuppressWarnings("unchecked")
  private Object getListProperty(Object object, String property) {
    List<Object> list = (List<Object>) object;
    if (property.equals("size")) {
      return list.size();
    }
    return list.get(toIndex(property));
  }

  @SuppressWarnings("unchecked")
  private Object getMapProperty(Object object, String property) {
    Map<?, Object> map = (Map<?, Object>) object;
    if (property.equals("size")) {
      return map.size();
    }
    String key = toMapKey(property);
    return map.get(key);
  }

  private PropertySetter getPropertySetter(Object object) {
    if (object instanceof Map) {
      // property is a map key. valid are 'map.key' and 'map[key]'
      return this::setMapProperty;
    } else if (object instanceof List) {
      return this::setListProperty;
    } else if (object.getClass().isArray()) {
      return this::setArrayProperty;
    } else if (object instanceof EventListenerCapable) {
      return this::setEventListenerCapableProperty;
    } else if (object instanceof GameObject) {
      return this::setGameObjectProperty;
    } else {
      return this::setBeanProperty;
    }
  }

  @SuppressWarnings("unchecked")
  private void setListProperty(Object object, String property, Object value) {
    List<Object> list = (List<Object>) object;
    list.set(toIndex(property), value);
  }

  @SuppressWarnings("unchecked")
  private void setMapProperty(Object object, String property, Object value) {
    Map<String, Object> map = (Map<String, Object>) object;
    String key = toMapKey(property);
    map.put(key, value);
  }

  private void setArrayProperty(Object object, String property, Object value) {
    Array.set(object, toIndex(property), value);
  }

  private void setEventListenerCapableProperty(Object object, String property, Object value) {
    ((EventListenerCapable) object).setProperty(property, value);
  }

  private void setGameObjectProperty(Object object, String property, Object value) {
    ((GameObject) object).set(property, value);
  }

  private void setBeanProperty(Object object, String property, Object value) {
    try {
      String propertyBeanName = property.substring(0, 1).toUpperCase() + property.substring(1);
      Method setter = Arrays.stream(object.getClass().getMethods())
          .filter(method -> method.getName().equals("set" + propertyBeanName))
          .filter(method -> method.getParameterCount() == 1)
          .filter(method -> value == null || method.getParameterTypes()[0].isAssignableFrom(value.getClass()))
          .findFirst().orElseThrow(() -> new IllegalStateException("no setter for property "+property+" found in "+object.getClass()));
      setter.invoke(object, value);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new IllegalArgumentException("cannot get property "+property+" from object of type "+object.getClass().getName(), e);
    }
  }


  @FunctionalInterface
  private interface PropertySetter {
    void accept(Object destination, String property, Object value);
  }
}
