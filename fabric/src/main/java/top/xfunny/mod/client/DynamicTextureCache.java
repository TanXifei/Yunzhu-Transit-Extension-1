package top.xfunny.mod.client;

import org.mtr.core.servlet.MessageQueue;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2LongArrayMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.mtr.mapping.holder.*;
import org.mtr.mod.render.MainRenderer;
import top.xfunny.mod.Init;
import top.xfunny.mod.client.client_data.DynamicResource;
import top.xfunny.mod.client.client_data.DynamicResourceTextCache;

import java.util.function.Supplier;

//改进前数据： dt静默:size = 183 dt峰值：543；rbi：182
public class DynamicTextureCache {
    public static DynamicTextureCache instance = new DynamicTextureCache();

    private static final int COOLDOWN_TIME = 10000;// 10s
     private static final Identifier DEFAULT_TRANSPARENT_RESOURCE = new Identifier(Init.MOD_ID, "textures/block/transparent.png");

    // 缓存key结构：名称-id（必须包含昵称$文本），DynamicResource]
    private final Object2ObjectLinkedOpenHashMap<String, DynamicResource> dynamicResources = new Object2ObjectLinkedOpenHashMap<>();

    // 待删除队列
    private final Object2LongArrayMap<Identifier> deletedResources = new Object2LongArrayMap<>();

    // 正在生成的队列
    private final ObjectOpenHashSet<String> generatingResources = new ObjectOpenHashSet<>();

    // 资源注册队列
    private final MessageQueue<Runnable> resourceRegistryQueue = new MessageQueue<>();

    public DynamicResource getResource (String id, Long location, Supplier<NativeImage> supplier) {
        // id: 元件名称
        resourceRegistryQueue.process(Runnable::run);

        //获取资源

        // 如果缓存中有，则返回，并延长冷却时间
        final DynamicResource dynamicResource = dynamicResources.get(id);
        if (dynamicResource != null && !dynamicResource.needsRefresh) {
            dynamicResource.expiryTime = System.currentTimeMillis() + COOLDOWN_TIME;
            Init.LOGGER.info("返回"+ id);
            return dynamicResource;
        }

        // 如果目标贴图正在生成中，则返回上一个已经生成好的贴图
        if(generatingResources.contains(id)){
            return getPreviousResource(id, location);
        }

        if(generatingResources.size()<=100){
            // 如果上述情况都没有，则生成新贴图并注册
            registerResource(supplier, id);
        }


        //异步注册中，查询老贴图并返回
        if (dynamicResource == null) {
            return getPreviousResource(id, location);
        } else {
            Init.LOGGER.info("返回"+ id);
            dynamicResource.expiryTime = System.currentTimeMillis() + COOLDOWN_TIME;
            dynamicResource.needsRefresh = false;
            return dynamicResource;
        }
    }


    private void registerResource(Supplier<NativeImage> supplier, String id) {
        generatingResources.add(id);
        MainRenderer.WORKER_THREAD.scheduleDynamicTextures(() -> {// 异步生成贴图并注册
            final NativeImage nativeImage = supplier.get();

            // 注册贴图
            resourceRegistryQueue.put(() -> {
                // 如果缓存中有同名的贴图，则删除，防止对象被覆盖后失去引用，造成缓存对象无法释放
                final DynamicResource staticTextureProviderOld = dynamicResources.get(id);
                if (staticTextureProviderOld != null) {
                    staticTextureProviderOld.remove();
                    deletedResources.put(staticTextureProviderOld.identifier, System.currentTimeMillis() + COOLDOWN_TIME);
                }

                if (nativeImage != null) {// 如果贴图生成成功，则注册
                    final NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(nativeImage);
                    final Identifier identifier = new Identifier(Init.MOD_ID, "id_" + org.mtr.mod.Init.randomString());
                    MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, new AbstractTexture(nativeImageBackedTexture.data));
                    final DynamicResource dynamicResourceNew = new DynamicResource(identifier, nativeImageBackedTexture);
                    dynamicResources.put(id, dynamicResourceNew);
                }

                generatingResources.remove(id);
            });
        });
    }

    public DynamicResource getPreviousResource(String id, Long location) {
        String name = id.split("\\$")[0];
        String lsstId = name + "$" + DynamicResourceTextCache.instance.getPreviousText(location);
        final DynamicResource previousDynamicResource = dynamicResources.get(lsstId);
        if(previousDynamicResource != null && !previousDynamicResource.needsRefresh){
            previousDynamicResource.expiryTime = System.currentTimeMillis() + COOLDOWN_TIME;
            Init.LOGGER.info("返回"+ lsstId);
            return previousDynamicResource;
        }else {
            Init.LOGGER.info("返回空白");
            return DefaultRenderingColor.TRANSPARENT.dynamicResource;
        }
    }

    public void reload(){
        dynamicResources.values().forEach(dynamicResource -> dynamicResource.needsRefresh = true);
        generatingResources.clear();
    }

    public void tick() {
        final ObjectArrayList<String> keysToRemove = new ObjectArrayList<>();
        dynamicResources.forEach((checkKey, checkDynamicResource) -> {
            if (checkDynamicResource.expiryTime < System.currentTimeMillis()) {
                checkDynamicResource.remove();
                deletedResources.put(checkDynamicResource.identifier, System.currentTimeMillis() + COOLDOWN_TIME);
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

    private enum DefaultRenderingColor {
        TRANSPARENT(DEFAULT_TRANSPARENT_RESOURCE);

        private final DynamicResource dynamicResource;

        DefaultRenderingColor(Identifier identifier) {
            dynamicResource = new DynamicResource(identifier, null);
        }
    }

}
