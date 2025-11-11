package top.xfunny.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.xfunny.mod.client.DynamicTextureCache;

@Mixin(org.mtr.mod.client.DynamicTextureCache.class)
public abstract class MixinDynamicTextureCache {
    @Inject(at = @At("TAIL"),
            method = "<init>",
            remap = false)
    private void afterConstruct(CallbackInfo callback) {
        DynamicTextureCache.instance = new DynamicTextureCache();
    }

    @Inject(at = @At("TAIL"),
            method = "tick",
            remap = false)
    private void afterTick(CallbackInfo ci) {
        DynamicTextureCache.instance.tick();
    }

}
