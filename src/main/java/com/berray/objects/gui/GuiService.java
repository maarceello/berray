package com.berray.objects.gui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuiService {
  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");

  private GuiService() {
  }

  public static String replaceText(String text, EventListenerCapable dataObject) {
    // find placeholders
    StringBuilder result = new StringBuilder();
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
    int lastMatch = 0;
    while (matcher.find()) {
      // add text from last match to start of this match
      result.append(text, lastMatch, matcher.start());
      String propertyName = matcher.group(1);
      Object propertyValue = dataObject.getProperty(propertyName);
      if (propertyValue != null) {
        result.append(propertyValue);
      }
      lastMatch = matcher.end();
    }
    // append stuff after the last match
    result.append(text.substring(lastMatch));
    return result.toString();
  }
}
