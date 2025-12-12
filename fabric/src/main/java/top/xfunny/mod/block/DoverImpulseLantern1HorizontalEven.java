package top.xfunny.mod.block;

import org.jetbrains.annotations.NotNull;
import org.mtr.core.data.Lift;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.mod.BlockEntityTypes;
import top.xfunny.mod.block.base.LiftButtonsBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class DoverImpulseLantern1HorizontalEven extends LiftButtonsBase {
    public DoverImpulseLantern1HorizontalEven() {
        super(false, false);
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (IBlock.getStatePropertySafe(state, SIDE)) {
            case LEFT:
                return IBlock.getVoxelShapeByDirection(13.75, 7, 0, 16, 15.5, 0.1, IBlock.getStatePropertySafe(state, FACING)); // Alignment TBD

            case RIGHT:
                return IBlock.getVoxelShapeByDirection(0, 7, 0, 2.25, 15.5, 0.1, IBlock.getStatePropertySafe(state, FACING)); // Alignment TBD

        }
        return VoxelShapes.empty();
    }

    @Nonnull
    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DoverImpulseLantern1HorizontalEven.BlockEntity(blockPos, blockState);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(SIDE);
    }

    public void addTooltips(@NotNull ItemStack stack, @Nullable BlockView world, List<MutableText> tooltip, @NotNull TooltipContext options) {
        tooltip.add(TextHelper.translatable("tooltip.warning_block_testing").formatted(TextFormatting.RED));
    }

    public static class BlockEntity extends LiftButtonsBase.BlockEntityBase {
        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.DOVER_IMPULSE_LANTERN_1_HORIZONTAL_EVEN.get(), pos, state);
        }
    }
}
