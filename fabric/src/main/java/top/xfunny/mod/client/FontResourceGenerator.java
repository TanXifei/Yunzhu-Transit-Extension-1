package top.xfunny.mod.client;

import org.mtr.core.tool.Utilities;
import org.mtr.mapping.holder.*;
import org.mtr.mod.config.Config;
import org.mtr.mod.data.IGui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.AbstractMap;
import java.util.Locale;
import java.util.Map;

public class FontResourceGenerator implements IGui {

    /**
     * 生成字体图片NativeImage数据
     * 改为 public 方便外部调用
     */
    public static NativeImage generateNativeImage(String text, int textColor, Font font, float fontSize, int padding, int letterSpacing) {
        int scale = (int) Math.pow(2, Config.getClient().getDynamicTextureResolution() + 5);
        try {
            final int[] dimensions = new int[2];

            // 静态上下文现在可以正常调用静态方法 getTextPixels
            final Map.Entry<int[], byte[]> textInformation = getTextPixels(
                    text.toUpperCase(Locale.ENGLISH),
                    dimensions,
                    (float) scale / 8 * fontSize,
                    padding,
                    font,
                    letterSpacing
            );

            // 获取计算后的宽度和高度
            final int totalWidth = dimensions[0];
            final int totalHeight = Math.round(scale * 1.5F);

            // 创建 Minecraft 的 NativeImage
            final NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), totalWidth, totalHeight, true);
            // 初始化画布背景为透明
            nativeImage.fillRect(0, 0, totalWidth, totalHeight, 0);

            // 绘制文字像素到 NativeImage
            drawString(nativeImage, textInformation.getValue(), totalWidth / 2, totalHeight / 2, dimensions,
                    IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER,
                    0x00000000, textColor, false);

            return nativeImage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取文字像素
     */
    private static Map.Entry<int[], byte[]> getTextPixels(String text, int[] dimensions, float fontSize, int padding, Font font, int letterSpacing) {
        try {
            // 初始测量
            BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g2d = tempImage.createGraphics();
            Font renderFont = font.deriveFont(Font.PLAIN, fontSize);
            g2d.setFont(renderFont);

            FontMetrics metrics = g2d.getFontMetrics();
            int textWidth = 0;
            if (letterSpacing == 0) {
                textWidth = metrics.stringWidth(text);
            } else {
                for (char c : text.toCharArray()) {
                    textWidth += metrics.charWidth(c) + letterSpacing;
                }
                textWidth -= letterSpacing; // 减去最后一个字符多加的间距
            }

            int textHeight = metrics.getHeight();
            // 局部变量，防止多线程冲突
            int calculatedWidth = textWidth + 2 * padding;
            int calculatedHeight = textHeight + 2 * padding;

            dimensions[0] = calculatedWidth;
            dimensions[1] = calculatedHeight;

            // 正式绘制像素
            BufferedImage textImage = new BufferedImage(calculatedWidth, calculatedHeight, BufferedImage.TYPE_BYTE_GRAY);
            g2d = textImage.createGraphics();
            g2d.setFont(renderFont);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setColor(Color.WHITE);

            int x = padding;
            int y = padding + metrics.getAscent();

            if (letterSpacing == 0) {
                g2d.drawString(text, x, y);
            } else {
                for (char c : text.toCharArray()) {
                    g2d.drawString(String.valueOf(c), x, y);
                    x += metrics.charWidth(c) + letterSpacing;
                }
            }

            g2d.dispose();

            // 获取灰度像素数据
            byte[] pixels = ((DataBufferByte) textImage.getRaster().getDataBuffer()).getData();
            textImage.flush();

            return new AbstractMap.SimpleEntry<>(dimensions, pixels);
        } catch (Exception e) {
            dimensions[0] = 0;
            dimensions[1] = 0;
            return new AbstractMap.SimpleEntry<>(dimensions, new byte[0]);
        }
    }

