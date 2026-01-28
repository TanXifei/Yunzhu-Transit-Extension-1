package top.xfunny.mod.client.resource;

import top.xfunny.mod.client.DynamicTextureCache;
import top.xfunny.mod.client.client_data.DynamicResource;


import top.xfunny.mod.client.FontResourceGenerator;


import java.awt.*;


public class TextureList {
    public static TextureList instance = new TextureList();
    public DynamicResource renderFont(String id, Long location, String originalText, int textColor, Font font, float fontSize, int letterSpacing) {
        return DynamicTextureCache.instance.getResource(String.format("%s$%s", id, originalText), location, () -> FontResourceGenerator.generateNativeImage(originalText, textColor, font, fontSize, 0, letterSpacing));
    }

    public DynamicResource getTestLiftPanelDisplay(String originalText,long location, int textColor) {
        return  DynamicTextureCache.instance.getResource(String.format("test_lift_panel_display_%s", originalText),location,() -> FontResourceGenerator.generateNativeImage(originalText, textColor, FontList.instance.getFont("testfont"), 4, 0, 4));
    }
}
