package com.berray.assets;

import java.util.HashMap;
import java.util.Map;

public class SpriteAtlas {
  private Map<String, SpriteSheet> sheets = new HashMap<>();
  private String textureAsset;


  public SpriteAtlas(String textureAsset) {
    this.textureAsset = textureAsset;
  }

  public SpriteAtlas textureAsset(String textureAsset) {
    this.textureAsset = textureAsset;
    return this;
  }

  public SpriteAtlas sheet(String sheetName, SpriteSheet sheet) {
    if (textureAsset != null) {
      sheet.textureAsset(textureAsset);
      sheet.slice();
    }
    sheets.put(sheetName, sheet);
    return this;
  }

  public Map<String, SpriteSheet> getSheets() {
    return sheets;
  }

  public void slice() {
    for (SpriteSheet sheet: sheets.values()) {
      if (sheet.getTexture() == null) {
        sheet.textureAsset(textureAsset);
        sheet.slice();
      }
    }
  }


  public static SpriteAtlas atlas() {
    return new SpriteAtlas(null);
  }
  public static SpriteAtlas atlas(String textureAsset) {
    return new SpriteAtlas(textureAsset);
  }

}
