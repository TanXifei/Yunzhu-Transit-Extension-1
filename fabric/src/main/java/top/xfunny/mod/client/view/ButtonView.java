package top.xfunny.mod.client.view;

import org.mtr.mapping.holder.*;
import org.mtr.mod.Init;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.render.QueuedRenderLayer;
import top.xfunny.mod.keymapping.DefaultButtonsKeyMapping;
import top.xfunny.mod.packet.PacketLanternSoundInstruction;
import top.xfunny.mod.client.InitClient;
import top.xfunny.mod.util.TransformPositionX;

import static org.mtr.mapping.mapper.DirectionHelper.FACING;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ButtonView extends ImageView {

    private int hoverColor;
    private int pressedColor;
    private int defaultColor;
    private boolean isFocused;
    private boolean isPressed;
    private boolean isAlwaysOn;
    private DefaultButtonsKeyMapping keyMapping;
    private float[] location, dimension;
    private float[] uv;

    private static final Map<String, Boolean> LAST_ACTIVE_MAP = new ConcurrentHashMap<>();

    private String lanternSoundInstruction = null;

    public ButtonView() {
        location = new float[2];
        dimension = new float[2];
        uv = new float[]{1, 1, 0, 0};
    }

    public void setBasicsAttributes(World world, BlockPos blockPos, DefaultButtonsKeyMapping keyMapping) {
        this.keyMapping = keyMapping;
        super.setBasicsAttributes(world, blockPos);
    }

    public void setBasicsAttributes(World world, BlockPos blockPos) {
        super.setBasicsAttributes(world, blockPos);
    }

    @Override
    public void render() {
        location[0] = x;
        location[1] = y;
        dimension[0] = width;
        dimension[1] = height;

        final HitResult hitResult = MinecraftClient.getInstance().getCrosshairTargetMapped();
        if (hitResult != null && keyMapping != null) {
            keyMapping.registerButton(id, location, dimension);
            BlockState blockState = world.getBlockState(blockPos);
            Direction facing = IBlock.getStatePropertySafe(blockState, FACING);

            final Vector3d hitLocation = hitResult.getPos();
            final String inButton = keyMapping.mapping(
                    TransformPositionX.transform(
                            MathHelper.fractionalPart(hitLocation.getXMapped()),
                            MathHelper.fractionalPart(hitLocation.getZMapped()),
                            facing
                    ),
                    MathHelper.fractionalPart(hitLocation.getYMapped())
            );
            final boolean inBlock = Init.newBlockPos(
                    hitLocation.getXMapped(),
                    hitLocation.getYMapped(),
                    hitLocation.getZMapped()
            ).equals(blockPos);

            isFocused = inBlock && inButton.equals(id);
        }

        if (isFocused || isPressed || isAlwaysOn) {
            setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);
        }

        setUv(uv);
        color = isPressed ? pressedColor : isFocused ? hoverColor : defaultColor;
        super.render();
    }

    public void setDefaultColor(int defaultColor, boolean isAlwaysOn) {
        this.defaultColor = defaultColor;
        this.isAlwaysOn = isAlwaysOn;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void setHoverColor(int hoverColor) {
        this.hoverColor = hoverColor;
    }

    public void setPressedColor(int pressedColor) {
        this.pressedColor = pressedColor;
    }

    public void setFlip(boolean flipVertical, boolean flipHorizontal) {
        if (flipVertical) {
            final float tempV = uv[0];
            uv[0] = uv[2];
            uv[2] = tempV;
        }
        if (flipHorizontal) {
            final float tempU = uv[1];
            uv[1] = uv[3];
            uv[3] = tempU;
        }
    }

    public void setLanternSound(String soundInstruction) {
        this.lanternSoundInstruction = soundInstruction;
    }

    private String makeSoundKey() {
        if (world == null || blockPos == null || lanternSoundInstruction == null) return null;
        // 使用方块位置 + 指令构建唯一键
        return blockPos.getX() + "_" + blockPos.getY() + "_" + blockPos.getZ() + "_" + lanternSoundInstruction;
    }

    public void activate() {
        isPressed = true;

        if (lanternSoundInstruction == null || world == null || blockPos == null) {
            return;
        }

        final String key = makeSoundKey();
        if (key == null) return;

        // 避免重复声音（跨实例/跨帧）
        Boolean already = LAST_ACTIVE_MAP.get(key);
        if (already != null && already) return;

        InitClient.REGISTRY_CLIENT.sendPacketToServer(
                new PacketLanternSoundInstruction(blockPos, lanternSoundInstruction)
        );

        LAST_ACTIVE_MAP.put(key, true);
    }

    public void resetLanternSound() {
        isPressed = false;
        final String key = makeSoundKey();
        if (key == null) return;
        LAST_ACTIVE_MAP.put(key, false);
    }
}