    private static void drawString(NativeImage nativeImage, byte[] pixels, int x, int y, int[] textDimensions,
                                   IGui.HorizontalAlignment horizontalAlignment,
                                   IGui.VerticalAlignment verticalAlignment,
                                   int backgroundColor, int textColor, boolean rotate90) {
        // ARGB/ABGR 分量提取
        final int textR = (textColor >> 16) & 0xFF;
        final int textG = (textColor >> 8) & 0xFF;
        final int textB = textColor & 0xFF;

        // 背景绘制逻辑
        if (((backgroundColor >> 24) & 0xFF) > 0) {
            for (int drawY = 0; drawY < textDimensions[rotate90 ? 0 : 1]; drawY++) {
                for (int drawX = 0; drawX < textDimensions[rotate90 ? 1 : 0]; drawX++) {
                    drawPixelSafe(nativeImage,
                            (int) horizontalAlignment.getOffset(drawX + x, textDimensions[rotate90 ? 1 : 0]),
                            (int) verticalAlignment.getOffset(drawY + y, textDimensions[rotate90 ? 0 : 1]),
                            backgroundColor);
                }
            }
        }

        int drawX = 0;
        int drawY = rotate90 ? textDimensions[0] - 1 : 0;

        for (int i = 0; i < textDimensions[0] * textDimensions[1]; i++) {
            final int alpha = pixels[i] & 0xFF;
            if (alpha > 0) {
                final int xPos = (int) horizontalAlignment.getOffset(x + drawX, textDimensions[rotate90 ? 1 : 0]);
                final int yPos = (int) verticalAlignment.getOffset(y + drawY, textDimensions[rotate90 ? 0 : 1]);

                if (Utilities.isBetween(xPos, 0, nativeImage.getWidth() - 1) &&
                        Utilities.isBetween(yPos, 0, nativeImage.getHeight() - 1)) {

                    final int existingPixel = nativeImage.getColor(xPos, yPos);
                    final int existingA = (existingPixel >> 24) & 0xFF;

                    if (existingA == 0) {
                        // 目标像素为空，直接填充
                        final int newColor = (alpha << 24) | (textB << 16) | (textG << 8) | textR;
                        nativeImage.setPixelColor(xPos, yPos, newColor);
                    } else {
                        // 进行像素混合
                        blendPixel(nativeImage, xPos, yPos, alpha, textR, textG, textB);
                    }
                }
            }

            if (rotate90) {
                drawY--;
                if (drawY < 0) {
                    drawY = textDimensions[0] - 1;
                    drawX++;
                }
            } else {
                drawX++;
                if (drawX == textDimensions[0]) {
                    drawX = 0;
                    drawY++;
                }
            }
        }
    }

    private static void drawPixelSafe(NativeImage nativeImage, int x, int y, int color) {
        if (Utilities.isBetween(x, 0, nativeImage.getWidth() - 1) && Utilities.isBetween(y, 0, nativeImage.getHeight() - 1)) {
            nativeImage.setPixelColor(x, y, color);
        }
    }

    private static void blendPixel(NativeImage nativeImage, int x, int y, int alpha, int r, int g, int b) {
        final int existingPixel = nativeImage.getColor(x, y);
        final int existingA = (existingPixel >> 24) & 0xFF;
        final int existingR = existingPixel & 0xFF;
        final int existingG = (existingPixel >> 8) & 0xFF;
        final int existingB = (existingPixel >> 16) & 0xFF;

        final int invAlpha = 255 - alpha;
        final int newR = (existingR * invAlpha + r * alpha) / 255;
        final int newG = (existingG * invAlpha + g * alpha) / 255;
        final int newB = (existingB * invAlpha + b * alpha) / 255;
        final int newA = Math.min(255, existingA + alpha);

        final int finalColor = (newA << 24) | (newB << 16) | (newG << 8) | newR;
        nativeImage.setPixelColor(x, y, finalColor);
    }
}