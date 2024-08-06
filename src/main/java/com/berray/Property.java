package com.berray;

/** Used to add a custom property to a game object via {@link GameObject#add(Object...)} */
public class Property<E> {

  private final String name;
  private final E value;

  public Property(String name, E value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public E getValue() {
    return value;
  }

  public static <E> Property<E> property(String name, E value) {
    return new Property<E>(name, value);
  }
}
