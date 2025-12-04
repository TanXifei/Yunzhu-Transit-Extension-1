package top.xfunny.mod.client.render;

import org.apache.commons.lang3.NotImplementedException;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.data.IGui;
import top.xfunny.mod.block.base.LiftButtonsBase;

public class RenderDoverImpulseLantern1Horizontal<T extends LiftButtonsBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private final boolean isOdd;
    public RenderDoverImpulseLantern1Horizontal(Argument dispatcher, Boolean isOdd) {
        super(dispatcher);
        this.isOdd = isOdd;
    }

    @Override
    public void render(T t, float v, GraphicsHolder graphicsHolder, int i, int i1) {
        throw new NotImplementedException();
    }
}
