package com.berray.assets;


import com.raylib.Raylib;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * {@link AssetManager} in which the asset tree is based on a java bean. The bean properties are separated by dots.
 * Array indices are allowed, ie "foobarList[4].property"
 */
public class BeanAssetManager<T> extends BaseAssetManager {

  private final T asset;

  public BeanAssetManager(T asset) {
    if (asset == null) {
      throw new IllegalArgumentException("asset may not be null");
    }
    this.asset = asset;
  }

  public T getAsset() {
    return asset;
  }

  @Override
  public Asset getAsset(String name) {
    List<String> properties = Arrays.asList(name.split(PROPERTY_SEPARATOR));
    Object property = getProperty(asset, properties);
    if (property == null) {
      throw new IllegalArgumentException("asset "+name+" not found");
    }
    return resolveAsset(property);
  }

  private Asset resolveAsset(Object property) {
    if (property instanceof Asset) {
      return (Asset) property;
    }
    if (property instanceof Raylib.Texture) {
      return new Asset(null, AssetType.SPRITE, property);
    }
    if (property instanceof SpriteSheet) {
      return new Asset(null, AssetType.SPRITE_SHEET, property);
    }
    if (property instanceof SpriteAtlas) {
      return new Asset(null, AssetType.SPRITE_ATLAS, property);
    }
    throw new IllegalStateException("unknown asset type: "+property.getClass());
  }

  /**
   * Returns the value of the property path. Returns null when one of the object on the path is null.
   */
  private Object getProperty(Object object, List<String> propertyPath) {
    if (object == null) {
      return null;
    }
    if (propertyPath.isEmpty()) {
      return object;
    }

    String property = propertyPath.get(0);
    String arrayIndex = null;

    // do we have an array lookup?
    int arrayStart = property.indexOf("[");
    if (arrayStart > -1) {
      int arrayEnd = property.lastIndexOf("]");
      if (arrayEnd < 1) {
        throw new IllegalStateException("missed closing array bracket ']' in property " + property);
      }
      arrayIndex = property.substring(arrayStart + 1, arrayEnd);
      property = property.substring(0, arrayStart);
    }

    // get Property from getter
    Object propertyValue = getPropertyValue(object, property);
    // if we should do an array lookup
    if (arrayIndex != null) {
      propertyValue = getArrayIndex(propertyValue, arrayIndex);
    }
    return getProperty(propertyValue, propertyPath.subList(1, propertyPath.size()));
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
    throw  new IllegalStateException("cannot get array index from class "+propertyValue.getClass().getSimpleName());
  }

  private Object getPropertyValue(Object object, String property) {
    Class<?> valueClass = object.getClass();
    try {
      String beanName = Character.toUpperCase(property.charAt(0)) + property.substring(1);
      return valueClass.getMethod("get" + beanName).invoke(object);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException("cannot get property from class " +valueClass.getSimpleName()+" for property "+property, e);
    }
  }

}
