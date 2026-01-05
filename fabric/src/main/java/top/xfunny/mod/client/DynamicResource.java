package top.xfunny.mod.client;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.NativeImage;
import org.mtr.mapping.holder.NativeImageBackedTexture;
import org.mtr.mod.render.MainRenderer;

import javax.annotation.Nullable;

public class DynamicResource {
    public final int width;
    public final int height;
    public final Identifier identifier;
    long expiryTime;
    boolean needsRefresh;

    DynamicResource(Identifier identifier, @Nullable NativeImageBackedTexture nativeImageBackedTexture) {
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


    void remove() {
        MinecraftClient.getInstance().getTextureManager().destroyTexture(identifier);
        MainRenderer.cancelRender(identifier);
    }
}
