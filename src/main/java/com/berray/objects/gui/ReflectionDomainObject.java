package com.berray.objects.gui;

import com.berray.event.CoreEvents;
import com.berray.event.EventListener;
import com.berray.event.EventManager;
import com.berray.event.PropertyChangeEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ReflectionDomainObject implements EventListenerCapable {
  private final Object wrappedObject;
  private final Map<String, Method> getterMethods = new HashMap<>();
  private final Map<String, Method> setterMethods = new HashMap<>();
  private final List<String> properties;


  private EventManager eventManager = new EventManager();

  public ReflectionDomainObject(Object wrappedObject) {
    if (wrappedObject == null) {
      throw  new NullPointerException("wrapped object must not be null");
    }
    this.wrappedObject = wrappedObject;

    // get property getter and setter methods
    for (Method method : wrappedObject.getClass().getMethods()) {
      String methodName = method.getName();
      if (methodName.startsWith("get") && methodName.length() > 3 && method.getParameterCount() == 0) {
        // getter method
        String name = methodName.substring(3,4).toLowerCase(Locale.ROOT) + methodName.substring(4);
        getterMethods.put(name, method);
      } else if (methodName.startsWith("is") && methodName.length() > 2 && method.getParameterCount() == 0 && method.getReturnType().equals(boolean.class)) {
        // getter method for a boolean Property in the form if "isXxx()"
        String name = methodName.substring(2,3).toLowerCase(Locale.ROOT) + methodName.substring(3);
        getterMethods.put(name, method);
      } else if (methodName.startsWith("set") && methodName.length() > 3 && method.getParameterCount() == 1) {
        // getter method
        String name = methodName.substring(3,4).toLowerCase(Locale.ROOT) + methodName.substring(4);
        setterMethods.put(name, method);
      }
    }

    Set<String> propertySet = new HashSet<>(getterMethods.keySet());
    propertySet.addAll(setterMethods.keySet());
    properties = Collections.unmodifiableList(new ArrayList<>(propertySet));
  }

  @Override
  public void onPropertyChange(EventListener<? extends PropertyChangeEvent> listener) {
    eventManager.addEventListener(CoreEvents.PROPERTY_CHANGED, listener);
  }

  @Override
  public void onPropertyChange(EventListener<? extends PropertyChangeEvent> listener, Object owner) {
    eventManager.addEventListener(CoreEvents.PROPERTY_CHANGED, listener, owner);
  }

  @Override
  public void removeListener(Object owner) {
    eventManager.removeListener(owner);
  }

  @Override
  public void removeAllListeners() {
    eventManager.clear();
  }

  @Override
  public List<String> getProperties() {
    return new ArrayList<>(properties);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getProperty(String name) {
    Method getterMethod = getterMethods.get(name);
    if (getterMethod == null) {
      throw new IllegalArgumentException("unknown property "+name+" in domain object type "+wrappedObject.getClass().getName());
    }
      try {
        return (T) getterMethod.invoke(wrappedObject);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new IllegalStateException("cannot get property "+name+" in domain object type "+wrappedObject.getClass().getName(), e);
      }
  }

  @Override
  public <T> void setProperty(String name, T value) {
    Method setterMethod = setterMethods.get(name);
    if (setterMethod == null) {
      throw new IllegalArgumentException("unknown property "+name+" in domain object type "+wrappedObject.getClass().getName());
    }

    T old = getProperty(name);
    if (Objects.equals(old, value)) {
      return;
    }
    try {
      setterMethod.invoke(wrappedObject, value);
    } catch (Exception e) {
      throw new IllegalStateException("cannot set property "+name+" in domain object type "+wrappedObject.getClass().getName(), e);
    }
    eventManager.trigger(CoreEvents.PROPERTY_CHANGED, Arrays.asList(null, name, old, value));
  }
}
