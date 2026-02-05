package top.xfunny.mod.client.resource;

import top.xfunny.mod.client.DynamicTextureCache;
import top.xfunny.mod.client.FontResourceGenerator;
import top.xfunny.mod.client.client_data.DynamicResource;
import java.awt.*;

public class TextureList {
    public static TextureList instance = new TextureList();

    public DynamicResource renderFont(String id, Long blockPos, String originalText, int textColor, Font font, float fontSize, int letterSpacing) {
        // ID 格式保持为 "name$text"，DynamicTextureCache 会负责解析
        String textureId = String.format("%s$%s", id, originalText);

        return DynamicTextureCache.instance.getResource(
                textureId,
                blockPos,
                () -> FontResourceGenerator.generateNativeImage(originalText, textColor, font, fontSize, 0, letterSpacing)
        );
    }

    // 弃用的方法可以删除或保留，不影响逻辑
    public DynamicResource getTestLiftPanelDisplay(String originalText, int textColor) {
        return DynamicTextureCache.instance.getResource(
                String.format("test_lift_panel_display_%s", originalText),
                null,
                () -> FontResourceGenerator.generateNativeImage(originalText, textColor, FontList.instance.getFont("testfont"), 4, 0, 4)
        );
    }
}