package top.xfunny.mod.client;

import org.mtr.core.servlet.MessageQueue;
import org.mtr.core.tool.Utilities;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2LongArrayMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.mtr.mapping.holder.*;
import org.mtr.mod.config.Config;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.MainRenderer;
import oshi.util.tuples.Pair;
import top.xfunny.mod.Init;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.util.ImageGenerator;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.AbstractMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class FontResourceGenerator {
    private FontResourceGenerator(){};
    private static FontResourceGenerator instance;
    public static synchronized FontResourceGenerator getInstance() {
        if (instance == null) {
            instance = new FontResourceGenerator();
        }
        return instance;
    }

    private static final int COOL_DOWN_TIME = 10000;
    private static final Identifier DEFAULT_BLACK_RESOURCE = new Identifier(Init.MOD_ID, "textures/block/black.png");
    private static final Identifier DEFAULT_WHITE_RESOURCE = new Identifier(Init.MOD_ID, "textures/block/white.png");
    private static final Identifier DEFAULT_TRANSPARENT_RESOURCE = new Identifier(Init.MOD_ID, "textures/block/transparent.png");
    private final Object2ObjectLinkedOpenHashMap<String, DynamicResource> dynamicResources = new Object2ObjectLinkedOpenHashMap<>();
    private final Object2LongArrayMap<Identifier> deletedResources = new Object2LongArrayMap<>();
    private final ObjectOpenHashSet<String> generatingResources = new ObjectOpenHashSet<>();
    private final MessageQueue<Runnable> resourceRegistryQueue = new MessageQueue<>();
    // 最近的贴图缓存：最多保留两个 id
    // 外层：id
    // 内层：每个 id 对应一个 LRU Map，最多缓存 2 个 originalText
    private final Object2ObjectLinkedOpenHashMap<String, Object2ObjectLinkedOpenHashMap<String, DynamicResource>> lastResourceById = new Object2ObjectLinkedOpenHashMap<>();



    private int totalWidth;
    private int totalHeight;

    public DynamicResource getResource (String key, String text, int textColor, Font font, float fontSize, int padding, int letterSpacing, DefaultRenderingColor defaultRenderingColor){


        resourceRegistryQueue.process(Runnable::run);
        final DynamicResource dynamicResource = dynamicResources.get(key);

        String idOnly = key.contains("$") ? key.substring(0, key.indexOf("$")) : key;
        String originalText = key.contains("$") ? key.substring(key.indexOf("$") + 1) : key;

        //测试用代码
        String target = "toshiba_screen_2_display_0_39032664879045";
        int dollar = key.indexOf('$');
        boolean match =
                dollar == target.length()
                        && key.startsWith(target);
//。

        if (dynamicResource != null && !dynamicResource.needsRefresh) {// 已生成且不需要刷新
            dynamicResource.expiryTime = System.currentTimeMillis() + COOL_DOWN_TIME;
            return dynamicResource;
        }


        if (generatingResources.size() >= 100 || generatingResources.contains(key) ) {// 如果正在生成中
            Object2ObjectLinkedOpenHashMap<String, DynamicResource> pool = lastResourceById.get(idOnly);
            if (pool !=null && !pool.isEmpty()) {
                if(match){
                    Init.LOGGER.info("返回缓存"+pool.firstKey());
                }

                return pool.get(pool.lastKey());
            }
            return defaultRenderingColor.dynamicResource;
        }

        MainRenderer.WORKER_THREAD.scheduleDynamicTextures(() -> {
            if(match){
                Init.LOGGER.info("开始生成"+key);
            }

            final NativeImage nativeImage = generateNativeImage(text, textColor, font, fontSize, padding, letterSpacing);
            resourceRegistryQueue.put(() -> {
                final DynamicResource staticTextureProviderOld = dynamicResources.get(key);
                if (staticTextureProviderOld != null) {
                    staticTextureProviderOld.remove();
                    deletedResources.put(staticTextureProviderOld.identifier, System.currentTimeMillis() + COOL_DOWN_TIME);
                }

                if (nativeImage != null) {
                    final NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(nativeImage);
                    final Identifier identifier = new Identifier(Init.MOD_ID, "id_" + Utilities.numberToPaddedHexString(new Random().nextLong()).toLowerCase(Locale.ENGLISH));
                    MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, new AbstractTexture(nativeImageBackedTexture.data));
                    final DynamicResource dynamicResourceNew = new DynamicResource(identifier, nativeImageBackedTexture);
                    dynamicResources.put(key, dynamicResourceNew);

                    putLastResource(idOnly, originalText, dynamicResourceNew);
                }
                generatingResources.remove(key);
            });
        });

        generatingResources.add(key);

        if (dynamicResource == null) {
            Object2ObjectLinkedOpenHashMap<String, DynamicResource> pool = lastResourceById.get(idOnly);
            if (pool != null && !pool.isEmpty()) {//pool 不为空
                return pool.get(pool.lastKey());
            }
            return defaultRenderingColor.dynamicResource;
        } else {// 现在正在显示的字符
            dynamicResource.expiryTime = System.currentTimeMillis() + COOL_DOWN_TIME;
            dynamicResource.needsRefresh = false;
            //putLastResource(idOnly, originalText, dynamicResource);
            return dynamicResource;
        }
    }


    /**
     * 获取文字像素
     * @param text 文本
     * @param dimensions 原始尺寸
     * @param fontSize 字体大小(字号，非字体图片大小)
     * @param padding 字体图片内间距
     * @param font 字体
     * @param letterSpacing 字符间距
     * @return 字体图片原始像素数据
     */
    private Map.Entry<int[], byte[]> getTextPixels(String text, int[] dimensions, float fontSize, int padding, Font font, int letterSpacing) {
        totalWidth = 0;

        try {
            BufferedImage textImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g2d = textImage.createGraphics();

            Font renderFont = font.deriveFont(Font.PLAIN, fontSize);
            g2d.setFont(renderFont);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            FontMetrics metrics = g2d.getFontMetrics();
            int textWidth = 0;
            if (letterSpacing == 0) {
                textWidth = metrics.stringWidth(text);
            } else {
                for (char c : text.toCharArray()) {
                    textWidth += metrics.charWidth(c) + letterSpacing;
                }
                textWidth -= letterSpacing;
            }

            int textHeight = metrics.getHeight();
            totalWidth = textWidth + 2 * padding;
            totalHeight = textHeight + 2 * padding;

            dimensions[0] = totalWidth;
            dimensions[1] = totalHeight;

            textImage = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_BYTE_GRAY);
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

            byte[] pixels = ((DataBufferByte) textImage.getRaster().getDataBuffer()).getData();
            textImage.flush();

            return new AbstractMap.SimpleEntry<>(dimensions, pixels);// 返回图像尺寸、像素数据
        } catch (Exception e) {
            dimensions[0] = 0;
            dimensions[1] = 0;
            return new AbstractMap.SimpleEntry<>(dimensions,new byte[0]);
        }
    }

    public void tick() {
        final ObjectArrayList<String> keysToRemove = new ObjectArrayList<>();
        dynamicResources.forEach((checkKey, checkDynamicResource) -> {
            String idOnly = checkKey.contains("$") ? checkKey.substring(0, checkKey.indexOf("$")) : checkKey;
            String originalText = checkKey.contains("$") ? checkKey.substring(checkKey.indexOf("$") + 1) : checkKey;

            // 如果在缓存中存在originalText，则不删除资源
            if (lastResourceById.containsKey(idOnly)) {
                Object2ObjectLinkedOpenHashMap<String, DynamicResource> pool = lastResourceById.get(idOnly);
                if (pool.containsKey(originalText)) {
                    return;
                }
            }

            if (checkDynamicResource.expiryTime < System.currentTimeMillis()) {
                checkDynamicResource.remove();
                deletedResources.put(checkDynamicResource.identifier, System.currentTimeMillis() + COOL_DOWN_TIME);
                keysToRemove.add(checkKey);
            }
        });
        keysToRemove.forEach(dynamicResources::remove);

        final ObjectArrayList<Identifier> deletedResourcesToRemove = new ObjectArrayList<>();
        deletedResources.forEach((identifier, expiryTime) -> {
            if (expiryTime < System.currentTimeMillis()) {
                MinecraftClient.getInstance().getTextureManager().destroyTexture(identifier);
                deletedResourcesToRemove.add(identifier);
            }
        });
        deletedResourcesToRemove.forEach(deletedResources::removeLong);
    }

    public void reload() {
        FontList.instance.FontReload();
        dynamicResources.values().forEach(dynamicResource -> dynamicResource.needsRefresh = true);
        generatingResources.clear();
        lastResourceById.clear();
    }


    //静态类

    /**
     * 生成字体图片NativeImage数据
     * @param text 文本
     * @param textColor 文本颜色
     * @param font 字体
     * @param fontSize 字体大小(字号，非字体图片大小)
     * @param padding 字体图片内间距
     * @param letterSpacing 字符间距
     * @return 字体图片NativeImage数据
     */
    private static NativeImage generateNativeImage(String text, int textColor, Font font, float fontSize, int padding, int letterSpacing){
        int scale = (int) Math.pow(2, Config.getClient().getDynamicTextureResolution() + 5);
        try {
            final int[] dimensions = new int[2];// 尺寸
            final int totalWidth;
            final int totalHeight;
            // 调用本类单例中的getTextPixels(), 获取字体图片原始尺寸以及生成的像素数据
            final Map.Entry<int[], byte[]> textInformation = FontResourceGenerator.instance.getTextPixels(
                    text.toUpperCase(Locale.ENGLISH),
                    dimensions,
                    (float) scale / 8 * fontSize,
                    padding,
                    font,
                    letterSpacing
            );

            // 赋值字体图片原始尺寸
            totalWidth = dimensions[0];
            totalHeight = Math.round(scale * 1.5F);

            // 新建NativeImage
            final NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), totalWidth, totalHeight, true);
            nativeImage.fillRect(0, 0, totalWidth, totalHeight, 0);

            drawString(nativeImage, textInformation.getValue(), totalWidth / 2, totalHeight / 2, dimensions,
                    IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER,
                    0x00000000, textColor, false);

            //String fileName = "test_img/output_" + System.currentTimeMillis() + ".png";
            //ImageGenerator.saveNativeImageAsPng(nativeImage, fileName);

            return nativeImage;
        }catch (Exception ignored){
            return null;
        }
    }


    /**
     * 自定义字体绘制方法
     * @param nativeImage 空的NativeImage数据（画布）
     * @param pixels 字体图片原始像素数据
     * @param x 绘制位置（起始x）
     * @param y 绘制位置（起始y）
     * @param textDimensions 字体图片原始尺寸
     * @param horizontalAlignment 水平对齐
     * @param verticalAlignment 垂直对齐
     * @param backgroundColor 背景颜色
     * @param textColor 文本颜色
     * @param rotate90 是否旋转90度
     */
    private static void drawString(NativeImage nativeImage, byte[] pixels, int x, int y, int[] textDimensions,
                                   IGui.HorizontalAlignment horizontalAlignment,
                                   IGui.VerticalAlignment verticalAlignment,
                                   int backgroundColor, int textColor, boolean rotate90) {
        // 提前计算颜色分量
        final int textR = (textColor >> 16) & 0xFF;
        final int textG = (textColor >> 8) & 0xFF;
        final int textB = textColor & 0xFF;

        // 背景不透明时绘制背景
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

        // 优化像素绘制循环
        int drawX = 0;
        int drawY = rotate90 ? textDimensions[0] - 1 : 0;

        for (int i = 0; i < textDimensions[0] * textDimensions[1]; i++) {
            final int alpha = pixels[i] & 0xFF;
            if (alpha > 0) { // 只处理非透明像素
                final int xPos = (int) horizontalAlignment.getOffset(x + drawX, textDimensions[rotate90 ? 1 : 0]);
                final int yPos = (int) verticalAlignment.getOffset(y + drawY, textDimensions[rotate90 ? 0 : 1]);

                if (Utilities.isBetween(xPos, 0, nativeImage.getWidth() - 1) &&
                        Utilities.isBetween(yPos, 0, nativeImage.getHeight() - 1)) {

                    final int existingPixel = nativeImage.getColor(xPos, yPos);
                    final int existingA = (existingPixel >> 24) & 0xFF;

                    if (existingA == 0) {
                        // 背景完全透明，直接设置颜色
                        final int newColor = (alpha << 24) | (textB << 16) | (textG << 8) | textR;
                        nativeImage.setPixelColor(xPos, yPos, newColor);
                    } else {
                        // 需要混合时使用优化后的混合算法
                        blendPixel(nativeImage, xPos, yPos, alpha, textR, textG, textB);
                    }
                }
            }

            // 更新坐标
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

        // 使用整数运算替代浮点运算
        final int invAlpha = 255 - alpha;
        final int newR = (existingR * invAlpha + r * alpha) / 255;
        final int newG = (existingG * invAlpha + g * alpha) / 255;
        final int newB = (existingB * invAlpha + b * alpha) / 255;
        final int newA = Math.min(255, existingA + alpha);

        final int finalColor = (newA << 24) | (newB << 16) | (newG << 8) | newR;
        nativeImage.setPixelColor(x, y, finalColor);
    }

    // 只保留每个id下的两个originalText缓存
    private void putLastResource(String id, String originalText, DynamicResource resource) {
        Object2ObjectLinkedOpenHashMap<String, DynamicResource> pool =
                lastResourceById.computeIfAbsent(id, k -> new Object2ObjectLinkedOpenHashMap<>());
        if (pool.size() > 2) {
            // 移除最老的一个，但在移除前可以不显式销毁，交给 tick 的 expiryTime 处理
            pool.removeFirst();
        }
        pool.put(originalText, resource);
    }

    public enum DefaultRenderingColor {
        BLACK(DEFAULT_BLACK_RESOURCE),
        WHITE(DEFAULT_WHITE_RESOURCE),


        TRANSPARENT(DEFAULT_TRANSPARENT_RESOURCE);

        final DynamicResource dynamicResource;

        DefaultRenderingColor(Identifier identifier) {
            dynamicResource = new DynamicResource(identifier, null);
        }
    }
}
