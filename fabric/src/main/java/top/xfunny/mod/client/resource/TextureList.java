package top.xfunny.mod.client.resource;

import top.xfunny.mod.client.DynamicResource;


import top.xfunny.mod.client.FontResourceGenerator;


import java.awt.*;


public class TextureList {
    public static TextureList instance = new TextureList();

    public DynamicResource renderFont(String id, String originalText, int textColor, Font font, float fontSize, int letterSpacing) {
        return FontResourceGenerator.getInstance().getResource(String.format("%s$%s", id, originalText), originalText, textColor, font, fontSize, 0, letterSpacing, FontResourceGenerator.DefaultRenderingColor.TRANSPARENT);
    }

    public DynamicResource getTestLiftButtonsDisplay(String originalText, int textColor) {
        return FontResourceGenerator.getInstance().getResource(String.format("test_lift_buttons_display_%s", originalText), originalText, textColor, FontList.instance.getFont("testfont"), 4, 0, 4, FontResourceGenerator.DefaultRenderingColor.TRANSPARENT);
    }

    public DynamicResource getTestLiftPanelDisplay(String originalText, int textColor) {
        return FontResourceGenerator.getInstance().getResource(String.format("test_lift_panel_display_%s", originalText),originalText, textColor, FontList.instance.getFont("testfont"), 4, 0, 4, FontResourceGenerator.DefaultRenderingColor.TRANSPARENT);
    }
}
