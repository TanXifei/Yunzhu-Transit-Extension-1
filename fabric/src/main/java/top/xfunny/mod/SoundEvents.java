package top.xfunny.mod;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.registry.SoundEventRegistryObject;


public class SoundEvents {
    public static final SoundEventRegistryObject OTIS_SERIES_1_LANTERN_1_UP;
    public static final SoundEventRegistryObject OTIS_SERIES_1_LANTERN_1_DOWN;

    static {
        OTIS_SERIES_1_LANTERN_1_UP = Init.REGISTRY.registerSoundEvent(new Identifier(Init.MOD_ID, "otis_series_1_lantern_1_up"));
        OTIS_SERIES_1_LANTERN_1_DOWN = Init.REGISTRY.registerSoundEvent(new Identifier(Init.MOD_ID, "otis_series_1_lantern_1_down"));
    }

    public static void init() {
        Init.LOGGER.info("注册声音事件");
    }
}
