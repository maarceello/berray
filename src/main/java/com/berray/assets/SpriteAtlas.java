package com.berray.assets;

import com.raylib.Raylib;

import java.util.HashMap;
import java.util.Map;

public class SpriteAtlas {
  private Map<String, SpriteSheet> sheets = new HashMap<>();

  public SpriteAtlas() {
  }

  public SpriteAtlas sheet(String sheetName, SpriteSheet sheet) {
    sheets.put(sheetName, sheet);
    return this;
  }

  public static SpriteAtlas atlas() {
    return new SpriteAtlas();
  }

  public void slice(Raylib.Texture sprite) {
    for (SpriteSheet sheet : sheets.values()) {
      sheet.slice(sprite);
    }
  }

  public Map<String, SpriteSheet> getSheets() {
    return sheets;
  }
}
