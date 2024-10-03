package com.berray.objects.gui;

import com.berray.GameObject;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyResolveService {
  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");

  private static final PropertyResolveService INSTANCE = new PropertyResolveService();

  private PropertyResolveService() {
  }

  public static PropertyResolveService getInstance() {
    return INSTANCE;
  }

  public static String replaceText(String text, Object dataObject) {
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

  public static Object getProperty(Object dataObject, String propertyName) {
    List<String> properties = splitPropertyNames(propertyName);
    return getProperty(dataObject, properties);
  }

  private static List<String> splitPropertyNames(String propertyName) {
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
  private static Object getProperty(Object object, List<String> propertyPath) {
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

  private static BiFunction<Object, String,  Object> getPropertyResolver(Object object) {
    if (object instanceof Map) {
      // property is a map key. valid are 'map.key' and 'map[key]'
      return PropertyResolveService::getMapProperty;
    } else if (object instanceof List) {
      return PropertyResolveService::getListProperty;
    } else if (object.getClass().isArray()) {
      return PropertyResolveService::getArrayProperty;
    } else if (object instanceof EventListenerCapable) {
      return PropertyResolveService::getEventListenerCapableProperty;
    } else if (object instanceof GameObject) {
      return PropertyResolveService::getGameObjectProperty;
    } else {
      return PropertyResolveService::getBeanProperty;
    }
  }

  private static Object getBeanProperty(Object object, String property) {
    try {
      String propertyBeanName = property.substring(0, 1).toUpperCase() + property.substring(1);
      return object.getClass().getMethod("get" + propertyBeanName).invoke(object);
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new IllegalArgumentException("cannot get property "+property+" from object of type "+object.getClass().getName(), e);
    }
  }

  private static Object getGameObjectProperty(Object object, String property) {
    return ((GameObject) object).get(property);
  }

  private static Object getEventListenerCapableProperty(Object object, String property) {
    return ((EventListenerCapable) object).getProperty(property);
  }

  private static Object getArrayProperty(Object object, String property) {
    return Array.get(object, toIndex(property));
  }

  private static int toIndex(String property) {
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
  private static String toMapKey(String property) {
    if (property.startsWith("[") && property.endsWith("]")) {
      return property.substring(1, property.length() - 1);
    }
    return property;
  }

  @SuppressWarnings("unchecked")
  private static Object getListProperty(Object object, String property) {
    return ((List<Object>) object).get(toIndex(property));
  }

  @SuppressWarnings("unchecked")
  private static Object getMapProperty(Object object, String property) {
    String key = toMapKey(property);
    return ((Map<?, Object>) object).get(key);
  }

  private Object getArrayIndex(Object propertyValue, String arrayIndex) {
    if (propertyValue == null) {
      return null;
    }
    if (propertyValue instanceof List) {
      int index = Integer.parseInt(arrayIndex);
      return ((List<?>) propertyValue).get(index);
    }
    if (propertyValue instanceof Map) {
      return ((Map<?, ?>) propertyValue).get(arrayIndex);
    }
    throw new IllegalStateException("cannot get array index from class " + propertyValue.getClass().getSimpleName());
  }

  private Object getPropertyValue(Object object, String property) {
    Class<?> valueClass = object.getClass();
    try {
      String beanName = Character.toUpperCase(property.charAt(0)) + property.substring(1);
      return valueClass.getMethod("get" + beanName).invoke(object);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException("cannot get property from class " + valueClass.getSimpleName() + " for property " + property, e);
    }
  }


}
