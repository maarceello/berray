package com.berray.objects.gui.layout;

import com.berray.GameObject;
import com.berray.math.Rect;
import com.berray.math.Vec2;
import com.berray.objects.gui.Container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Layout manager which places the components along horizontally. The width of the components are calculated from a
 * format string and the height is either a fixed height or the maximum height of all the minimum heights of the
 * components.
 * <p>
 * When one component cannot be scaled to fit the calculated box the component is centered in the box.
 * <p>
 * The format string id s comma separated list of widths specifiers which consists of a number and a unit.
 * Supported specifiers are:
 * <ul>
 * <li>50px - width is 50 px</li>
 * <li>5em - width is 5 times the current font height</li>
 * <li>* - this column is variable width and takes the remaining space of the row. There can be only one column of this type.</li>
 * <li>5* - this column is variable width. see below. </li>
 * </ul>
 * <p>
 * There can be columns with variable width. Either there is one column with type '*'. This column takes all the remaining space in the row.
 * Or there are multiple variable width columns with relative width specifier. For example "2*,1*" creates two columns, where column one is double the width of column two.
 * So column one takes two thirds of the space and column two takes one third of the space.
 */
public class RowLayout implements LayoutManager {

  private List<SizeSpecifier> columnSpecifiers = new ArrayList<>();
  private SizeSpecifier height = null;

  public void setHeightString(String heightString) {
    this.height = SizeSpecifier.fromString(heightString);
  }

  public void setWidthString(String widthString) {
    Objects.requireNonNull(widthString, "widthString");
    String[] columnSpecs = widthString.split(";");
    if (columnSpecs.length == 0) {
      throw new IllegalStateException("empty column string");
    }
    for (String columnSpec : columnSpecs) {
      columnSpecifiers.add(SizeSpecifier.fromString(columnSpec));
    }
  }


  @Override
  public void layoutPanel(Container panel, List<GameObject> componentsToLayout, Rect destination) {
    Vec2 containerSize = destination.getSize();
    if (containerSize == null) {
      throw  new IllegalStateException("container can not be layed out because it has no size. tags of container: "+panel.getTags());
    }

    // calculate fixed and dynamic widths
    float[] width = new float[columnSpecifiers.size()];

    float fontSize = panel.getLookAndFeelManager().getFontSize("default");

    float rowHeight = height != null ? height.getSize(fontSize) : 0;
    float usedWidth = 0;
    float sumVariableParts = 0;
    int numVariableParts = 0;
    for (int i = 0; i < columnSpecifiers.size(); i++) {
      SizeSpecifier specs = columnSpecifiers.get(i);
      if (specs.sizeUnit == SizeUnit.VARIABLE) {
        sumVariableParts += specs.amount;
        numVariableParts++;
      } else {
        float pixels = specs.getSize(fontSize);
        width[i] = pixels;
        usedWidth += pixels;
      }
    }

    float remainingWidth = containerSize.getX() - usedWidth;
    if (remainingWidth < 0) {
      throw new IllegalStateException("used fixed space ("+usedWidth+") is more than the container allows ("+containerSize.getX()+")");
    }

    for (int i = 0; i < columnSpecifiers.size(); i++) {
      SizeSpecifier specs = columnSpecifiers.get(i);
      if (specs.sizeUnit == SizeUnit.VARIABLE) {
        if (numVariableParts == 1) {
          // when only one variable part is requested, this takes up all the remaining space.
          // the "amount" factor is ignored and doesn't even have to be supplied
          width[i] = remainingWidth;
        }
        else {
          if (specs.amount == 0.0f) {
            throw new IllegalStateException("more than one variable column is specified, but colum index "+i+" does not has an amount specified");
          }
          width[i] = (remainingWidth * specs.amount) / sumVariableParts;
        }
      }
    }


    // place and resize components
    float currentX = destination.getX();
    for (int i = 0; i < Math.min(width.length, componentsToLayout.size()); i++) {
      GameObject component = componentsToLayout.get(i);
      if (component.isWritable("size")) {
        component.set("size", new Vec2(width[i], rowHeight));
      }
      component.set("pos", new Vec2(currentX, destination.getY()));
      currentX += width[i];
    }
  }


  private static class SizeSpecifier {
    private final float amount;
    private final SizeUnit sizeUnit;

    public SizeSpecifier(float amount, SizeUnit sizeUnit) {
      this.amount = amount;
      this.sizeUnit = sizeUnit;
    }

    public float getAmount() {
      return amount;
    }

    public SizeUnit getSizeUnit() {
      return sizeUnit;
    }

    public float getSize(float fontSize) {
      return amount * (getSizeUnit() == SizeUnit.EM ? fontSize : 1.0f);
    }

    public static SizeSpecifier fromString(String s) {
      // check ending
      for (SizeUnit sizeUnit : SizeUnit.values()) {
        if (s.endsWith(sizeUnit.getValue())) {
          // try to parse the remain as a float
          String valueString = s.substring(0, s.length() - sizeUnit.getValue().length()).trim();
          if (sizeUnit == SizeUnit.VARIABLE && valueString.length() == 0) {
            // the variable unit can be used without an amount
            return new SizeSpecifier(0, sizeUnit);
          }
          try {
            float value = Float.parseFloat(valueString);
            return new SizeSpecifier(value, sizeUnit);
          } catch (NumberFormatException e) {
            throw new IllegalStateException("no valid (float) value found in string '" + s + "'", e);
          }

        }
      }
      throw new IllegalStateException("no valid size unit found in string '" + s + "'");
    }

  }

  private enum SizeUnit {
    PX("px"),
    EM("em"),
    VARIABLE("*");

    private final String value;

    SizeUnit(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public static SizeUnit fromValue(String value) {
      for (SizeUnit sizeUnit : values()) {
        if (sizeUnit.value.equals(value)) {
          return sizeUnit;
        }
      }
      throw new IllegalArgumentException("unknown size unit: " + value);
    }
  }
}
