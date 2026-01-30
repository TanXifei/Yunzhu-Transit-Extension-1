package top.xfunny.mod.client.client_data;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.NativeImage;
import org.mtr.mapping.holder.NativeImageBackedTexture;
import org.mtr.mod.render.MainRenderer;
import top.xfunny.mod.Init;
import top.xfunny.mod.client.DynamicTextureCache;

import javax.annotation.Nullable;

public class DynamicResource {
    public final int width;
    public final int height;
    public final Identifier identifier;
    public long expiryTime;
    public boolean needsRefresh;

    public DynamicResource(Identifier identifier, @Nullable NativeImageBackedTexture nativeImageBackedTexture) {
        this.identifier = identifier;
        if (nativeImageBackedTexture != null) {
            final NativeImage nativeImage = nativeImageBackedTexture.getImage();
            if (nativeImage != null) {
                width = nativeImage.getWidth();
                height = nativeImage.getHeight();
            } else {
                width = 16;
                height = 16;
            }
        } else {
            width = 16;
            height = 16;
        }
    }


    public void remove() {
        Init.LOGGER.info("删除: " + identifier);
        MinecraftClient.getInstance().getTextureManager().destroyTexture(identifier);
        MainRenderer.cancelRender(identifier);
    }
}
