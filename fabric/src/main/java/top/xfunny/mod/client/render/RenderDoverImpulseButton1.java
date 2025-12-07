package top.xfunny.mod.client.render;

import org.apache.commons.lang3.NotImplementedException;
import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftDirection;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.PlayerHelper;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.block.DoverImpulseButton1;
import top.xfunny.mod.block.HitachiB85Button1;
import top.xfunny.mod.block.HitachiButtonPAFC;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.keymapping.DefaultButtonsKeyMapping;
import top.xfunny.mod.util.ReverseRendering;

import java.util.Comparator;

// TODO: Dover Impulse Button 1
public class RenderDoverImpulseButton1 extends BlockEntityRenderer<DoverImpulseButton1.BlockEntity> implements DirectionHelper, IGui, IBlock {
    private static final int HOVER_COLOR = 0xFFCCAA44;
    private static final int PRESSED_COLOR = 0xFFFFCC66;
    private static final int DEFAULT_COLOR = 0xFF452D15;
    private static final Identifier ARROW_TEXTURE = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/.png");
    private static final Identifier BUTTON_TEXTURE = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/.png");
    private static final Identifier BUTTON_LIGHT_TEXTURE = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/.png");
    private static final Identifier SCREEN_BACKGROUND_TEXTURE = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/.png");

    public RenderDoverImpulseButton1(Argument dispatcher) {super(dispatcher);}
    @Override
    public void render(DoverImpulseButton1.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
        throw new NotImplementedException();
    }
}
